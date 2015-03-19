package wl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Liang Wang
 * @since 2013/9/23
 * @version 2013/10/28
 *
 * <p>this class is used to extract information about how many edus in 
 * each .edus file.
 */
public class EDUCounter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static ArrayList<Integer> countEDU(File inputDir) throws IOException {
//		File input 
		File[] fileList = inputDir.listFiles();
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.clear();
//		if(fileList.exists()==false || fileList.isDirectory()){
//			System.err.println("Invalid parameter.");
//			System.exit(1);
//		}
		
//		Scanner cin = new Scanner(fileList);
//		int current = 0;
//		FileWriter writer = new FileWriter(new File(inputDir.toString()+Main.EDU_COUNTER));
		for(int i=0; i<fileList.length; i++){
			
			if(fileList[i].toString().endsWith(Main.EDUS_SUFFIX) == false)
				continue;
			
//			System.err.println("Processing: "+fileList[i].toString()+"...");
//			String name = cin.nextLine().trim();
			File file = fileList[i];
			int counter = 0;
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine()){
				String str = reader.nextLine().trim();
				if(str!=null && str.length()>0)
					counter++;
			}// end internal while loop
			result.add(counter);
//			writer.write(counter+"\n");
//			System.out.println(counter);
		}// end external while loop
		
//		writer.flush();
		return result;
	}// end method main

}// end class EDUCounter
