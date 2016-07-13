package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class TGenerator extends Generator {
    public TGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_T));
        setChildList();
    }
    /**
     * ���ú�ѡʽ����
     */
    public void setChildList() {
        addChildList(0, new Word(SyntaxAnalyser.NONT_VC));
        addChildList(0, new Word(SyntaxAnalyser.NONT_P));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));//��ʶ��
        addFirstWordList(0, new Word(SyntaxAnalyser.CONST_INTEGER));//����
    }

}
