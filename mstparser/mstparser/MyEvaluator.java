package mstparser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Liang Wang
 * @since 2013/10/13
 * @version 2013/11/12
 *
 */
public class MyEvaluator {
	
	public ArrayList<IntervalNode> goldIntervalNodes;
	public ArrayList<IntervalNode> preIntervalNodes;
	public ArrayList<IntervalNode> currentIntervalNodes;
	public int[] result;
	boolean[][] matrix;
	
	public void findNonprojective(int[] preHeads, String fileName){
//		two for loops are easy to implement
		for(int i=0; i<preHeads.length; i++){
			int p1 = preHeads[i];
			int c1 = i;
			for(int j=0; j<preHeads.length; j++){
				int p2 = preHeads[j];
				int c2 = j;
				int pos1 = (p2-p1)*(p2-c1);
				if(pos1 > 0) pos1 = 1;
				else if(pos1 < 0) pos1 = -1;
				int pos2 = (c2-p1)*(c2-c1);
				if(pos2 > 0) pos2 = 1;
				else if(pos2 < 0) pos2 = -1;
				if(pos1*pos2 < 0)
					System.out.println(fileName+": <"+(p1)+", "+(c1)+"> vs <"+(p2)+", "+(c2)+">");
			}// end inner for loop
		}// end external for loop
		return;
	}// end method findNonprojective
	
	/**
	 * 
	 * @param goldHeads are gold standards
	 * @param preHeads are results produced by program
	 * @param goldLabel are gold label standards
	 * @param preLabel are labels given by program
	 * @return an array containing 3 elements: correct spans, correct nuclearity, correct relations
	 */
	public int[] evaluate(int[] goldHeads, int[] preHeads, String[] goldLabel, String[] preLabel, String fileName){
		
		if(goldHeads.length != preHeads.length){
			System.err.println("Error: length of head of should be the same.");
			return null;
		}
		this.findNonprojective(preHeads, fileName);
//		instantiate some variables
		this.result = new int[3];
		Arrays.fill(result, 0);
		this.goldIntervalNodes = new ArrayList<IntervalNode>();		
		this.preIntervalNodes = new ArrayList<IntervalNode>();
		this.currentIntervalNodes = new ArrayList<IntervalNode>();
		this.goldIntervalNodes.clear();
		this.preIntervalNodes.clear();
		this.currentIntervalNodes.clear();
		
		int len = goldHeads.length;
		matrix = new boolean[len][len];
		for(int i=0; i<matrix.length; i++)
			Arrays.fill(matrix[i], false);
		for(int i=0; i<len; i++){
			matrix[i][i] = true;
			if(goldHeads[i] >= 0)
				matrix[goldHeads[i]][i] = true;
		}
//		floyd algorithm
//		actually we can use O(N^2) algorithm, but N is very small and floyd is easy to implement.
		for(int i=0; i<len; i++){
			for(int j=0; j<len; j++){
				for(int k=0; k<len; k++){
					if(matrix[j][i] && matrix[i][k])
						matrix[j][k] = true;
				}
			}
		}// end floyd algorithm
//		create gold intervals
		this.goldIntervalNodes.add(new IntervalNode(0, len-1, -1, IntervalNode.NUCLEUS, 0, IntervalNode.SPAN));
		this.generateIntervals(goldHeads, goldLabel, 0, len-1, 0, true);
//		for(IntervalNode node : currentIntervalNodes)
//			this.goldIntervalNodes.add(node);
		this.currentIntervalNodes.clear();
		
		for(int i=0; i<matrix.length; i++)
			Arrays.fill(matrix[i], false);
		for(int i=0; i<len; i++){
			matrix[i][i] = true;
			if(preHeads[i] >= 0)
				matrix[preHeads[i]][i] = true;
		}
//		floyd algorithm
		for(int i=0; i<len; i++){
			for(int j=0; j<len; j++){
				for(int k=0; k<len; k++){
					if(matrix[j][i] && matrix[i][k])
						matrix[j][k] = true;
				}
			}
		}// end floyd algorithm
//		create comparison intervals
		this.preIntervalNodes.add(new IntervalNode(0, len-1, -1, IntervalNode.NUCLEUS, 0, IntervalNode.SPAN));
		this.generateIntervals(preHeads, preLabel, 0, len-1, 0, false);
//		for(IntervalNode node : currentIntervalNodes)
//			this.preIntervalNodes.add(node);
		this.currentIntervalNodes.clear();
				
		this.scan();
		
		return this.result;
	}// end method evaluate
	
//	compare two array lists and get the final results
//	upper bound of complexity is O(N^2), considering N is at most 600, I think it is acceptable.
	private void scan(){
		
		Arrays.fill(result, 0);
//		iterate over every node in predicted interval nodes
		for(IntervalNode node : this.preIntervalNodes){
			
//			just enumerate every node in gold node array list.
			for(IntervalNode goldNode : this.goldIntervalNodes){
				
//				completely match in span
				if(node.left==goldNode.left && node.right==goldNode.right){
//					correct spans
					result[0]++;
//					correct nuclearity
					if(node.nuclearity == goldNode.nuclearity)
						result[1]++;
//					correct relation, it is nonsense temporarily 
//					because I have not included label information
					if(node.relation.equals(goldNode.relation))
						result[2]++;
					break;
				}// end if clause
			}// end internal loop
		}// end external for loop
		
		return;
	}// end method scan
	
