package utils;

import org.omg.CORBA.TRANSACTION_MODE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hersch on 2016/7/2.
 * �﷨�������
 */
public class TreeNode extends Word {
    //��ǩ����
    public static int labels;
    //���׽ڵ�
    public TreeNode parentNode;
    //�ӽڵ�
    public List<TreeNode>chlidNodes;
    //�ս���ڵ�
    public List<TreeNode>terminalNodes;
    //��Ԫʽ
    public List<String>generLists;
    //��Ǻ�
    public List<Integer>labelNumLists;
    public TreeNode(){}
    public TreeNode(String value){
        setValue(value);
        type = "";
        chlidNodes = new ArrayList<TreeNode>();
        terminalNodes = new ArrayList<TreeNode>();
        labelNumLists = new ArrayList<Integer>();
        generLists = new ArrayList<String>();
    }
    /**
     * �����±��
     * @return
     */
    public int newLabel(){
        ++labels;
        labelNumLists.add(labels);
        return labels;
    }

    /**
     * ��ȡ�����ɵı�ŵ��б�
     * @return
     */
    public List<Integer> getLabelNumLists() {
        return labelNumLists;
    }

    /**
     * ����ӽڵ�
     * @param treeNode
     */
    public void addChildNode(TreeNode treeNode){
        chlidNodes.add(treeNode);
    }
    /**
     * ��ȡ�ӽڵ��б�
     * @return
     */
    public List<TreeNode> getChildNodes(){
        return chlidNodes;
    }
    /**
     * ���÷����ӳ����Ӽ�
     * @return
     */
    public List<String> getGenetLists(){
        return generLists;
    }
    /**
     * ��ӷ����ӳ���
     * @param s
     */
    public void addGenerLists(String s){
        generLists.add(s);
    }

    /**
     * �����������е��ս��
     * @param treeNode
     */
    public void setTerminalWordsLists(TreeNode treeNode){
        terminalNodes.add(treeNode);
    }

    /**
     * ��ȡ�������е��ս��
     * @return
     */
    public List<TreeNode> getTermialWordsLists(){
        return terminalNodes;
    }

    /**
     * ���ø��׽ڵ�
     * @param node
     */
    public void setParentNode(TreeNode node){
        this.parentNode = node;
    }

    /**
     * ��ȡ���׽ڵ�
     * @return
     */
    public TreeNode getParentNode() {
        return parentNode;
    }
}
