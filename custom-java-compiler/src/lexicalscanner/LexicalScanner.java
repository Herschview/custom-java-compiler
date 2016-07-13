package lexicalscanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lexicalscanner.preprocess.FileFliter;
import lexicalscanner.statemachine.CharStateMachine;
import lexicalscanner.statemachine.LineJudgeMachine;
import lexicalscanner.statemachine.NumStateMachine;
import lexicalscanner.statemachine.OperStateMachine;
import lexicalscanner.statemachine.RegionStateMachine;
import lexicalscanner.statemachine.SpaceStateMachine;
import lexicalscanner.statemachine.StringStateMachine;
import lexicalscanner.statemachine.WordStateMachine;
import utils.Word;

/**
 * 
 * @author Hersch
 *
 */

public class LexicalScanner {
	public static final int BUFSIZE = 512;
	public static int bufSize = BUFSIZE;
	public static final int STATE_NUM = 512;
	public static final String inputPathName = "input.txt";
	public static final String filterPathName = "filter.txt";
	public static final String outputPathName = "output.txt";
	public static final int START_STATE= 0;//��ʼ״̬
	public static final int ILLEGAL_STATE = 99999999;//�����ַ���ƥ�䵫�ǿ���ȷ����һ��״̬Ϊ����״̬
	public static final int ERROR_STATE = 1000001;//����״̬

	private int bufNum = 0;//������������

	public static ArrayList<String> keyWordList = new ArrayList<String>();//��Źؼ���ƥ��
	public static ArrayList<String> operaterList = new ArrayList<String>();//��������
	public static ArrayList<Word> wordList = new ArrayList<Word>();//���ʶ������ĵ���
	public static ArrayList<Word> errorWordList = new ArrayList<Word>();//���ʶ������Ĵ��󵥴�
	public static String []endAttributeArray = new String[STATE_NUM];//��Ч״̬������
	public static int []endStateArray = new int[STATE_NUM];	//��ֹ״̬

	private int lineCount = 1;//�к�
	private int numberCount = 1;//�ܵ��ʸ���
	private int endFileFlag = 0;//�ļ�������־
	private int exceedFlag = 0;//�Ƿ񳬳���ǰ������
	public static int currentState = START_STATE;//��ʼ״̬
	public static int lastWordState = START_STATE;//��һ������״̬
	public static int preState = START_STATE;//��һ��״̬
	char[][] buffer = new char[2][BUFSIZE];//������
	File filterFile;
	File outputFile;
	FileReader filterFileReader;//��ȡ�ļ�
	FileWriter outputFileWriter;//д���ļ�

	CharStateMachine charStateMachine;//�ַ�����״̬��
	OperStateMachine operStateMachine;//�����״̬��
	WordStateMachine wordStateMachine;//��ʶ��״̬��
	RegionStateMachine regionStateMachine;//���޷�״̬��
	StringStateMachine stringStateMachine;//�ַ�������״̬��
	SpaceStateMachine spaceStateMachine;//�ո�״̬��
	NumStateMachine numStateMachine;//��ֵ״̬��
	LineJudgeMachine lineCountMachine;//����״̬��

