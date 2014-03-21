package ziqiangyeah.experiment.RST;

import java.util.*;

/**
 * 用于表达文本中一行，也就是树的一个节点，Nucleus和Satellite的子节点分开存放
 * Tag表示本节点的标记值，对于N，tag为其最左子树Tag，对S，tag为其父节点Tag
 * 
 * Modified by : Liang Wang
 * @author Cao Ziqiang
 * @version 2013/10/20
 */
public class RSTNode {
	public static final int UNKNOWN=-1;
	public static final String NUCLEUS="Nucleus";
	public static final String SATELLITE="Satellite";
	public static final String LEAF="leaf";
	public static final String ROOT="Root";
	public static final String SPAN="span";
	public static final int ROOT_TAG=0;
	public int Parent;
	public String RelationType;
	public int Tag;
	public int SpanLeft;
	public int SpanRight;
	
//	an array list to store all nucleus child node,
//	there may be more than 1 nucleus child nodes.
	public ArrayList<Integer> NChildList;
//	an array list to store all satellite child node,
//	there is at most one satellite node.
	public ArrayList<Integer> SChildList;
	
//	a empty constructor
	public RSTNode(){
		this.NChildList=new ArrayList<Integer>();
		this.SChildList=new  ArrayList<Integer>();
		this.Tag=UNKNOWN;
	}
	
//	to see if a position is inside span of this node
	public boolean inNode(int position){
		return SpanLeft<=position&&position<=SpanRight;
	}
	
//	to see if interval [posLeft, posRight] is inside span of this node
	public boolean inNode(int positionLeft,int positionRight){
		return SpanLeft<=positionLeft&&positionRight<=SpanRight;
	}
	
//	to see if it is a leaf node
	public boolean isLeaf(){
		return SpanLeft==SpanRight;
	}
	
}// end class RSTNode