	/**
	 * 
	 * @param heads is head of each node
	 * @param labels is label for each edge
	 * 
	 * note that currentIntervalNodes is empty at the start of function.
	 */
	private void generateIntervals(int[] heads, String[] labels, int left, int right, int center, boolean gold){
		if(left >= right) return;		
//		IntervalNode(int left, int right, int parent,int nuclearity, int center, String relation)
//		search from the right side
		if(right > center){
			int begin = center+1; 
			int end = right;
			int pos = -1;
			for(int i=end; i>=begin; i--){
				if(heads[i]==center){pos = i; break;}
			}
			if(pos < 0){
				System.err.println("Error0: this is not a projective tree.");
				return;
			}
//			search for left boundary
			int leftBoundary = -1;
			for(int i=pos-1; i>=center; i--){
				if(matrix[pos][i] == false){
					leftBoundary = i+1;
					break;
				}
			}
			if(matrix[center][leftBoundary-1]==false || leftBoundary<=center){
				System.err.println("Error1: this is not a projective tree.");
				return;
			}
			
//			add result into array list
//			IntervalNode(int left, int right, int parent,int nuclearity, int center, String relation)
			if(gold == true){
				this.goldIntervalNodes.add(new IntervalNode(left, leftBoundary-1, 
						heads[center], IntervalNode.NUCLEUS, center, IntervalNode.SPAN));
				this.goldIntervalNodes.add(new IntervalNode(leftBoundary, right, 
						center, IntervalNode.SATELLITE, pos, labels[pos]));
			}
			else{
				this.preIntervalNodes.add(new IntervalNode(left, leftBoundary-1, 
						heads[center], IntervalNode.NUCLEUS, center, IntervalNode.SPAN));
				this.preIntervalNodes.add(new IntervalNode(leftBoundary, right, 
						center, IntervalNode.SATELLITE, pos, labels[pos]));
			}
//			call recursively
//			generateIntervals(int[] heads, String[] labels, int left, int right, int center, boolean gold)
			generateIntervals(heads, labels, left, leftBoundary-1, center, gold);
			generateIntervals(heads, labels, leftBoundary, right, pos, gold);
		}// end if clause
//		search from the left side
		else if(right == center){
			int pos = -1;
			for(int i=left; i<center; i++){
				if(heads[i] == center){pos = i; break;}
			}
			if(pos < 0){
				System.err.println("Error2: this is not a projective tree.");
				return;
			}
			
			int rightBoundary = -1;
			for(int i=pos+1; i<=right; i++){
				if(matrix[pos][i]==false){
					rightBoundary = i-1;
					break;
				}
			}
			if(matrix[center][rightBoundary+1]==false || rightBoundary>=right){
				System.err.println("Error3: this is not a projective tree.");
				return;
			}
//			IntervalNode(int left, int right, int parent,int nuclearity, int center, String relation)
			if(gold == true){
				this.goldIntervalNodes.add(new IntervalNode(left, rightBoundary, 
						center, IntervalNode.SATELLITE, pos, labels[pos]));
//				something is wrong here
//				this.goldIntervalNodes.add(new IntervalNode(rightBoundary+1, right,
//						heads[center], IntervalNode.NUCLEUS, center, labels[center]));
				this.goldIntervalNodes.add(new IntervalNode(rightBoundary+1, right,
						heads[center], IntervalNode.NUCLEUS, center, IntervalNode.SPAN));
			}
			else{
				this.preIntervalNodes.add(new IntervalNode(left, rightBoundary, 
						center, IntervalNode.SATELLITE, pos, labels[pos]));
//				something is wrong here
//				this.preIntervalNodes.add(new IntervalNode(rightBoundary+1, right,
//						heads[center], IntervalNode.NUCLEUS, center, labels[center]));
				this.preIntervalNodes.add(new IntervalNode(rightBoundary+1, right,
						heads[center], IntervalNode.NUCLEUS, center, IntervalNode.SPAN));
			}
//			call recursively
//			generateIntervals(int[] heads, String[] labels, int left, int right, int center, boolean gold)
			generateIntervals(heads, labels, left, rightBoundary, pos, gold);
			generateIntervals(heads, labels, rightBoundary+1, right, center, gold);
		}// end else clause
		return;		
	}// end method generateInterals

}// end class MyEvaluator
