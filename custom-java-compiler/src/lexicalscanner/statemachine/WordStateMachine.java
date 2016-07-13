package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * ��ʶ��״̬����
 * @author Hersch
 *
 */

public class WordStateMachine {
    public static final int WORD_STATE =38;
	public WordStateMachine() {
		initKeyWordList();
		initEndState();
	}
	/**
	 * ��ʼ����ֹ״̬�ı�Ǻ���Ч״̬�Ķ�Ӧ������
	 */
	public void initEndState(){
		LexicalScanner.endAttributeArray[38] = "0x104";//��ʶ��
	}
	/**
	 * ��ʼ���ؼ�������
	 */
	public void initKeyWordList(){
		//50���ؼ���
		LexicalScanner.keyWordList.add("abstract");
		LexicalScanner.keyWordList.add("boolean");
		LexicalScanner.keyWordList.add("break");
		LexicalScanner.keyWordList.add("byte");
		LexicalScanner.keyWordList.add("case");
		LexicalScanner.keyWordList.add("catch");
		LexicalScanner.keyWordList.add("char");
		LexicalScanner.keyWordList.add("class");

		LexicalScanner.keyWordList.add("const");
		LexicalScanner.keyWordList.add("continue");
		LexicalScanner.keyWordList.add("default");
		LexicalScanner.keyWordList.add("do");
		LexicalScanner.keyWordList.add("double");
		LexicalScanner.keyWordList.add("else");
		LexicalScanner.keyWordList.add("extends");
		LexicalScanner.keyWordList.add("false");

		LexicalScanner.keyWordList.add("final");
		LexicalScanner.keyWordList.add("finally");
		LexicalScanner.keyWordList.add("float");
		LexicalScanner.keyWordList.add("for");
		LexicalScanner.keyWordList.add("goto");
		LexicalScanner.keyWordList.add("if");
		LexicalScanner.keyWordList.add("implements");
		LexicalScanner.keyWordList.add("import");

		LexicalScanner.keyWordList.add("instanceof");
		LexicalScanner.keyWordList.add("int");
		LexicalScanner.keyWordList.add("interface");
		LexicalScanner.keyWordList.add("long");
		LexicalScanner.keyWordList.add("native");
		LexicalScanner.keyWordList.add("new");
		LexicalScanner.keyWordList.add("null");
		LexicalScanner.keyWordList.add("package");

		LexicalScanner.keyWordList.add("private");
		LexicalScanner.keyWordList.add("protected");
		LexicalScanner.keyWordList.add("public");
		LexicalScanner.keyWordList.add("return");
		LexicalScanner.keyWordList.add("short");
		LexicalScanner.keyWordList.add("static");
		LexicalScanner.keyWordList.add("super");
		LexicalScanner.keyWordList.add("switch");

		LexicalScanner.keyWordList.add("synchronized");
		LexicalScanner.keyWordList.add("this");
		LexicalScanner.keyWordList.add("throw");
		LexicalScanner.keyWordList.add("throws");
		LexicalScanner.keyWordList.add("transient");
		LexicalScanner.keyWordList.add("true");
		LexicalScanner.keyWordList.add("try");
		LexicalScanner.keyWordList.add("void");

		LexicalScanner.keyWordList.add("volatile");
		LexicalScanner.keyWordList.add("while");
		System.out.println(LexicalScanner.keyWordList.size());
	}
    /**
     * ����״̬��״̬
     * @param c
     */
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(LexicalScanner.isWord(c)||c=='$'||c=='_'){
				LexicalScanner.currentState = WORD_STATE;//$||_||��ĸ
			}
			else{
				LexicalScanner.currentState = LexicalScanner.START_STATE;
			}
			break;
		case WORD_STATE:
			if(!LexicalScanner.isWord(c)&&!LexicalScanner.isNumFirst(c)&&c!='$'&&c!='_'){
				LexicalScanner.currentState = LexicalScanner.ILLEGAL_STATE;//��ֹ״̬��Ҫ���˵�ǰ����������ַ�
			}
			break;
		}
	}
}
