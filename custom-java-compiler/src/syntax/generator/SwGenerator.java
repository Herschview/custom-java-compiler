package syntax.generator;

import syntax.SyntaxAnalyser;
import utils.Word;

/**
 * Created by Hersch on 2016/7/1.
 */
public class SwGenerator extends Generator {
    public SwGenerator(){
        setNonTerminalWord(new Word(SyntaxAnalyser.NONT_SW));
        setChildList();
    }
    /**
     * ���ú�ѡʽ����
     */
    public void setChildList() {
        addChildList(0, new Word("while"));
        addChildList(0, new Word("("));
        addChildList(0, new Word(SyntaxAnalyser.NONT_ER));
        addChildList(0, new Word(")"));
        addChildList(0, new Word("{"));
        addChildList(0, new Word(SyntaxAnalyser.NONT_S));
        addChildList(0, new Word("}"));
        addFirstWordList(0, new Word("while"));
    }

}
