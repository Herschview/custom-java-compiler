package lexicalscanner.statemachine;

import lexicalscanner.LexicalScanner;
/**
 * ����״̬����
 * @author Hersch
 *
 */

public class LineJudgeMachine {
	public static final int LINE_COUNT_STATE = 117;
    public LineJudgeMachine(){
    	LexicalScanner.endStateArray[LINE_COUNT_STATE] = 1;//��ʼ����Ч״̬�ı�ʶ
    }
    /**
     * ����״̬����״̬�ı�
     * @param ch
     */
	public void changeState(char ch){
		switch (LexicalScanner.currentState) {
		case LexicalScanner.START_STATE:
			if(ch =='\r')
				LexicalScanner.currentState = 116;//\r
			break;
		case 116:
			if(ch=='\n'){
				LexicalScanner.currentState = LINE_COUNT_STATE;//\r\n end 
			}
		break;
		}
	}
}
