package ziqiangyeah.experiment.RST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
/**
 * 负责输入输出，需要修改目录地址
 * Modified by : Liang Wang
 * @author Cao Ziqiang
 * @version 2013/10/20 
 */
public class Main {
//	static final String INPUT_DIRECTORY="G:\\trainingdis\\";
//	static final String OUTPUT_DIRECTORY="G:\\data\\result\\TRAININGRESULT\\";
	static String INPUT_DIRECTORY = "";
	static String OUTPUT_DIRECTORY = "";
//	static final String INPUT_DIRECTORY="G:\\dis\\";
//	static final String OUTPUT_DIRECTORY="G:\\data\\result\\ALLDIS\\";
	static final String INPUT_SUFFIX=".dis";
	static final String OUTPUT_SUFFIX=".dep";
	public static String[] relation;
	public static int unaryCounter = 0;
	public static boolean closed = false;
	public static HashSet<String> relationSet = new HashSet<String>();
	public static String filename = "";
	
	/**
	 * 
	 * @param args currently you need to specify input path
	 * 
	 * to run this program, you need to change variable INPUT_DIRECTORY,
	 * output result will be in same directory.
	 * and run following command: <br>
	 * <code> java ziqiangyeah.experiemnt.RST.Main input:path1</code>
	 */
	public static void main(String[] args){
		
		if(args.length != 1){
			System.err.println("You are required to give exactly one parameter to specify input path.");
			return;
		}
		
//		parse options
		for(int i=0; i<args.length; i++){
			String[] str = args[i].trim().split(":");
			if(str[0].equalsIgnoreCase("input")){
				INPUT_DIRECTORY = str[1];			
				OUTPUT_DIRECTORY = str[1];
//				System.out.println(str[1]);
			}
		}// end for loop		
		
		File inputDir=new File(INPUT_DIRECTORY);
		Main.relationSet.clear();
		
		if(inputDir.isDirectory() == false){
			System.err.println("Error: Input path must be a valid directory.");
			return;
		}// end if
		
//		iterate for every input file
		for(File file:inputDir.listFiles()){
			filename=file.getName();
			if(!filename.endsWith(INPUT_SUFFIX)){
				continue;
			}
			Main.closed = false;
			System.err.println("dealing with----"+filename);
//			given file name, load dependency tree into memory
			RSTTree tree=RSTLoader.loadDis(file.getAbsolutePath());
			tree.searchTag();
			int[] parentList=tree.getParentList();
			String depName=OUTPUT_DIRECTORY+filename.substring(0, filename.length()-INPUT_SUFFIX.length())+OUTPUT_SUFFIX;
			saveResult(parentList, depName);
		}
		//RSTTree tree=RSTLoader.loadDis("F:\\RST\\TEST\\wsj_0602.out.dis");
		//tree.showTree();
		
		System.err.println("Number of n-ary relations: "+Main.unaryCounter);
//		output some additional information
//		test data: 19/38, training data: 155/342, 
//		total: 179/385(some file not included in training data set) or 174/380
//		System.err.println("Number of files have more than 2 childs: "+Main.unaryCounter);
//		unique relation number: 112, 
//		unique relation number in training data: 110
//		these two labels appear only in test data: elaboration-process-step-e, background-e
//		System.err.println("Number of unique relations: "+Main.relationSet.size());
//		Iterator<String> it = Main.relationSet.iterator();
//		while(it.hasNext())
//			System.err.println(it.next());
		
		return;
	}// end method main
	
	/**
	 * 
	 * @param parentList a list of parent for every node
	 * @param depName file name to store final result
	 */
	private static void saveResult(int[] parentList, String depName) {
		try {
			PrintStream ps=new PrintStream(new FileOutputStream(depName));
			for(int i=1;i<parentList.length;i++){
				ps.println(i+"\t"+parentList[i]+"\t"+relation[i]);
				
//				1189.dis is a special case, node 9 has been revised.
				if(relation[i].equals("span"))
					System.err.println(Main.filename+" "+i);
				if(Main.relationSet.contains(relation[i])==false 
//						&& relation[i].equals("span")==false
					){					
					Main.relationSet.add(relation[i]);
				
				}// end if clause
			}// end for loop
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// end method saveResult
}// end class Main
