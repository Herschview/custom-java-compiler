package utils;

import syntax.SyntaxAnalyser;

/**
 * �����ַ�����
 */
public class Word {
	//��������
    public int line;
	//�����ֵ�����
    public String type;
	//�����������е�˳��ֵ
    public int number;
    //�����ֵ�ֵ
    public String value;
	//�����ִ����״ֵ̬
	public int state;
	public Word(){}
	public Word(String value){
		if(value!= SyntaxAnalyser.ID&&value!= SyntaxAnalyser.CONST_INTEGER){
			type = SyntaxAnalyser.OTHER;
		}
		else{
			type = value;
		}
		this.value = value;
	}
	/**
	 * ��ȡ�����ֵ�״̬
	 * @return
	 */
	public int getState() {
		return state;
	}
	/**
	 * ���������ֵ�״̬
	 * @param state
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * ��ȡ���������ڵ�����
	 * @return
	 */
	public int getLine() {
		return line;
	}
	/**
	 * ���������ֵ��к�
	 * @param line
	 */
	public void setLine(int line) {
		this.line = line;
	}
	/**
	 * ��ȡ�����ֵ�����
	 * @return
	 */
	public String getType() {
		return type;
	}
	/**
	 * ��ȡ�����ֵ�����
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * ��ȡ���������������е�˳������ֵ
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * �������������������е�˳������ֵ
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * ��ȡ�����ֵ�ֵ
	 * @return
	 */
	public String getValue() {
		return value;
	}
	/**
	 * ���������ֵ�ֵ
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
