package syntax;

import utils.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Hersch on 2016/7/4.
 * ��Ԫʽ������
 */
public class GeneratorReduction {
    //���
    public static int LABEL;
    public static final String TEMP="temp";
    //��ʱ����
    public static int RESULT;
    //��תָ��
    public final String JUMP_JF = "JF";
    public final String JUMP_JT = "JT";
    public final String JUMP_J = "J";
    //S�еı������
    public static final int SW_LABEL_BEGIN = 0;
    public static final int SW_LABEL_TRUE = 1;
    public static final int SW_LABEL_FALSE = 2;
    //ȫ�ֱ���Լ��������
    public boolean recurseFlag = false;
    //�����������ȼ�
    public Map<String, Integer> priorityMap;
    //�����ջ
    public Stack<TreeNode> operatorStack;
    //����ջ
    public Stack<TreeNode> numStack;
    //��Ԫʽ����
    public List<GeneratorTable> generatorList;
    //��������ȼ�
    public final int ADD_PRO = 0;
    public final int SUB_PRO = 0;
    public final int MUL_PRO = 1;
    public final int DEVIDE_PRO = 1;
    //�����Ԫʽ�ļ�
    public static final String GenratorFilePath = "generator.txt";

    public GeneratorReduction() {
        initStack();
        initPriority();
        initGeneratorList();
    }

    /**
     * ��ʼ������������ȼ�
     */
    public void initPriority() {
        priorityMap = new HashMap<String, Integer>();
        priorityMap.put("+", ADD_PRO);
        priorityMap.put("-", SUB_PRO);
        priorityMap.put("*", MUL_PRO);
        priorityMap.put("/", DEVIDE_PRO);
    }
    public void initGeneratorList() {
        generatorList = new ArrayList<GeneratorTable>();
    }

    /**
     * ��ʼ������ջ������ջ
     */
    public void initStack() {
        operatorStack = new Stack<TreeNode>();
        numStack = new Stack<TreeNode>();
    }
    public List<GeneratorTable> getGeneratorTable(){
        return this.generatorList;
    }

    /**
     * ����Sw.code��������Ԫʽ
     * @param treeNode
     */
    public void searchGenOfSw(TreeNode treeNode) {
        treeNode.getLabelNumLists().clear();
        treeNode.getLabelNumLists().add(newLabel());
        treeNode.getLabelNumLists().add(newLabel());
        treeNode.getLabelNumLists().add(newLabel());
        searchGenOfEr(treeNode.getChildNodes().get(2));//Er
        searchGenOfS(treeNode.getChildNodes().get(5));//S
        reduceGotoGen(treeNode);
        reduceNextLabel(treeNode);
    }

    /**
     * ����Sw.next����Ԫʽ
     * @param treeNode
     */
    public void reduceNextLabel(TreeNode treeNode) {
        GeneratorTable generatorTable = new GeneratorTable();
        generatorTable.addItem(0, new TreeNode("(" + treeNode.getLabelNumLists().get(SW_LABEL_FALSE) + ")"));
        generatorTable.addItem(1, new TreeNode(""));
        generatorTable.addItem(2, new TreeNode(""));
        generatorTable.addItem(3, new TreeNode(""));
        generatorTable.addItem(4, new TreeNode(""));
        generatorList.add(generatorTable);
    }

    /**
     * ����goto������Ԫʽ
     * @param treeNode
     */
    public void reduceGotoGen(TreeNode treeNode) {
        GeneratorTable generatorTable = new GeneratorTable();
        generatorTable.addItem(0, new TreeNode("(" + newLabel() + ")"));
        generatorTable.addItem(1, new TreeNode(JUMP_J));
        generatorTable.addItem(2, new TreeNode(""));
        generatorTable.addItem(3, new TreeNode(""));
        generatorTable.addItem(4, new TreeNode("(" + treeNode.getLabelNumLists().get(SW_LABEL_BEGIN) + ")"));
        generatorList.add(generatorTable);
    }