	public LexicalScanner(){

		initEndState();//��ʼ��״̬����

		new FileFliter(inputPathName);//����ע��

		charStateMachine = new CharStateMachine();
		operStateMachine = new OperStateMachine();
		wordStateMachine = new WordStateMachine();
		stringStateMachine = new StringStateMachine();
		regionStateMachine = new RegionStateMachine();
		spaceStateMachine = new SpaceStateMachine();
		numStateMachine = new NumStateMachine();
		lineCountMachine = new LineJudgeMachine();

		filterFile = new File(filterPathName);
		if(!filterFile.exists()){
			System.out.println("filterFile not exists!\n");
		}
		outputFile = new File(outputPathName);
		if(!outputFile.exists()){
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("outputFile not exist!\n");
		}
		analyzerContent();//�ʷ������ı�
	}
	public void initEndState(){
		for(int i=0;i<endAttributeArray.length;i++){
			endAttributeArray[i] = "";
		}
		for(int i=0;i<endStateArray.length;i++){
			endStateArray[i] = 0;
		}
	}
	public void analyzerContent(){
		int lineNumCount = 0;

		try {
			filterFileReader = new FileReader(filterFile);
			outputFileWriter = new FileWriter(outputFile);
			outputFileWriter.write("<tokens>\r\n");
			addBuffer(bufNum);//��仺����
			int bp = 0;//βָ��
			int hp = 0;//ͷָ��
			//�ļ�δ����
			while(endFileFlag==0){
				//��ǰָ��λ��δ����BUFSIZE
				while(hp<bufSize){
					char c = buffer[bufNum][hp];
					preState = currentState;//��¼��һ�ε�״̬����ILLEGAL����
					//changeState(c);
					transferState(c);
					if(currentState==START_STATE){
						hp++;//�ƶ�ǰָ��
						bp = hp;//��ָ�븳ֵ
					}
					else if(currentState==ILLEGAL_STATE){
						lastWordState = preState;
						currentState = START_STATE;
						String a = getString(bufNum, bp, hp-1);//hp-1����һ���ַ�

						//��ʶ��+�ؼ���+������������ֵΪ38

						if(preState==wordStateMachine.WORD_STATE){
							int flag = 0;
							for(int i =0;i<keyWordList.size();i++){
								if(a.equals(keyWordList.get(i))){
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setState(preState);
									w.setValue(a);
									w.setNumber(numberCount++);
									w.setType("0x103");
									outputWord(w);//�ؼ���������ļ�
									lineNumCount++;//ÿ�е��ʸ����Լ�
									wordList.add(w);//��ӵ�word�����б���
									flag = 1;
									break;
								}
								else if(a.equals("true")||a.equals("false")){
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setValue(a);
									w.setState(preState);
									w.setNumber(numberCount++);
									lineNumCount++;//ÿ�е��ʸ����Լ�
									w.setType("0x105");
									outputWord(w);//������ļ�
									wordList.add(w);//��ӵ�word�����б���
									System.out.println(a+" "+"<"+"0x105"+">"+"\r\n");
									flag = 1;
									break;
								}
							}
							if(flag == 0){
								Word w =new Word(a);
								w.setLine(lineCount);
								w.setValue(a);
								w.setState(preState);
								w.setNumber(numberCount++);
								lineNumCount++;//ÿ�е��ʸ����Լ�
								w.setType(endAttributeArray[preState]);//��ʶ��
								outputWord(w);//������ļ�
								wordList.add(w);//��ӵ�word�����б���
								System.out.println(a+" "+"<"+endAttributeArray[preState]+">"+"\r\n");
							}
						}
						//������������
						else{
							Word w =new Word(a);
							w.setLine(lineCount);
							w.setValue(a);
							w.setState(preState);
							w.setNumber(numberCount++);
							lineNumCount++;//ÿ�е��ʸ����Լ�
							w.setType(endAttributeArray[preState]);
							outputWord(w);//������ļ�
							wordList.add(w);//��ӵ�word�����б���
							System.out.println(a+" "+"<"+endAttributeArray[preState]+">"+"\r\n");
						}
						bp = hp;
					}
					else if(currentState==ERROR_STATE){
						String a = getString(bufNum, bp, hp);
						Word w =new Word(a);
						w.setLine(lineCount);
						w.setValue(a);
						w.setState(ERROR_STATE);
						w.setNumber(numberCount++);
						lineNumCount++;//ÿ�е��ʸ����Լ�
						w.setType("0x101");
						outputWord(w);//������ļ�
						wordList.add(w);//��ӵ�word�����б���
						errorWordList.add(w);
						hp++;
						bp = hp;
						currentState = START_STATE;
					}
					else{
						if(judgeState(currentState)){
							//�ų��ո񵥴�
							if(currentState!=lineCountMachine.LINE_COUNT_STATE){
								if(currentState!=spaceStateMachine.SPACE_STATE){
									lastWordState = currentState;//��һ������״̬
									String a = getString(bufNum, bp, hp);//��ȡ����
									Word w =new Word(a);
									w.setLine(lineCount);
									w.setValue(a);
									w.setState(currentState);
									w.setNumber(numberCount++);
									lineNumCount++;//ÿ�е��ʸ����Լ�
									w.setType(endAttributeArray[currentState]);
									outputWord(w);//������ļ�
									wordList.add(w);//��ӵ�word�����б���
									System.out.println(a+" "+"<"+endAttributeArray[currentState]+">"+"\r\n");
								}
							}
							//����
							else{
								outputFileWriter.write("-------Line:"+lineCount+" Words Count: "+lineNumCount+"\r\n");
								lineCount++;
								lineNumCount=0;
							}
							hp++;
							bp = hp;
							currentState = START_STATE;
						}
						else{
							hp++;
						}
					}
				}
				//���ʻ�δ������뻺����
				if(currentState!=START_STATE&&!judgeState(currentState)){
					bufNum = 1-bufNum;
					exceedFlag = 1;
					addBuffer(bufNum);
					hp=0;
				}
				else{
					hp = 0;
					bp = 0;
					clearBuffer(bufNum);
					addBuffer(bufNum);
				}
			}
			outputFileWriter.write("</tokens>"+"\r\n");

            if(errorWordList.size()>0){
				for(Word w:errorWordList){
					outputErrorWord(w);
				}
			}
			//�ر��ļ���д
			filterFileReader.close();
			outputFileWriter.flush();
			outputFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	/*
	 * ѡ��״̬������
	 */
	public void transferState(char c){
		//�ʼ��Ҫ����������ַ�ѡ���ĸ�״̬��
		if(currentState == START_STATE){
			if(isWordFirst(c)){
				wordStateMachine.changeState(c);
			}//��ʶ��״̬��
			else if(isOperatorFirst(c)){
				operStateMachine.setIsPlusOrMinusFlag(isPlusOrMinus());
				operStateMachine.changeState(c);
			}//�����״̬��
			else if(isRegionFirst(c)){//
				regionStateMachine.changeState(c);
			}//���޷�״̬��
			else if(isSpaceFirst(c)){
				spaceStateMachine.changeState(c);
			}//�ո�״̬��
			else if(isCharFirst(c)){
				//charState(c);
				charStateMachine.changeState(c);
			}//�ַ�����״̬��
			else if(isStringFirst(c)){
				stringStateMachine.changeState(c);
			}
			else if(isNumFirst(c)){
				if(c=='0'){
					numStateMachine.setZeroFirstFlag(1);
				}
				numStateMachine.changeState(c);
				//numStateMachine.setZeroFirstFlag(0);//�ָ���ʼֵ
			}
			else if(isLineCountFirst(c)){
				lineCountMachine.changeState(c);
			}
			//			else{
			//				currentState = START_STATE;
			//			}
		}
		else{		//�������̵��и��ݵ�ǰ��״̬��ѡ���ĸ�״̬������ִ��
			if(currentState>=1&&currentState<=37){
				operStateMachine.changeState(c);
			}//�����״̬��
			else if(currentState==38){
				wordStateMachine.changeState(c);
			}//��ʶ��״̬��
			else if(currentState>=39&&currentState<=47){
				regionStateMachine.changeState(c);
			}//���޷�״̬��
			else if(currentState==48){
				spaceStateMachine.changeState(c);
			}//�ո�״̬��
			else if(currentState>=49&&currentState<=76){
				//charState(c);
				charStateMachine.changeState(c);
			}//�ַ�����״̬��
			else if(currentState>=77&&currentState<=98){
				stringStateMachine.changeState(c);
			}//�ַ�������״̬��
			else if(currentState>=99&&currentState<=115){
				numStateMachine.changeState(c);
			}//����״̬��
			else if(currentState==116){
				lineCountMachine.changeState(c);
			}
			else{//���ڴ���״̬
				currentState = ERROR_STATE;
			}
		}
	}
	/**
	 * �ж���һ��״̬�Ե�ǰ״̬�ķ���Ӱ�����������ǼӼ�
	 */
	public boolean isPlusOrMinus(){
		//Ϊ��ʶ�������ַ����������ַ��������������������Ϊ�Ӽ���
		if(lastWordState==38||(lastWordState>=49&&lastWordState<=115)){
			return true;
		}
		return false;
	}
	/**
	 * ������ļ�
	 * @param w
	 */
	public void outputWord(Word w){
		try {
			outputFileWriter.write("  "+"<token>\r\n");
			outputFileWriter.write("    "+"<number> "+w.getNumber()+" </number>\r\n");
			outputFileWriter.write("    "+"<value> "+w.getValue()+" </value>\r\n");
			outputFileWriter.write("    "+"<type> "+w.getType()+" </type>\r\n");
			outputFileWriter.write("    "+"<line> "+w.getLine()+" </line>\r\n");
			outputFileWriter.write("  "+"</token>\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//�ؼ�������
	}
	public void outputErrorWord(Word w){
		try {
			outputFileWriter.write("<Error Info>"+"\r\n");
			outputFileWriter.write("  "+"<value> "+w.getValue()+" </value>\r\n");
			outputFileWriter.write("  "+"<type> Error Word </type>"+"\r\n");
			outputFileWriter.write("  "+"<line> "+w.getLine()+" </line>\r\n");
			outputFileWriter.write("</Error Info>"+"\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����:����һ�������ַ�Ϊ������ķ���
	 */
	public static boolean isOperatorFirst(char c){
		String str = ""+c;
		for(int i =0;i<operaterList.size();i++){
			if(operaterList.get(i).equals(str)){
				return true;
			}
		}
		return false;
	}
	/*
	 * ����:����һ��������ַ�Ϊ���е�'\r'
	 */
	public static boolean isLineCountFirst(char c){
		if(c=='\r'){
			return true;
		}
		return false;
	}
	/*
	 * ����:����һ��������ַ�Ϊ��ֵ�����ķ���
	 * 
	 */
	public static boolean isNumFirst(char c){
		if(c>='0'&&c<='9'){
			return true;
		}
		return false;
	}
	/*
	 * ����:����һ�������ַ�Ϊ�ַ������ķ���
	 */
	public static boolean isCharFirst(char c){
		if(c=='\''){
			return true;
		}
		return false;
	}
	/*
	 * ����:����һ�������ַ��Ƿ�Ϊ�ַ��������ķ���
	 */
	public static boolean isStringFirst(char c){
		if(c=='"'){
			return true;
		}
		return false;
	}
	/**
	 * ����:����һ�������ַ�Ϊ��ʶ���ķ���
	 */
	public static boolean isWordFirst(char c){
		if(isWord(c)||c=='$'||c=='_')
			return true;
		return false;
	}
	/*
	 * ����:����һ�������ַ�Ϊ���޷��ķ���
	 */
	public boolean isRegionFirst(char c){
		if(c=='{' || c=='}' || c==',' || c==';'
				||c=='('||c==')'||c=='['||c==']'||c=='.'){
			return true;
		}
		return false;
	}
	/*
	 * ����һ��Ϊ�ո�
	 */
	public boolean isSpaceFirst(char c){
		if(c==' '){
			return true;
		}
		return false;
	}
	/*
	 * �Ƿ�Ϊ�˽�����
	 */
	public static boolean isOctalNum(char c){
		if(c>='0'&&c<='7'){
			return true;
		}
		return false;
	}

	/*
	 *�Ƿ�Ϊʮ��������
	 */
	public static boolean isHexNum(char c){
		if((c>='0'&&c<='9')||(c>='A'&&c<='F')||(c>='a'&&c<='f')){
			return true;
		}
		return false;
	}
	/*
	 * �Ƿ�Ϊ����
	 */
	public static boolean  isWord(char c){
		if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){
			return true;
		}
		else return false;
	}
	/**
	 * �ضϵ�ǰ����
	 * @param bufNum �������ı��
	 * @param bp ���ʵ���ʼλ��
	 * @param hp ���ʵĽ���λ��
	 * @return
	 */
	public String getString(int bufNum,int bp,int hp){
		String a = "";
		//��������������
		if(exceedFlag==1){//��Ҫʹ������������
			for(int i = bp;i<BUFSIZE;i++){
				a+=buffer[1-bufNum][i];//��ȡ�ϸ��������Ĳ��ֵ���
			}
			for(int i=0;i<=hp;i++){
				a += buffer[bufNum][i];//���ӱ��λ������Ĵ���
			}
			exceedFlag = 0;
		}
		else{
			for(int i=bp;i<=hp;i++){
				a+=buffer[bufNum][i];
			}
		}
		return a;
	}
	/**
	 * ���뻺����
	 * @param bufNum
	 */
	public void addBuffer(int bufNum){
		try {
			int c;
			int bufCount = 0;
			while(bufCount<BUFSIZE&&(c=filterFileReader.read())!=-1){
				buffer[bufNum][bufCount++] = (char)c;
			}
			bufSize = bufCount;
			if(bufSize == 0){
				endFileFlag = 1;//�ж��ļ��Ƿ����
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ����:���bufNum��Ӧ�Ļ�����
	 * @param bufNum
	 */
	public void clearBuffer(int bufNum){
		for(int i=0;i<BUFSIZE;i++){
			buffer[bufNum][i] = ' ';
		}
	}
	public boolean judgeState(int state){
		//ȥ����ǰ׺����Щ��̬����Ϊ��Щ��������ILLEGAL_STATE��
		if(endStateArray[state]==1)
			return true;
		return false;
	}
}
