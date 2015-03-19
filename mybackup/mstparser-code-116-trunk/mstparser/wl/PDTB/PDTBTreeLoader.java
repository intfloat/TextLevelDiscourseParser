package wl.PDTB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/**
 * 
 * @author Liang Wang
 * @version 2013/11/26
 *
 */
public class PDTBTreeLoader {
	
	private Vector<PDTBNode> status;
	public ArrayList<PDTBNode> leafNodes;
	private PDTBTree mytree;
	public static final String NONE = "-NONE-";
	
	/**
	 * 
	 * @param w is target word to search
	 * @param begin is beginning index to search
	 * @return
	 */
	public int searchWord(String w, int begin){
		for(int i=begin; i<leafNodes.size(); i++){
			if(w.equals(leafNodes.get(i).word))
				return i;
		}// end for loop
//		in case we didn't find corresponding word
		return -1;
	}// end method searchWord
	
	/**
	 * 
	 * @param words is an array of string
	 * @return all syntactic labels from bottom to top
	 */
	public ArrayList<ArrayList<String>> getSyntacticLabels(ArrayList<ArrayList<String>> words){
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		for(int i=0; i<words.size(); i++){
			ArrayList<String> ithArray = new ArrayList<String>();
			res.add(ithArray);
		}// end for loop
		
		ArrayList<String> currentList = null;
		int position = 0;
		int left, right;
		for(int i=0; i<words.size(); i++){
			currentList = words.get(i);
			int index = position;
			int numberFound = 0;
			for(int j=0; j<currentList.size(); j++){
				int ans = this.searchWord(currentList.get(j), index);
				if(ans < 0) continue;
				++numberFound;
				if(ans-index<=2 || (j==0 && (ans-index)<=10)) index=ans+1;
			}// end internal for loop
			double ratio = ((double)numberFound/currentList.size());
			if(ratio<0.5 && numberFound<5){
//				do nothing...
				res.get(i).add("NOTFOUND");
//				for(String str:currentList)
//					System.out.print(str+" ");
//				System.out.println();
			}
			else{
				left = position; right = Math.max(index-1, position);
				position = index;
				res.set(i, this.getRangeLabels(left, right));				
			}// end else clause
		}// end external for loop
		return res;
	}// end method getSyntacticLabels
	
	/**
	 * 
	 * @param left
	 * @param right
	 * @return labels lying in specified range
	 */
	private ArrayList<String> getRangeLabels(int left, int right){		
		ArrayList<String> res = new ArrayList<String>();
		if(left>right || right>=leafNodes.size()) return res;
		PDTBNode leftNode = leafNodes.get(left);
		PDTBNode rightNode = leafNodes.get(right);
		int leftLevel = leftNode.level;
//		int leftCol = leftNode.col;
		int rightLevel = rightNode.level;
//		int rightCol = rightNode.col;
//		search for the lowest common ancestor
		if(leftLevel < rightLevel){
			while(rightLevel > leftLevel){
				int parent = rightNode.parent;
				rightNode = this.mytree.tree.get(rightLevel-1).get(parent);
				--rightLevel;
			}// end while loop
		}
		else if(leftLevel > rightLevel){
			while(leftLevel > rightLevel){
				int parent = leftNode.parent;
				leftNode = this.mytree.tree.get(leftLevel-1).get(parent);
				--leftLevel;
			}// end while loop
		}// end else if clause
		while(leftNode.col != rightNode.col){
			int parent = leftNode.parent;
			leftNode = this.mytree.tree.get(leftLevel-1).get(parent);
			--leftLevel;
			parent = rightNode.parent;
			rightNode = this.mytree.tree.get(rightLevel-1).get(parent);
			--rightLevel;
		}
//		this is the level for lowest common ancestor
		int topLevel = leftNode.level;
		
		PDTBNode currentNode = null;
		for(int i=left; i<=right; i++){
			currentNode = leafNodes.get(i);
			while(currentNode.level >= topLevel){
				int parent = currentNode.parent;
				int curLevel = currentNode.level;
//				to differentiate two different cases
				if(currentNode.level == topLevel)
					res.add("top_"+currentNode.posTag);
				else
					res.add(currentNode.posTag);
				if(parent < 0) break;
				currentNode = this.mytree.tree.get(curLevel-1).get(parent);
				
			}// end while loop
		}// end for loop
		return res;
	}// end method getRangeLabels
	