    /**
     * ����Er.code��������Ԫʽ
     * @param treeNode
     */
    public void searchGenOfEr(TreeNode treeNode) {
        //Er->VcC (>,a,30,T0) (jT, , , label)
        List<TreeNode> nodes = treeNode.getTermialWordsLists();
        GeneratorTable generatorTable = new GeneratorTable();
        generatorTable.addItem(0, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_BEGIN) + ")"));
        generatorTable.addItem(1, nodes.get(1));
        generatorTable.addItem(2, nodes.get(0));
        generatorTable.addItem(3, nodes.get(2));
        TreeNode node;
        generatorTable.addItem(4, node = new TreeNode());
        int result = newResult();
        node.setValue(SyntaxAnalyser.NONT_T + result);
        node.setType(TEMP);
        generatorList.add(generatorTable);

        GeneratorTable generatorJTable = new GeneratorTable();
        generatorJTable.addItem(0, new TreeNode("(" + newLabel() + ")"));
        generatorJTable.addItem(1, new TreeNode(JUMP_JT));
        generatorJTable.addItem(2, new TreeNode(""));
        generatorJTable.addItem(3, new TreeNode(""));
        generatorJTable.addItem(4, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_TRUE).toString() + ")"));
        generatorList.add(generatorJTable);

        GeneratorTable generatorFTable = new GeneratorTable();
        generatorFTable.addItem(0, new TreeNode("(" + newLabel() + ")"));
        generatorFTable.addItem(1, new TreeNode(JUMP_JF));
        generatorFTable.addItem(2, new TreeNode(""));
        generatorFTable.addItem(3, new TreeNode(""));
        generatorFTable.addItem(4, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_FALSE).toString() + ")"));
        generatorList.add(generatorFTable);
    }

    /**
     * ����S.code��������Ԫʽ
     * @param treeNode
     */
    public void searchGenOfS(TreeNode treeNode) {
        if (treeNode.getChildNodes().size() == 5) {
            TreeNode finalNode = treeNode.getChildNodes().get(0);
            TreeNode equalNode = treeNode.getChildNodes().get(1);
            //��ȡEx���������ս��
            for (TreeNode childNode : treeNode.getChildNodes().get(2).getTermialWordsLists()) {
                if (childNode.getType().equals(SyntaxAnalyser.CONST_INTEGER) || childNode.getType().equals(SyntaxAnalyser.ID)) {
                    numStack.push(childNode);
                } else {
                    if (operatorStack.isEmpty() || priorityMap.get(operatorStack.lastElement().getValue()) < priorityMap.get(childNode.getValue())) {
//                        operatorStack.push(childNode.getValue());
                        operatorStack.push(childNode);
                    } else {
                        //���е�ջ����
                        GeneratorTable generator = new GeneratorTable();
                        if (!recurseFlag) {
                            generator.addItem(0, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_TRUE) + ")"));
                            recurseFlag = true;
                        } else {
                            generator.addItem(0, new TreeNode("(" + newLabel() + ")"));
                        }
                        generator.addItem(1, operatorStack.pop());
                        TreeNode nodeB = numStack.pop();
                        TreeNode nodeA = numStack.pop();
                        generator.addItem(2, nodeA);
                        generator.addItem(3, nodeB);
                        int result = newResult();
                        TreeNode node;
                        generator.addItem(4, node = new TreeNode());
                        node.setValue(SyntaxAnalyser.NONT_T + result);
                        node.setType(TEMP);
                        numStack.push(node);
                        operatorStack.push(childNode);
                        generatorList.add(generator);
                    }
                }
            }
            while (operatorStack.size() > 0) {
                GeneratorTable generator = new GeneratorTable();
                if (!recurseFlag) {
                    generator.addItem(0, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_TRUE) + ")"));
                    recurseFlag = true;
                } else {
                    generator.addItem(0, new TreeNode("(" + newLabel() + ")"));
                }
                generator.addItem(1, operatorStack.pop());
                TreeNode nodeB = numStack.pop();
                TreeNode nodeA = numStack.pop();
                generator.addItem(2, nodeA);
                generator.addItem(3, nodeB);
                int result = newResult();
                TreeNode node;
                generator.addItem(4, node = new TreeNode());
                node.setType(TEMP);
                node.setValue(SyntaxAnalyser.NONT_T + result);
                numStack.push(node);
                generatorList.add(generator);
            }
            //���Ľ����ֵ��finalResult
            GeneratorTable generator = new GeneratorTable();
            if (!recurseFlag) {
                generator.addItem(0, new TreeNode("(" + treeNode.getParentNode().getLabelNumLists().get(SW_LABEL_TRUE) + ")"));
                recurseFlag = true;
            } else {
                generator.addItem(0, new TreeNode("(" + newLabel() + ")"));
            }
            generator.addItem(1, equalNode);
            generator.addItem(2, numStack.pop());
            generator.addItem(3, new TreeNode(""));
            generator.addItem(4, finalNode);
            generatorList.add(generator);
            //�ݹ����
            searchGenOfS(treeNode.getChildNodes().get(4));
        }
    }

    /**
     * ���������Ԫʽ
     */
    public void outputGenerator() {
        File generatorFile = new File(GenratorFilePath);
        if(!generatorFile.exists()){
            try {
                generatorFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileWriter generatorWriter = null;
        try {
            generatorWriter = new FileWriter(generatorFile);
            for (GeneratorTable generatorTable : generatorList) {
                generatorWriter.write(generatorTable.getTable().get(0).getValue() + "(" +
                        generatorTable.getTable().get(1).getValue() + "," +
                        generatorTable.getTable().get(2).getValue() + "," +
                        generatorTable.getTable().get(3).getValue() + "," +
                        generatorTable.getTable().get(4).getValue() + ")");
                generatorWriter.write("\n");
            }
            generatorWriter.flush();
            generatorWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �����µı�ǩ
     * @return
     */
    public int newLabel() {
        return ++LABEL;
    }

    /**
     * �����µ���ʱ�������
     * @return
     */
    public int newResult() {
        return ++RESULT;
    }
}
