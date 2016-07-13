package syntax.masm;

import syntax.GeneratorReduction;
import syntax.GeneratorTable;
import syntax.SyntaxAnalyser;
import utils.AddressNode;
import utils.RegisterNode;
import utils.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Hersch on 2016/7/6.
 * ��Ԫʽ���ɻ��
 */
public class Masm {
    private final String MOV = "MOV";
    private final String SUB = "SUB";
    private final String JG = "JG";
    private final String JB = "JB";
    private final String ADD = "ADD";
    private final String MUL = "MUL";
    private final String DIV = "DIV";
    private final String CMP = "CMP";
    private final String INC = "INC";
    //trueΪ����,falseΪС��
    private boolean moreOrLessFlag = false;
    private int jumpT = 0;
    private final String MASM_PATH = "masm.asm";
    private FileWriter fileWriter;
    private File file;
    private List<String> masmCodeList;
    //��¼��ת��ȷ�ı��
    //��żĴ���
    List<RegisterNode> registerNodes;
    //�����Ԫʽ�б�
    List<GeneratorTable> generatorTables;
    //��ŵ�ǰ�Ĵ����������ӳ���ϵ
    Map<String, RegisterNode> valueRegisterMap;
    //���ݶ��б�
    Map<String, AddressNode> dataSegmentMap;
    private String OP = "";

    public Masm(List<GeneratorTable> list) {
        masmCodeList = new ArrayList<String>();
        dataSegmentMap = new HashMap<String, AddressNode>();
        generatorTables = list;
        valueRegisterMap = new HashMap<String, RegisterNode>();
        initFileWriter();
        initRegister();
        initDataSegments();
        generatorMasm();
        //System.out.println("end start");
        masmCodeList.add("end start");
        outputFileStream();

    }

