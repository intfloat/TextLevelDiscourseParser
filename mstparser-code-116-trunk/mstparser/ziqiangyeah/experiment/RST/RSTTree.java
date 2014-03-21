package ziqiangyeah.experiment.RST;

import java.util.*;

/**
 * 表达RST树,根据文件的层级表示法，用二维数组来定位一个节点，这样寻找父节点和子节点变得十分容易
 * m_LevelNodeList.get(i).get(j)代表树的第i层第j个节点，它的父节点一定在第i-1层，子节点一定在i+1层
 * 最重要的函数是setTag，用递归的方式求出每个节点的tag
 * getParentList对每个叶节点向其父节点搜索对应tag,如果想同时得到relation修改此函数即可
 * 
 * Modified by : Liang Wang
 * @author Cao Ziqiang
 * @version 2013/10/20
 */
public class RSTTree {
//	entire tree is stored in this 2-D array list
	ArrayList<ArrayList<RSTNode>> m_LevelNodeList;
	
//	just an empty constructor
	public RSTTree(){}
	
	/**
	 * 
	 * @param level is level of node in tree
	 * @param spanLeft is left boundary of tree
	 * @param spanRight is right boundary of tree
	 * @param type is a binary variable which can be NUCLEUS or SATELLITE
	 * @param relation is the relation when this node is a dependent
	 */
	public void addNode(int level,int spanLeft,int spanRight,String type,String relation){
		RSTNode node=new RSTNode();
		node.SpanLeft=spanLeft;
		node.SpanRight=spanRight;
//		tag will be calculated in later stage
		node.Tag=RSTNode.UNKNOWN;
		node.RelationType=relation;
		ArrayList<RSTNode> nodeList;
		if(level<m_LevelNodeList.size()){
			nodeList=m_LevelNodeList.get(level);
		}else{
			nodeList=new ArrayList<RSTNode>();
			m_LevelNodeList.add(nodeList);
		}
		int position=nodeList.size();
		setParent(level, position,spanLeft, spanRight, node,type);
		nodeList.add(node);
	}// end method addNode

//	this is a function used to get parent list for every node
	public int[] getParentList(){
//		this is the total number of EDUs
		int size=m_LevelNodeList.get(0).get(0).SpanRight;
		int[] parentList=new int[size+1];
		Main.relation = new String[size+1];
		
//		iterate over every level
		for(int i=m_LevelNodeList.size()-1;i>=0;i--){
			ArrayList<RSTNode> nodeList=m_LevelNodeList.get(i);
//			iterate over every node in given level
			for(int j=0;j<nodeList.size();j++){
				RSTNode node=nodeList.get(j);
				if(!node.isLeaf()) continue;
				
			
				int leafTag=node.SpanLeft;
				int parentTag=RSTNode.UNKNOWN;
				int parentPosition=node.Parent;
				String preRelation = node.RelationType;
				
//				iterate along the way to root of tree
				for(int k=i-1;k>=0;k--){
					RSTNode parent=m_LevelNodeList.get(k).get(parentPosition);
//					this is not wanted parent node
					if(parent.Tag==leafTag){
						parentPosition=parent.Parent;
						preRelation = parent.RelationType;
						continue;
					}
					parentTag=parent.Tag;
//					get relation for this directed edge
					Main.relation[leafTag] = preRelation;
					break;
				}
//				there is no parent
				if(parentTag==RSTNode.UNKNOWN){
					parentTag=RSTNode.ROOT_TAG;
					Main.relation[leafTag] = node.ROOT;
				}
				parentList[leafTag]=parentTag;
			}// end internal for loop
		}// end external for loop
		return parentList;
	}// end method getParentList
	
