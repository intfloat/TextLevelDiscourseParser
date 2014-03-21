package wl.PDTB;

import java.util.ArrayList;

/**
 * 
 * @author Liang Wang
 * @version 2013/11/26
 *
 */
public class PDTBTree {
	
//	use 2d array to denote a tree
	public ArrayList<ArrayList<PDTBNode>> tree;
	
//	just an empty constructor
	public PDTBTree(){}
	
	/**
	 * there is no need to give parameter "col"
	 * @param posTag
	 * @param level
	 * @param parent
	 * @param word
	 * @return
	 */
	public PDTBNode addNode(String posTag, int level, int parent, String word){
		
//		for debugging, those cases should not occur
		if(parent<0){
			System.err.println("Parent should be non-negative");
			return null;
		}	
//		for debugging
		if(level > tree.size()){
			System.err.println("Level of tree is expected to grow by 1, something wrong.");
			return null;
		}		
		
		PDTBNode node = new PDTBNode(posTag, level, -1, parent, word);
		if(level < tree.size()){			
			node.setCol(tree.get(level).size());
			tree.get(level).add(node);
			tree.get(level-1).get(node.parent).childList.add(node.col);
		}
		else{
			ArrayList<PDTBNode> nextLevel = new ArrayList<PDTBNode>();
			tree.add(nextLevel);
			node.setCol(nextLevel.size());			
			tree.get(level).add(node);
			tree.get(level-1).get(node.parent).childList.add(node.col);
		}
		return node;
	}
	
//	add an empty root node will made operations simpler
	public PDTBNode addRoot(){
//		when this method is called, tree should be an null object
		if(tree != null){
			System.err.println("addRoot is called in wrong way!");
			return null;
		}		
		this.tree = new ArrayList<ArrayList<PDTBNode>>();
		ArrayList<PDTBNode> child = new ArrayList<PDTBNode>();
//		level 0, col 0, parent -1, no word
		PDTBNode root = new PDTBNode("ROOT", 0, 0, -1, PDTBNode.INVALID_WORD);
		child.add(root);
		this.tree.add(child);
		return root;
	}// end method addRoot
	
//	just for debugging, actually we will not use this method in practice
	public void showTree(){
		int row = tree.size();
		for(int i=0; i<row; i++){
			int col = tree.get(i).size();
			for(int j=0; j<col; j++){
				PDTBNode node = tree.get(i).get(j);
				if(node.isLeaf())
					System.out.print(node.posTag+"_"+node.word);
				else
					System.out.print(node.posTag);
				System.out.print(" ");
			}// end internal for loop
			System.out.println();
		}// end external for loop
		return;
	}// end method showTree

}// end class PDTBTree
