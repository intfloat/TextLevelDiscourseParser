package wl.PDTB;

import java.util.ArrayList;

/**
 * 
 * @author Liang Wang
 * @version 2013/11/26
 *
 */
public class PDTBNode {
	
//	its POS, maybe NN or NP etc
	public String posTag;
//	its level, 0 indexed
	public int level;
//	its column position in current level
	public int col;
//	its parent, so we can travel the whole tree in two directions
	public int parent;
//	possible word, only for leaf node
	public String word;	
//	list of its children
	public ArrayList<Integer> childList;
//	to indicate there is actually no valid word
	public static final String INVALID_WORD = "$INVALID$";
	
//	an empty constructor
	public PDTBNode(){
		this.posTag = "";
		this.level = this.col = this.parent = -1;
		this.word = PDTBNode.INVALID_WORD;
		this.childList = new ArrayList<Integer>();
	}// end first constructor
	
//	all parameters correspond to its different fields
	public PDTBNode(String posTag, int level, int col, int parent, String word){
		this.posTag = posTag;
		this.level = level;
		this.col = col;
		this.parent = parent;
		this.word = word;
		this.childList = new ArrayList<Integer>();
	}// end second constructor
	
//	if it is a leaf node
	public boolean isLeaf(){
		if(this.word == PDTBNode.INVALID_WORD)
			return false;
		return true;
	}// end method isLeaf
	
	public void setCol(int col){
		this.col = col;
	}

}// end class PDTBNode