	/**
	 * 
	 * @param level is level of node in tree
	 * @param position is current position of this node
	 * @param spanLeft is left boundary of tree
	 * @param spanRight is right boundary of tree
	 * @param node is child node needed to find its parent
	 * @param type is a binary variable which can be NUCLEUS or SATELLITE
	 * 
	 */
	private void setParent(int level,int position, int spanLeft, int spanRight, RSTNode node,String type) {
//		parent of given node must be in level-1
		ArrayList<RSTNode> latentParentList=m_LevelNodeList.get(level-1);
		int size=latentParentList.size();
//		look for every node in higher level, return if once find its parent
		for(int i=size-1;i>=0;i--){
			RSTNode latentParent=latentParentList.get(i);
//			latentParent is its true parent if inNode function returns true
			if(latentParent.inNode(spanLeft, spanRight)){
				node.Parent=i;
				if(type.equals(RSTNode.NUCLEUS)){
					latentParent.NChildList.add(position);
				}else{
					latentParent.SChildList.add(position);
				}
				break;
			}// end if clause
		}// end for loop
		
		return;
	}// end method setParent
	
	/**
	 * 
	 * @param spanEnd is number of EDUs of the entire file
	 */
	public void addRoot(int spanEnd){
		RSTNode node=new RSTNode();
		node.SpanLeft=1;
		node.SpanRight=spanEnd;
//		root has no head
		node.Parent=-1;
		node.RelationType=RSTNode.ROOT;
		m_LevelNodeList=new ArrayList<ArrayList<RSTNode>>();
		ArrayList<RSTNode> nodeList=new ArrayList<RSTNode>();
		nodeList.add(node);
		m_LevelNodeList.add(nodeList);
	}// end method addRoot
	
//	print contents in tree
	public void showTree(){
		int size=m_LevelNodeList.size();
		for(int i=0;i<size;i++){
			System.out.println("level:"+i);
			ArrayList<RSTNode> nodeList=m_LevelNodeList.get(i);
			int subSize=nodeList.size();
			System.out.println("size:"+subSize);
			for(int j=0;j<subSize;j++){
				RSTNode node=nodeList.get(j);
				System.out.println(node.SpanLeft+"--"+node.SpanRight+"\t"+node.Parent+"\t"+node.RelationType);
			}// end internal for loop
		}// end external for loop
	}// end method showTree
	
	public void searchTag(){
		setTag(0,0);
	}// end method searchTag
	
	/**
	 * 
	 * @param level is current level of the node
	 * @param position can be used to get node together with level information
	 * 
	 * subtree with a root in given level and given position
	 * will be tagged through this function.
	 */
	private void setTag(int level,int position){
		RSTNode node=m_LevelNodeList.get(level).get(position);
		
//		leaf node is boundary case
		if(node.isLeaf()){
			node.Tag=node.SpanLeft;
			return;
		}
		
//		recursively set tags for its child
		for(int subPosition:node.NChildList){
			setTag(level+1,subPosition);
		}
		//设为最左子树的tag,不一定是左子树，一定是第一个nucleus
//		tag actually indicate the center of a interval span
		if(node.Tag==RSTNode.UNKNOWN){
			int leftChildPosition=node.NChildList.get(0);

            // test if there is any n-ary branches
            int childNumber = node.NChildList.size()+node.SChildList.size();
            if(childNumber>2 && Main.closed==false){
//                System.out.println(Main.filename+" "+node.SpanLeft+" "+node.SpanRight);
//                System.out.println("nucleus: "+node.NChildList.size()+" sate: "+node.SChildList.size());
            	Main.unaryCounter++;
//            	Main.closed = true;
//            	Main.closed = false;
            }

//          tag of current span is set to the tag of its first nucleus node
			node.Tag=m_LevelNodeList.get(level+1).get(leftChildPosition).Tag;
		}
		for(int subPosition:node.SChildList){
			RSTNode schild=m_LevelNodeList.get(level+1).get(subPosition);
			//satellite节点tag为其父节点
			//schild.Tag=node.Tag;
			setTag(level+1,subPosition);
		}// end for loop
		
		return;
	}// end method setTag
	
}// end class RSTTree
