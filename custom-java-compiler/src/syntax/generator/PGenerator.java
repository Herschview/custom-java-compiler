package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class PGenerator extends Generator {
    //P->*VcP|/VcP|��
    public PGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_P));
        setChildList();
    }

    /**
     * ���ú�ѡʽ����
     */
    public void setChildList() {
        addChildList(0,new Word("*"));
        addChildList(0,new Word(SyntaxAnalyser.NONT_VC));
        addChildList(0,new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(0, new Word("*"));
        //��ÿ����ѡʽ��first���϶������б����һλ
        addChildList(1, new Word("/"));
        addChildList(1, new Word(SyntaxAnalyser.NONT_VC));
        addChildList(1, new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(1, new Word("/"));

        addChildList(2, new Word(""));
        addFollowWordList(new Word("#"));
        addFollowWordList(new Word(";"));
        addFollowWordList(new Word("+"));
        addFollowWordList(new Word("-"));
        //�����ֺ�ѡʽΪ�գ�����FOLLOW(��)����ͳһת����first���ϴ洢��Ϊ�˷���ͳһ
    }
}
