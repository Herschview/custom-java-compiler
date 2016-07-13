package syntax;

import syntax.generator.*;
import lexicalscanner.LexicalScanner;
import syntax.masm.Masm;
import utils.TreeNode;
import utils.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * �﷨��������
 */
public class SyntaxAnalyser {
    //�ս������
    public static final String NONT_S = "S";
    public static final String NONT_SW = "Sw";
    public static final String NONT_ER ="Er";
    public static final String NONT_VC = "Vc";
    public static final String NONT_T = "T";
    public static final String NONT_F = "F";
    public static final String NONT_Ex = "Ex";
    public static final String NONT_P = "P";
    public static final String NONT_C = "C";
    //��ʶ��0x104   ����0x107
    public static final String CONST_INTEGER = "const";
    public static final String ID = "id";
    public static final String OTHER = "other";
    //����ʽ����
    public static List<Generator> generators;
    //�﷨����ջ
    public Stack<TreeNode> analyserStack;
    //���������ַ���
    public List<Word> remainList;
    //���ڵ�
    public TreeNode rootNode;
    //��ǰ�﷨�ڵ�
    public TreeNode currentNode;

    public SyntaxAnalyser() {
        //���ò���ʽ����
        setGenerators();
        analyserStack = getInitAnalyserStack();
        remainList = getRemainList(LexicalScanner.wordList);
        run();
        //���¶��ϻ���ۺ�����
        getAttrFromChild(rootNode);
        //�������巭���ӳ���
        new SyntaxTranslation(rootNode);
        //������Ԫʽ
        GeneratorReduction generatorReduction = new GeneratorReduction();
        generatorReduction.searchGenOfSw(rootNode);
        generatorReduction.outputGenerator();
        //���ɻ��
        new Masm(generatorReduction.getGeneratorTable());
    }

    /**
     * �����﷨����ȡ�ۺ�����
     * @param treeNode
     */
    public void getAttrFromChild(TreeNode treeNode){
        if(treeNode.getChildNodes().size()==0){
            treeNode.setTerminalWordsLists(treeNode);
            return;
        }
        for(TreeNode node:treeNode.getChildNodes()){
            getAttrFromChild(node);
            for(TreeNode terminalNode:node.getTermialWordsLists()){
                if(!terminalNode.value.equals("")) {
                    treeNode.setTerminalWordsLists(terminalNode);
                }
            }
        }
    }

    /**
     * �﷨����������
     */
    public void run() {
        rootNode = analyserStack.lastElement();
        while (analyserStack.size() > 1) {
            String aStr = analyserStack.lastElement().getValue();
            String rStr = remainList.get(0).getValue();
            String type = remainList.get(0).getType();
            currentNode = analyserStack.lastElement();
            //First����Ϊ���������Ǳ�����ƥ�����༴��
            if (aStr.equals(type) || aStr.equals(rStr)) {
                //�����ս����ֵ������Ӧ�﷨������ֵ
                currentNode.setValue(rStr);
                currentNode.setType(remainList.get(0).getType());
                currentNode.setNumber(remainList.get(0).getNumber());
                currentNode.setLine(remainList.get(0).getLine());
                analyserStack.pop();
                remainList.remove(0);
            } else {
                reductGenerator();//�Ƶ�
            }
        }
        return;
    }

    /**
     * ��������ջ�ͷ���ջ���Ƶ��Լ��ƽ��Ƴ�
     */
    public void reductGenerator() {
        int generatorIndex = searchGenerator();
        searchFirstWord(generatorIndex);
    }

    /**
     * ���ҵ�ǰ����ջ���˷��ս����Ӧ�Ĳ���ʽ������ֵ
     *
     * @return
     */
    public int searchGenerator() {
        int index = 0;
        for (int i = 0; i < generators.size(); i++) {
            Word currentNonTerminalWord = analyserStack.lastElement();
            if (generators.get(i).getNonTerminalWord().getValue().equals(currentNonTerminalWord.getValue())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * ���Ƶ�����ʽ�ƽ�����ջ
     *
     * @param words
     */
    public void intoAnalyserStack(List<Word> words) {
        //����ʽ��������
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            TreeNode treeNode = new TreeNode(word.getValue());
            treeNode.setParentNode(currentNode);
            currentNode.addChildNode(treeNode);
        }
        //����ʽ��ջ
        for (int i = currentNode.getChildNodes().size() - 1; i >= 0; i--) {
            analyserStack.add(currentNode.getChildNodes().get(i));
        }
    }

    /**
     * ������ǰƥ���ս�����ڵĺ�ѡʽ����
     *
     * @param index
     */
    public void searchFirstWord(int index) {
        //���ҵ���Ӧ���ս���Ĳ���ʽ����
        int num = 0;
        Generator generator = generators.get(index);
        for (int i = 0; i < generator.firstWordList.size(); i++) {
            List<Word> list = generator.firstWordList.get(i);
            for (int j = 0; j < list.size(); j++) {
                //�������ֺͱ�ʶ����Ҫ�������ж�
                String value = remainList.get(0).getValue();
                String type = remainList.get(0).getType();
                if (value.equals(list.get(j).getValue()) || type.equals(list.get(j).getValue())) {
                    //���Ƴ�����ջ�Ķ����ķ��ս��
                    analyserStack.pop();
                    intoAnalyserStack(generator.childList.get(i));
                    return;
                }
            }
        }
        //��ѡʽ���ڿռ��鿴follow����
        for (Word word : generator.followWordList) {
            if (word.getValue().equals(remainList.get(0).getType()) ||
                    word.getValue().equals(remainList.get(0).getValue())) {
                analyserStack.pop();
                TreeNode treeNode = new TreeNode("");
                currentNode.addChildNode(treeNode);
                return;
            }
        }
    }


    /**
     * ��ʼ��Ԥ��ջ
     *
     * @param list
     * @return
     */
    public List<Word> getRemainList(List<Word> list) {
        List<Word>wordList = new ArrayList<Word>();
        for (Word word :list) {
            transferType(word);
            wordList.add(word);
        }
        return wordList;
    }

    /**
     * ��ʼ������ջ
     * @return
     */
    public Stack<TreeNode> getInitAnalyserStack() {
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(new TreeNode("#"));
        stack.push(new TreeNode(NONT_SW));
        return stack;
    }

    /**
     * ��ʼ������ʽ����
     */
    public void setGenerators() {
        if (generators == null) {
            generators = new ArrayList<Generator>();
        }
        generators.add(new SwGenerator());
        generators.add(new SGenerator());
        generators.add(new ErGenerator());
        generators.add(new CGenerator());
        generators.add(new VcGeneratr());
        generators.add(new ExGenerator());
        generators.add(new TGenerator());
        generators.add(new FGenerator());
        generators.add(new PGenerator());
    }

    /**
     * ���ʷ������б�ĵ�������
     * @param word
     */
    public void transferType(Word word) {
        if (word.getType().equals("0x104")) {
            word.setType(ID);
        } else if (word.getType().equals("0x107")) {
            word.setType(CONST_INTEGER);
        } else {
            word.setType(OTHER);
        }
    }
}
