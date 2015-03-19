package wl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * 
 * @author Liang Wang
 * @since 2013/9/9
 * @version 2013/10/28
 * 
 * this class is used to extract paragraph information
 * to know which paragraph a given EDU is located in.
 */
public class ParagraphIDExtractor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void extract(File input) throws IOException {
		
//		File input = new File(inputDirectory);
//		if(input.isDirectory() == false){
//			System.err.println("Invalid input directory: "+inputDirectory);
//			return;
//		}
		File[] filelist = input.listFiles();
//		if(filelist.exists()==false || filelist.isDirectory()==true){
//			System.err.println("Invalid parameter.");
//			System.exit(1);
//		}// end if
		
//		Scanner cin = new Scanner(filelist);
//		int currentFile = 0;
//		iterate over all files
		for(int i=0; i<filelist.length; i++){
			if(filelist[i].toString().endsWith(Main.OUT_SUFFIX) == false)
				continue;
			System.err.println("ParagraphID: "+filelist[i].toString()+"...");
			File file = filelist[i];
//			output result into same directory with .para suffix
			FileWriter writer = new FileWriter(file.toString()+Main.PARA_SUFFIX);
			
//			valid file name, just skip it and output some information
//			if(file.exists()==false || file.isDirectory()==true){
//				System.err.println("Invalid file name."+file.toString());
//				continue;
//			}// end if
			
			Scanner cinreader = new Scanner(file);
			int paraID = 0;
			int sentenceID = 0;
			
//			reset delimiter to read blank line, else Scanner will skip blank line.
			cinreader.useDelimiter("\\n");
			while(cinreader.hasNext()){
				String str = cinreader.next();
//				a blank line, increment current paragraph number
				if(str.length() == 0)
					paraID++;					
//				a line containing non-empty sentence, just increment sentence number.
				else{
					writer.write(sentenceID+" "+paraID+"\n");
					writer.flush();
					sentenceID++;
				}// end else				

			}// end internal while loop
		}// end external while loop
		
		System.err.println("Complete.");
		return;
	}// end method main

}// end class ParagraphIDExtractor
