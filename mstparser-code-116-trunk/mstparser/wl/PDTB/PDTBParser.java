package wl.PDTB;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 
 * @author Liang Wang
 * @version 2013/11/26
 *
 */
public class PDTBParser {
	
	public static final String PDTBSuffix = ".mrg";	
	public PDTBTreeLoader loader;
	/**
	 * 
	 * @param file is a file with suffix "mrg"
	 * @return a PDTBTree object, then we can call method of PDTBTree
	 *  to query corresponding information
	 * @throws FileNotFoundException 
	 */
	public PDTBTree parse(File file) throws FileNotFoundException {
		// TODO Auto-generated method stub
		PDTBTree tree = new PDTBTree();
		loader = new PDTBTreeLoader();
//		invalid parameter
		if(file.toString().endsWith(PDTBSuffix)==false
				|| file.isDirectory()){
			System.err.println("Invalid file name: "+file.toString());
			System.exit(1);
		}		
		
		tree = loader.loadTree(file);
//		for(int i=0; i<loader.leafNodes.size(); i++)
//			System.out.print(loader.leafNodes.get(i).posTag+"_"+loader.leafNodes.get(i).word+" ");
//		System.out.println();
		return tree;
	}// end method parse	
	

}// end class PDTBParser
