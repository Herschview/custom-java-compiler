package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class ExGenerator extends Generator {
    public ExGenerator() {
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_Ex));
        setChildList();
    }

    /**
     * ���ú�ѡʽ����
     */
    public void setChildList() {
        addChildList(0, new Word(SyntaxAnalyser.NONT_T));
        addChildList(0, new Word(SyntaxAnalyser.NONT_F));
        addFirstWordList(0, new Word(SyntaxAnalyser.ID));
        addFirstWordList(0, new Word(SyntaxAnalyser.CONST_INTEGER));
    }

}

