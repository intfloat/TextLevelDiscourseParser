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
 * <p>
 *  this class is used to transform feature files from detailed version
 *  to much simpler version. This program will delete all extra information
 *  to tell what the feature means specifically.
 *  </p>
 *  
 *  <p>to use this class, just execute following command:</p>
 *  <code> java FeatureIntegration edusout.txt</code>
 *  
 *  <p>output will be in same directory with original data with suffix ".feat"</p>
 *
 */
public class FeatureIntegration {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void featureIntegration(File inputDirectory) throws IOException {

		File[] fileList = inputDirectory.listFiles();		
		ArrayList<Integer> counterArray = EDUCounter.countEDU(inputDirectory);
//		Vector<Integer> counterVector = new Vector<Integer>();
//		counterVector.clear();
//		Scanner cin = new Scanner(new File("d:\\result\\eduCounter.txt"));
//		Scanner cin = new Scanner(new File("d:\\result\\testEDUCounter.txt"));
//		cin.useDelimiter("\\n");
//		while(cin.hasNextInt()){
//			String str = cin.nextLine();
//			System.err.println("string: "+str);
//			counterVector.add(cin.nextInt());			
//		}// end while loop
		
//		cin = new Scanner(fileList);
		int current = 0;
		for(int num=0; num<fileList.length; num++){
			
			if(fileList[num].toString().endsWith(Main.PRIM_SUFFIX) == false)
				continue;
			
//			output some hints
			System.err.println("Feature Integration:"+fileList[num].toString()+"...");
			
			String name = fileList[num].toString();
			File file = new File(name);
			Scanner reader = new Scanner(file);
			FileWriter writer = new FileWriter(name+Main.FEAT_SUFFIX);
			
//			output total number of edus in this file
			writer.write(counterArray.get(current)+"\n");
			
//			set new delimiter
			reader.useDelimiter("\\n");
			while(reader.hasNext()){
				String index = reader.next().trim();			
				
//				if there is no bug, this will not happen.
				if(index.split("\\s++").length != 2){
					System.err.println("string: "+index);
					System.err.println("Error occurs in "+name);
					break;
				}
				writer.write(index+"\n");
				String feature = "";
				while(reader.hasNext()){
					String str = reader.next().trim();
//					end of one pair
					if(str==null || str.length()==0 || reader.hasNext()==false){
						writer.write(feature.trim()+"\n");
						break;
					}// end if
//					feature.append(str.split("\\s++")[0]+" ");
					feature += str.split("\\s++")[0]+" ";					
				}// end internal loop
				
			}// end internal while loop
			
//			update index of current file
			current++;
			writer.flush();
			
		}// end external while loop
	}// end method main

}// end class FeatureIntegration
