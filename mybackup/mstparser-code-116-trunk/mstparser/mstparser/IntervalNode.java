package mstparser;

/**
 * 
 * @author Liang Wang
 * @since 2013/10/13
 * @version 2013/10/13
 *
 */
public class IntervalNode {
	
	public static final int NUCLEUS = 1;
	public static final int SATELLITE = 2;
	public static final int UNCERTAIN = 0;
	public static final String SPAN = "SPAN"; 
	public int parent;
	public String relation;
	public int left;
	public int right;
	public int nuclearity;
	public int center;
	
//	empty constructor
	public IntervalNode(){
		this.parent = -1;
		this.relation = "";
		this.left=this.right=-1;
		this.nuclearity = IntervalNode.UNCERTAIN;
		this.center = -1;
	}// end first constructor
	
//	another constructor containing all essential information
	public IntervalNode(int left, int right, int parent, 
			int nuclearity, int center, String relation){
		this.left = left;
		this.right = right;
		this.parent = parent;
		this.nuclearity = nuclearity;
		this.center = center;
		this.relation = relation;
	}// end second constructor
	
	/**
	 * 
	 * @param first is first given IntervalNode object
	 * @param second is second given IntervalNode object
	 * @return a new Interval node merged from two parameters
	 */
	public static IntervalNode merge(IntervalNode first, IntervalNode second){
		IntervalNode result = new IntervalNode();
		
		result.left = first.left;
		result.right = second.right;
		result.nuclearity = IntervalNode.UNCERTAIN;
//		first node is nucleus
		if(first.nuclearity == IntervalNode.NUCLEUS){
//			relation information is stored in satellite node
			result.relation = second.relation;
			result.center = first.center;
			result.parent = first.parent;
		}// end if
		else if(second.nuclearity == IntervalNode.NUCLEUS){
			result.relation = first.relation;
			result.center = second.center;
			result.parent = second.parent;
		}// end else if
		else{
			System.err.println("Error: can not find any nucleus node.");
		}// end else
		
		return result;
	}// end method merge

}// end class IntervalNode
