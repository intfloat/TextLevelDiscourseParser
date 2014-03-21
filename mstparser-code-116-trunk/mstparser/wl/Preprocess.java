package wl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;

import opennlp.tools.util.InvalidFormatException;

/**
 * 
 * @author Liang Wang
 * @since 2013/9/11
 * @version 2013/10/28
 * 
 * this class is used to generate .pre file, containing all information
 * needed to extract feature in the following format:<br> 
 * <code>
 * sentenceID lineID offset paraID
 * rawString <br> rawTokens <br> rawTags <br> filteredTokens <br> filteredTags 
 * </code><br><br>
 * 
 * to use this class, you can execute following command:<br>
 * <code>java Preprocess out.txt</code>
 *
 */
public class Preprocess {

	/**
	 * @param inputDirectory is an input directory
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public static void process(File inputDirectory) throws InvalidFormatException, IOException {

		File[] filelist = inputDirectory.listFiles();
//		Scanner cin = new Scanner(filelist);
		Vector<Integer> sentenceLength = new Vector<Integer>();
		
//		just to instantiate some static variables, although it looks a little bit strange ><
		EDU edu = new EDU();
//		int cur = 1;
		for(int num=0; num<filelist.length; num++){
			
			if(filelist[num].toString().endsWith(Main.OUT_SUFFIX) == false)
				continue;
			
			System.err.println("Preprocess: "+filelist[num].toString());
//			counter is a variable just to check if there is any inconsistency error in given treebank
			int counter = 0;
			String name = filelist[num].toString();
//			get current file to process
			File file = new File(name);
//			this is target file, program will output result through this FileWriter
			FileWriter writer = new FileWriter(name+Main.PRE_SUFFIX);
			
//			to judge if two edus are in same paragraph, we need to include extra information
//			from file with suffix ".para"
			File paraInfoFile = new File(name+Main.PARA_SUFFIX);			
			Scanner paraReader = new Scanner(paraInfoFile);
			Vector<Integer> paraVector = new Vector<Integer>();
			paraVector.clear();
			while(paraReader.hasNextLine()){
				String[] values = paraReader.nextLine().trim().split("\\s++"); 
				paraVector.add(new Integer(values[1].trim()));
			}
			
//			define and instantiate some variables
			sentenceLength.clear();
			Scanner reader = new Scanner(file);
//			a vector to store all tokens, it is equivalent to a 2D array.
			Vector<String[]> tokensVector = new Vector<String[]>();
//			a vector used to store all tags, it is equivalent to a 2D array.
			Vector<String[]> tagsVector = new Vector<String[]>();
			tokensVector.clear(); tagsVector.clear();
			
			while(reader.hasNextLine()){
				String rawString = reader.nextLine().trim();
				if(rawString.length() == 0)
					continue;
//				tokenize and tag with tools provided by opennlp
				String[] str = EDU.tokenizer.tokenize(rawString);			
				String[] tags = EDU.tagger.tag(str);
				tokensVector.add(str); tagsVector.add(tags);
				counter += str.length;
				sentenceLength.add(str.length);
			}// end while loop
			
//			just to record current value of variable counter
			int tmp1 = counter;
			
			
//			compare if tokens number is equal
			counter = 0;			
			file = new File(name+Main.EDUS_SUFFIX);
			reader = new Scanner(file);
			
//			define and instantiate some variables
			int sentenceID = 0;
			int lineID = 0;
			int offset = 0;
			int paraID = 0;
			int pointer = 0;
			int sentencePointer = 0;
			boolean flag = true;
			
			while(reader.hasNextLine()){
				String rawString = reader.nextLine().trim();
				String[] str = EDU.tokenizer.tokenize(rawString);
				counter += str.length;
				
//				a valid line
				if(tokensVector.get(pointer).length <= sentencePointer){
					pointer++;
					sentencePointer = 0;
					sentenceID++;
					paraID = paraVector.get(sentenceID);
					offset = 0;
				}// end if 
				
//				error occurs because words from different sentence are merged into one edu
				if(sentencePointer+str.length > tokensVector.get(pointer).length){
//					output error information for debug
					if(flag == true){
						System.err.println("file name: "+name+" line number "+lineID);
						System.err.println("out of bounds exception! sentencePointer: "+sentencePointer
								+" length: "+tokensVector.get(pointer).length+" str length "+str.length);
					}
//					change value of flag to guarantee that only one error information
//					is given for each given input file.
					flag = false;
					continue;
				}// end if
				
//				output corresponding information using FileWriter
				writer.write(sentenceID+" "+lineID+" "+offset+" "+paraID+"\n");
//				raw string
				writer.write(rawString+"\n");
//				raw tokens
				for(int i=sentencePointer; i<sentencePointer+str.length; i++)
					writer.write(tokensVector.get(pointer)[i]+" ");
				writer.write("\n");
//				raw tags
				for(int i=sentencePointer; i<sentencePointer+str.length; i++)
					writer.write(tagsVector.get(pointer)[i]+" ");
				writer.write("\n");
				
//				filtered tokens
				for(int i=sentencePointer; i<sentencePointer+str.length; i++){
					String s = tokensVector.get(pointer)[i];
					String t = tagsVector.get(pointer)[i];
//					pay attention, there are many non-standard tags or tokens such as "$" or "%"
					if(Pattern.matches(".*[a-zA-Z_0-9].*", s) && Pattern.matches(".*[a-zA-Z_0-9].*", t))
						writer.write(s+" ");
				}
				writer.write("\n");
//				filtered tags
				for(int i=sentencePointer; i<sentencePointer+str.length; i++){
					String s = tokensVector.get(pointer)[i];
					String t = tagsVector.get(pointer)[i];
					if(Pattern.matches(".*[a-zA-Z_0-9].*", s) && Pattern.matches(".*[a-zA-Z_0-9].*", t))
						writer.write(tagsVector.get(pointer)[i]+" ");
				}
				writer.write("\n");
				writer.flush();
				
				sentencePointer += str.length;
				offset++; lineID++;
				
				sentenceLength.add(str.length);
			}// end while loop, this loop is too long... sorry about that

			/**
			 * error occurs because words from different sentence are merged into one edu
			 */
			if(tmp1 != counter){
				System.err.println(file.toString());
				System.err.println("length in .out file: "+tmp1+"\n");
				System.err.println("length in .edus file: "+counter+"\n");
			}// end if
			
		}// end external while loop
		return;
	}// end method process

}// end class Preprocess
