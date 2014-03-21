package ziqiangyeah.experiment.RST;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * 按行读取dis文件，文件本身根据括号前空格数量分好层次
 * 提取每个节点的span,N/S,relation信息，加入RSTTree中
 * 
 * modified by : Liang Wang
 * @author Cao Ziqiang * 
 * @version 2013/10/20
 */
public class RSTLoader {
	static final String NCLEUS_TAG="( Nucleus";
	static final String SATELLITE_TAG="( Satellite";
//	static final String DATA_DIR="F:/RST/TRAINING";
	static final String SPAN_TAG="(span ";
	static final String LEAF_TAG="(leaf ";
//	static final String SURFIX=".dis";
	
//	relation should be on dependent because head node may have
//	several dependents, but dependent only have at most one head
	static final String RELATION_TAG="(rel2par ";
	
	/**
	 * 
	 * @param fileName is the absolute path of a file with ".dis" suffix
	 * @return a RSTTree object
	 */
	public static RSTTree loadDis(String fileName){
		RSTTree tree=new RSTTree();
		try {
			Scanner sc = new Scanner(new FileInputStream(fileName));
			String rootInfo=sc.nextLine();
			int[] numList=getSpanNumbers(rootInfo);
			tree.addRoot(numList[1]);
			
//			iterate for every line in given file
			while(sc.hasNextLine()){
				String lineInfo=sc.nextLine();
				if(!lineInfo.contains(RELATION_TAG)) continue;
				int level=getLevel(lineInfo);
				String type;
				if(lineInfo.contains(NCLEUS_TAG)){
					type=RSTNode.NUCLEUS;
				}else{
					type=RSTNode.SATELLITE;
				}
				int spanLeft,spanRight;
				
//				it is an internal node
				if(lineInfo.contains(SPAN_TAG)){
					int[] spanList=getSpanNumbers(lineInfo);
					spanLeft=spanList[0];
					spanRight=spanList[1];
				}// end if
//				it is a leaf node
				else{
					spanLeft=spanRight=getLeafNumber(lineInfo);				
				}// end else
				String relation=getRelation(lineInfo);
				tree.addNode(level, spanLeft, spanRight, type,relation);
			}// end while loop
			
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tree;
	}// end method loadDis
	
	//获取节点层次, this takes advantage of indentation information
	public static int getLevel(String lineInfo){
		int start=lineInfo.indexOf('(');
		return start/2;
	}
	
	//获取节点relation
	public static String getRelation(String lineInfo){
		int start=lineInfo.indexOf(RELATION_TAG);
		int end=lineInfo.indexOf(')',start);
		
		if(start<0 || end<0){
			System.err.println("Error: there is no relation found.");
			return null;
		}// end if
		
		return lineInfo.substring(start+RELATION_TAG.length(),end);
	}
	
	//获取span的起止
	public static int[] getSpanNumbers(String lineInfo){
		int[] numList=new int[2];
		int start=lineInfo.indexOf(SPAN_TAG);
		int end=lineInfo.indexOf(')',start);
		
		if(start<0 || end<0){
			System.err.println("Error: there is no span found.");
			return null;
		}// end if
		
		String[] numInfo=lineInfo.substring(start+SPAN_TAG.length(),end).split(" ");
		numList[0]=Integer.parseInt(numInfo[0]);
		numList[1]=Integer.parseInt(numInfo[1]);
		return numList;
	}
	//获取叶节点的标记
	public static int getLeafNumber(String lineInfo){
		int start=lineInfo.indexOf(LEAF_TAG);
		int end=lineInfo.indexOf(')',start);
		return Integer.parseInt(lineInfo.substring(start+LEAF_TAG.length(),end));
	}
	
}// end class RSTLoader
