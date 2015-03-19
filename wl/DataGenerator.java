package wl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class DataGenerator {
	
	
 //	private static Scanner reader;
	private static ArrayList<Integer> eduCounter = new ArrayList<Integer>();
//	private static Scanner dep;
	private static ArrayList<File> depFilelist = new ArrayList<File>();
	private static Scanner relationReader;
	private static HashMap<Integer, String> map = new HashMap<Integer, String>();
	private static HashMap<String, String> transform = new HashMap<String, String>();
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		boolean labeled = false;
		boolean coarseRelation = false;
		boolean train = false;
		int current;
		for(int i=0; i<args.length; i++){
			String[] option = args[i].split(":");
			if(option[0].equals("labeled"))
				labeled = Boolean.parseBoolean(option[1]);
			else if(option[0].equals("coarse"))
				coarseRelation = Boolean.parseBoolean(option[1]);
			else if(option[0].equals("train"))
				train = Boolean.parseBoolean(option[1]);
			else
				System.err.println("Invalid option: "+args[i]);			
		}		
		
		eduCounter.clear(); depFilelist.clear();
		File inputDir = null;
		if(train == true){
			inputDir = new File(Main.TRAIN_PATH);
			eduCounter = EDUCounter.countEDU(inputDir);
			File[] files = inputDir.listFiles();
			for(File file : files){
				if(file.toString().endsWith(Main.DEP_SUFFIX))
					depFilelist.add(file);
			}// end for loop
			current = 0;
		}
		else{
			inputDir = new File(Main.TEST_PATH);
			current = EDUCounter.countEDU(new File(Main.TRAIN_PATH)).size();
			eduCounter = EDUCounter.countEDU(inputDir);
			File[] files = inputDir.listFiles();
			for(File file : files){
				if(file.toString().endsWith(Main.DEP_SUFFIX))
					depFilelist.add(file);
			}// end for loop
		}
		
//		their size should be equal
		if(eduCounter.size() != depFilelist.size()){
			System.err.println("Error: number of edus file is not equal to number of dep file.");
			System.exit(1);
		}
		
		relationReader = new Scanner(new File(Main.RELATION_PATH));
		map.clear(); transform.clear();
		
//		transform between two categories of relations, 110 or 18 labels
		parseRelations();
		
		for(int num=0; num<eduCounter.size(); num++){
			
			int N = eduCounter.get(num);
			Scanner depReader = new Scanner(depFilelist.get(num));			
			String str = "";
			for(int i=0; i<N; i++)
				str += current+" ";
//			first line, just a series of number to indicate which file
			System.out.println(str.trim());
			str = "";
			
			for(int i=0; i<N; i++)
				str += "NN ";
//			second line, it should be POS tags, but we will not use it,
//			so just meaningless NN will be fine.
			System.out.println(str.trim());
			str = "";
			
			String relation = "";
			for(int i=0; i<N; i++){
				String[] number = depReader.nextLine().trim().split("\\s++");
				
				if(number.length != 3){
					System.err.println("Error: dependency format is wrong! in "+
							depFilelist.get(num).toString());
					System.exit(1);
				}
				int head = Integer.parseInt(number[1]);				
				str += head+" ";
				if(coarseRelation == false)
					relation += number[2]+" ";
				else
					relation += transform.get(number[2])+" ";
			}// end for loop
			
//			it will be third line in labeled cases, and won't appear in unlabeled case
			if(labeled == true)
				System.out.println(relation.trim());			
//			it will be third line in unlabeled cases and fourth line in labeled cases
			System.out.println(str.trim());
			
			if(depReader.hasNextLine()){
				System.err.println("Error: there is extra lines in "+depFilelist.get(num).toString());
				System.exit(1);
			}
			
			current++;
			
			System.out.println();
		}// end while loop		
		return;
		
	}// end method main
	
	/**
	 * this function is used to parse relation reflection
	 */
	private static void parseRelations(){
		String[] number = relationReader.nextLine().trim().split("\\s++");
		int numOfCoarse = Integer.parseInt(number[0]);
		int numOfAccurate = Integer.parseInt(number[1]);
		
		for(int i=0; i<numOfCoarse; i++){
			String[] pair = relationReader.nextLine().trim().split("\\s++");
			map.put(Integer.parseInt(pair[0]), pair[1]);
		}
		
		for(int i=0; i<numOfAccurate; i++){
			String[] pair = relationReader.nextLine().trim().split("\\s++");
//			System.err.println(pair[0]);
			if(pair[1].equalsIgnoreCase("ROOT"))
				transform.put(pair[0], pair[1]);
			else
				transform.put(pair[0], map.get(Integer.parseInt(pair[1])));
		}
		
//		System.err.println(map.size());
//		System.err.println(transform.size());
		
		return;
	}// end method parseRelations

}// end class RandomTrainDataGenerator
