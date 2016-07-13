package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * �ո�״̬����
 * @author Hersch
 * 
 */

public class SpaceStateMachine {
    public static final int SPACE_STATE = 48;
	public SpaceStateMachine() {
		initEndState();
	}
	/**
	 * ��ʼ����ֹ״̬�ı�Ǻ���Ч״̬�Ķ�Ӧ������
	 */
	public void initEndState(){
		LexicalScanner.endAttributeArray[48] = "0x102";
		LexicalScanner.endStateArray[48] =1;
	}
	public void changeState(char c){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(c==' '){
				LexicalScanner.currentState = SPACE_STATE;//�ո� end
			}
			break;
		}
	}
}
