package syntax.generator;

import utils.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/7/1.
 */
public class Generator {
    public static int CHILD_NUM = 6;
    //���ս��
    public Word nonTerminalWord;
    public List<List<Word>> firstWordList;//ÿ����ѡʽ��First����
    public List<Word> followWordList;//Follow����
    public List<List<Word>> childList;//��ѡʽ����

    public Generator() {
        childList = new ArrayList<List<Word>>();
        firstWordList = new ArrayList<List<Word>>();
        followWordList = new ArrayList<Word>();
        for(int i=0;i<CHILD_NUM;i++){
            List<Word>words = new ArrayList<Word>();
            firstWordList.add(words);
            words = new ArrayList<Word>();
            childList.add(words);
        }
    }

    /**
     * ��ȡ����ʽ��˵ķ��ս��
     * @return
     */
    public Word getNonTerminalWord(){
        return this.nonTerminalWord;
    }

    /**
     * ��ȡ��ǰ����ʽ��˽ڵ����������з��ս��
     * @param nonTerminalWord
     */
    public void setNonTerminalWord(Word nonTerminalWord) {
        this.nonTerminalWord = nonTerminalWord;
    }

    /**
     * ���First����
     * @param i
     * @param word
     */
    public void addFirstWordList(int i, Word word) {
        firstWordList.get(i).add(word);
    }

    /**
     * ��Ӻ�ѡʽ����
     * @param i
     * @param word
     */
    public void addChildList(int i, Word word) {
        childList.get(i).add(word);
    }

    /**
     * ���Follow����
     * @param word
     */
    public void addFollowWordList(Word word) {
        followWordList.add(word);
    }
}
