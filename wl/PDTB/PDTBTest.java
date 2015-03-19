package wl.PDTB;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author Liang Wang
 * @version 2013/11/26
 *
 */
public class PDTBTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
//		parse a tree
		PDTBParser parser = new PDTBParser();
		File file = new File("g:\\06\\");
		File[] str = file.listFiles();
		for(File cur : str){
			if(cur.toString().endsWith(".mrg")==false) continue;
			System.err.println("Processing "+cur.toString());
//			I think it works pretty well
			PDTBTree tree = parser.parse(cur);
//			show corresponding structure of PDTBTree to verify if it is correct
//			tree.showTree();
		}
		return;
	}// end method main

}// end class PDTBTest