	/**
	 * 
	 * @param file with suffix ".mrg", already been checked in PDTBParser
	 * @return corresponding tree for this file
	 * @throws FileNotFoundException 
	 */
	public PDTBTree loadTree(File file) throws FileNotFoundException{
		PDTBTree pdtbTree = new PDTBTree();
		leafNodes = new ArrayList<PDTBNode>();
		status = new Vector<PDTBNode>();
		Scanner reader = new Scanner(file);
		
		PDTBNode root = pdtbTree.addRoot();
		status.clear(); status.add(root);
		leafNodes.clear();
		int currentLevel = 0;
		int lineNumber = 0;
//		get every line sequentially
		while(reader.hasNextLine()){
			String line = reader.nextLine();
			++lineNumber;
//			there maybe empty lines, just skip them
			if(line.trim().length() == 0)
				continue;
			int actualLevel = line.indexOf('(');
//			this should not occur, just for debugging
//			if(actualLevel < 0){
//				System.err.println("Error in "+file.toString()+" can not find ( "+lineNumber+" "+line);
//				continue;
//			}			
			if(actualLevel >= 0){
				actualLevel = actualLevel/2;
				if(actualLevel==0 && currentLevel!=0){
					System.err.println("Level information seems wrong "+file.toString()+" "+lineNumber+" "+line);
					System.err.println("currentLevel: "+currentLevel+" actualLevel: "+actualLevel);
					System.exit(1);
				}
				if(actualLevel!=0 && actualLevel!=currentLevel+1){
					System.err.println("Level information seems wrong "+file.toString()+" "+lineNumber+" "+line);
					System.err.println("currentLevel: "+currentLevel+" actualLevel: "+actualLevel);
					System.exit(1);
				}
			}// end external if clause
			
			String[] str = line.trim().split("\\s++");
//			PDTBNode currentNode = status.get(currentLevel);
//			this is the key part for parsing PDTB tree
			for(int i=0; i<str.length; i++){
				if(currentLevel != status.size()-1){
					System.err.println("Error in status transition. ");
					System.err.println("currentLevel: "+currentLevel+" size "+status.size());
					System.exit(1);
				}
				int len = str[i].length();
//				first case
				if(str[i].equals("(")) continue;
//				second case
				if(str[i].indexOf(')')==len-1 && len>1){
					int col = status.get(currentLevel).col;
					String word = str[i].substring(0, len-1);
					pdtbTree.tree.get(currentLevel).get(col).word = word;
//					add leaf nodes into an array for convenience of searching later
					if(pdtbTree.tree.get(currentLevel).get(col).posTag.equals(NONE)==false)
						leafNodes.add(pdtbTree.tree.get(currentLevel).get(col));
					
					status.remove(currentLevel);
					--currentLevel;
					continue;
				}
//				third case
				if(str[i].indexOf(')')==0 && str[i].charAt(len-1)==')'){
					for(int j=0; j<len; j++){
//						avoid ArrayIndexOutOfBound exception
						if(currentLevel > 0){
							status.remove(currentLevel);
							--currentLevel;
						}
					}// end for loop
					continue;
				}
//				fourth case
				if(str[i].indexOf('(')==0 && len>1){
					String pos = str[i].substring(1);
					PDTBNode child = pdtbTree.addNode(pos, currentLevel+1, 
							status.get(currentLevel).col, PDTBNode.INVALID_WORD);
					status.add(child);
					++currentLevel;
					continue;
				}
//				this should not happen
				System.err.println("Missing some cases error: "+line);
				System.exit(1);
			}// end for loop
			
		}// end while loop
//		perform substituting
		for(int i=0; i<leafNodes.size(); i++){
			if(leafNodes.get(i).word.equals("-LRB-"))
				leafNodes.get(i).word = "(";
			else if(leafNodes.get(i).word.equals("-RRB-"))
				leafNodes.get(i).word = ")";
		}// end for loop
		this.mytree = pdtbTree;
		return pdtbTree;
	}// end method loadTree
	
}// end class PDTBTreeLoader