    public void initFileWriter() {
        file = new File(MASM_PATH);
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputFileStream() {
        try {
            for (String str : masmCodeList) {
                fileWriter.write(str + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDataSegments() {
        for (GeneratorTable generatorTable : generatorTables) {
            for (int i = 2; i < generatorTable.getTable().size(); i++) {
                TreeNode treeNode = generatorTable.getTable().get(i);
                if (treeNode.getType().equals(SyntaxAnalyser.ID) ||
                        treeNode.getType().equals(GeneratorReduction.TEMP)) {
                    if (!dataSegmentMap.containsKey(treeNode.getValue())) {
                        dataSegmentMap.put(treeNode.getValue(), new AddressNode());
                    }
                }
            }
        }
        //�����������ݶ�
        //System.out.println(".386");
        masmCodeList.add(".386");
        //System.out.println(".model  flat, stdcall");
        masmCodeList.add(".model  flat, stdcall");
        //System.out.println("option  casemap:none");
        masmCodeList.add("option  casemap:none");
        //System.out.println("includelib \\masm32\\lib\\msvcrt.lib");
        masmCodeList.add("includelib \\masm32\\lib\\msvcrt.lib");
        //System.out.println(".data");
        masmCodeList.add(".data");
        for (Map.Entry<String, AddressNode> entry : dataSegmentMap.entrySet()) {
            //System.out.println(entry.getKey() + "  " + "dd" + " " + 0);
            masmCodeList.add(entry.getKey() + "  " + "dd" + " " + 0);
        }
        //System.out.println(".stack");
        masmCodeList.add(".stack");
        //System.out.println(".code");
        masmCodeList.add(".code");
        //System.out.println("start:");
        masmCodeList.add("start:");
    }

    public void initRegister() {
        registerNodes = new ArrayList<RegisterNode>();
        RegisterNode axRegister = new RegisterNode();
        axRegister.setName("EAX");
        registerNodes.add(axRegister);
        RegisterNode bxRegister = new RegisterNode();
        bxRegister.setName("EBX");
        registerNodes.add(bxRegister);
        RegisterNode cxRegister = new RegisterNode();
        cxRegister.setName("ECX");
        registerNodes.add(cxRegister);
        RegisterNode dxRegister = new RegisterNode();
        dxRegister.setName("EDX");
        registerNodes.add(dxRegister);
    }

    /**
     * ������Ԫʽ���ɻ��
     */
    public void generatorMasm() {
        for (GeneratorTable generatorTable : generatorTables) {
            List<TreeNode> treeNodeList = generatorTable.getTable();
            if (treeNodeList.get(1).getValue().equals(">") || treeNodeList.get(0).getValue().equals("<")) {
                handleMoreOrLess(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("JT") ||
                    treeNodeList.get(1).getValue().equals("JF") ||
                    treeNodeList.get(1).getValue().equals("J")) {
                handleJump(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("=")) {
                handleEqual(generatorTable);
            } else if (treeNodeList.get(1).getValue().equals("")) {
                //System.out.println(treeNodeList.get(0).getValue());
                masmCodeList.add(treeNodeList.get(0).getValue());
            } else {
                handleCalculate(generatorTable);
            }
        }
    }

    /**
     * ������������෭��
     */
    public void handleMoreOrLess(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(3);
        //System.out.println(treeNodeList.get(0).getValue() + ":");
        masmCodeList.add(treeNodeList.get(0).getValue() + ":");
        if (!judgeConst(firstNode) && !judgeConst(nextNode)) {
            //a>b
            allocateRegister(firstNode);
            allocateRegister(nextNode);
            // System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName()
            //       + ", " + firstNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName()
                    + ", " + firstNode.getValue());
            System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName()
                    + ", " + nextNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName()
                    + ", " + nextNode.getValue());
            System.out.println(CMP + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    valueRegisterMap.get(nextNode.getValue()).getName());
            masmCodeList.add(CMP + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    valueRegisterMap.get(nextNode.getValue()).getName());
            unLockRegister(firstNode);
            unLockRegister(nextNode);
        } else {
            if (!judgeConst(firstNode) && judgeConst((nextNode))) {
                System.out.println(CMP + " " + firstNode.getValue() + ", " +
                        nextNode.getValue());
                masmCodeList.add(CMP + " " + firstNode.getValue() + ", " +
                        nextNode.getValue());
            } else if (!judgeConst(nextNode) && judgeConst(firstNode)) {
                System.out.println(CMP + " " + nextNode.getValue() + ", " +
                        firstNode.getValue());
                masmCodeList.add(CMP + " " + nextNode.getValue() + ", " +
                        firstNode.getValue());
            } else {
                System.out.println(CMP + " " + firstNode.getValue() + ", " + nextNode.getValue());
                masmCodeList.add(CMP + " " + firstNode.getValue() + ", " + nextNode.getValue());
            }
        }
        if (treeNodeList.get(1).getValue().equals(">")) {
            moreOrLessFlag = true;
        } else {
            moreOrLessFlag = false;
        }
    }

    /**
     * ������ת����෭��
     */

    public void handleJump(GeneratorTable generatorTable) {
        String value = generatorTable.getTable().get(1).getValue();
        if (value.equals("JT")) {
            //����
            if (moreOrLessFlag) {
                System.out.println("JG" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JG" + " " + generatorTable.getTable().get(4).getValue());
            } else {
                System.out.println("JB" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JB" + " " + generatorTable.getTable().get(4).getValue());
            }
        } else if (value.equals("JF")) {
            if (moreOrLessFlag) {
                System.out.println("JB" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JB" + " " + generatorTable.getTable().get(4).getValue());
            } else {
                System.out.println("JG" + " " + generatorTable.getTable().get(4).getValue());
                masmCodeList.add("JG" + " " + generatorTable.getTable().get(4).getValue());
            }
            //�ҵ�������ת�ı��
            for (int i = 0; i < generatorTables.size(); i++) {
                if (generatorTables.get(i).getTable().get(1).getValue().equals("JF")) {
                    jumpT = i + 1;
                    break;
                }
            }
            System.out.println(generatorTables.get(jumpT).getTable().get(0).getValue() + ":");
            masmCodeList.add(generatorTables.get(jumpT).getTable().get(0).getValue() + ":");
        } else {
            System.out.println("JUMP" + " " + generatorTable.getTable().get(4).getValue());
            masmCodeList.add("JUMP" + " " + generatorTable.getTable().get(4).getValue());
        }
    }

    /**
     * ����ֵ����෭��
     * ��ʱ������ֵ�ͳ�����ֵ
     */
    public void handleEqual(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(4);
        //���ڳ�����ֵ
        if (!dataSegmentMap.containsKey(firstNode.getValue())) {
            System.out.println(MOV + " " + nextNode.getValue() + ", " + firstNode.getValue());
            dataSegmentMap.get(nextNode.getValue()).setValue(Integer.parseInt(firstNode.getValue()));
            masmCodeList.add(MOV + " " + nextNode.getValue() + ", " + firstNode.getValue());
        }
        //������ʱ������ֵ
        else {
            allocateRegister(firstNode);//Ϊ��ʱ��������һ���Ĵ���
            System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    firstNode.getValue());
            masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() + ", " +
                    firstNode.getValue());
            System.out.println(MOV + " " + nextNode.getValue() + ", " +
                    valueRegisterMap.get(firstNode.getValue()).getName());
            masmCodeList.add((MOV + " " + nextNode.getValue() + ", " +
                    valueRegisterMap.get(firstNode.getValue()).getName()));
            int value = dataSegmentMap.get(firstNode.getValue()).getValue();
            dataSegmentMap.get(nextNode.getValue()).setValue(value);
            //ȥ��ռ�üĴ�������ʱ����
            unLockRegister(firstNode);
        }
    }

    /**
     * ������������෭��
     * ע��˷����������⴦��
     */
    public void handleCalculate(GeneratorTable generatorTable) {
        List<TreeNode> treeNodeList = generatorTable.getTable();
        TreeNode firstNode = treeNodeList.get(2);
        TreeNode nextNode = treeNodeList.get(3);
        TreeNode opNode = treeNodeList.get(1);
        TreeNode temporaryNode = treeNodeList.get(4);
        String op = opNode.getValue();

        if (firstNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
            if (nextNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
                //1+5=T3
                int result = calculate(op, Integer.parseInt(firstNode.getValue()), Integer.parseInt(nextNode.getValue()));
                dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                System.out.println(MOV + " " + temporaryNode.getValue() + ", " + result);
                masmCodeList.add(MOV + " " + temporaryNode.getValue() + ", " + result);
            } else {
                //1+a=T3
                int result = calculate(op, dataSegmentMap.get(nextNode.getValue()).getValue(),
                        Integer.parseInt(firstNode.getValue()));
                //�˷����� MOV EAX,NUM1  MUL SRC
                if (OP.equals(MUL)) {
                    //MOV EAX,a
                    valueRegisterMap.put(nextNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = result
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + firstNode.getValue());
                    masmCodeList.add(MUL + " " + firstNode.getValue());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(nextNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    unLockRegister(nextNode);
                }
                // ������
                else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    //MOV EAX,a
                    valueRegisterMap.put(nextNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //DIV 1
                    //EAX = result
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + firstNode.getValue());
                    masmCodeList.add(MUL + " " + firstNode.getValue());
                    //MOV T3,EAX   EAX:EDX ��:����
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(nextNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    unLockRegister(nextNode);

                } else {
                    //MOV �Ĵ���,a
                    allocateRegister(nextNode);//��a����Ĵ���
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //ADD register,1
                    valueRegisterMap.get(nextNode.getValue()).setValue(result);
                    System.out.println(OP + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MOV T3,register
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                                    ", " + valueRegisterMap.get(nextNode.getValue()).getName()
                    );
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(nextNode);
                }
            }
        } else if (firstNode.getType().equals(SyntaxAnalyser.ID) || firstNode.getType().equals(GeneratorReduction.TEMP)) {
            //�������¼������
            //T1+1   T1+a  a+T1 a+1
            if (nextNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
                //a+1=T3
                int result = calculate(op, dataSegmentMap.get(firstNode.getValue()).getValue(),
                        Integer.parseInt(nextNode.getValue()));
                if (OP.equals(MUL)) {
                    //MOV EAX,a MUL SRC
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MUL 1
                    //EAX = a*1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + nextNode.getValue());
                    masmCodeList.add(MUL + " " + nextNode.getValue());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    //�����Ĵ���
                    unLockRegister(firstNode);
                } else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    //MOV EAX,a
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //DIV 1
                    //EAX = a/1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + nextNode.getValue());
                    masmCodeList.add(MUL + " " + nextNode.getValue());
                    //MOV T3,EAX   EAX:EDX ��:����
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);

                } else {
                    //MOV �Ĵ���,a
                    allocateRegister(firstNode);//��a����Ĵ���
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //ADD register,1
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    //MOV T3,register
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(firstNode);
                }
            } else {
                //a+b=T3
                int result = calculate(op, dataSegmentMap.get(firstNode.getValue()).getValue(),
                        dataSegmentMap.get(nextNode.getValue()).getValue());
                if (OP.equals(MUL)) {
                    //MOV EAX,a
                    //����˷��Ĵ�����a
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    //����Ĵ���
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = a*b
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(MUL + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add((MUL + " " + valueRegisterMap.get(nextNode.getValue()).getName()));
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);
                } else if (OP.equals(DIV)) {
                    //MOV EAX,a   DIV SRC
                    valueRegisterMap.put(firstNode.getValue(), registerNodes.get(0));
                    registerNodes.get(0).setState(1);
                    //����Ĵ���
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() + ", " + nextNode.getValue());
                    //MUL 1
                    //EAX = a/b
                    valueRegisterMap.get(firstNode.getValue()).setValue(result);
                    System.out.println(DIV + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(DIV + " " + valueRegisterMap.get(nextNode.getValue()).getName());
                    //MOV T3,EAX
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(
                            valueRegisterMap.get(firstNode.getValue()).getValue());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    unLockRegister(firstNode);
                } else {
                    allocateRegister(firstNode);
                    allocateRegister(nextNode);
                    System.out.println(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + firstNode.getValue());
                    //MOV REG,a
                    System.out.println(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    masmCodeList.add(MOV + " " + valueRegisterMap.get(nextNode.getValue()).getName() +
                            ", " + nextNode.getValue());
                    System.out.println(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    masmCodeList.add(OP + " " + valueRegisterMap.get(firstNode.getValue()).getName() +
                            ", " + valueRegisterMap.get(nextNode.getValue()).getName());
                    System.out.println(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    masmCodeList.add(MOV + " " + temporaryNode.getValue() +
                            ", " + valueRegisterMap.get(firstNode.getValue()).getName());
                    dataSegmentMap.get(temporaryNode.getValue()).setValue(result);
                    unLockRegister(firstNode);
                    unLockRegister(nextNode);
                }
            }
        }
    }

    /**
     * �������Ĵ���
     *
     * @param treeNode
     */
    public void allocateRegister(TreeNode treeNode) {
        boolean flag = false;
        if (!valueRegisterMap.containsKey(treeNode.getValue())) {
            for (RegisterNode registerNode : registerNodes) {
                if (registerNode.getState() == 0) {
                    valueRegisterMap.put(treeNode.getValue(), registerNode);
                    registerNode.setState(1);
                    valueRegisterMap.get(treeNode.getValue()).setValue(dataSegmentMap.get(treeNode.getValue()).getValue());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println("Allocate register failed!");
            } else {
                valueRegisterMap.get(treeNode.getValue()).setValue(
                        dataSegmentMap.get(treeNode.getValue()).getValue());
            }
        }
    }

    /**
     * ���
     *
     * @param treeNode
     */
    public void unLockRegister(TreeNode treeNode) {
        valueRegisterMap.get(treeNode.getValue()).setState(0);
        valueRegisterMap.remove(treeNode.getValue());
    }

    /**
     * ������
     *
     * @param op
     * @param a
     * @param b
     * @return
     */
    public int calculate(String op, int a, int b) {
        int result = 0;
        if (op.equals("+")) {
            result = a + b;
            OP = ADD;
        } else if (op.equals("-")) {
            result = a - b;
            OP = SUB;
        } else if (op.equals("*")) {
            result = a * b;
            OP = MUL;
        } else if (op.equals("/")) {
            result = a / b;
            OP = DIV;
        }
        return result;
    }

    /**
     * �ж��Ƿ�Ϊ����
     *
     * @param treeNode
     * @return
     */
    public boolean judgeConst(TreeNode treeNode) {
        if (treeNode.getType().equals(SyntaxAnalyser.CONST_INTEGER)) {
            return true;
        }
        return false;
    }
}
