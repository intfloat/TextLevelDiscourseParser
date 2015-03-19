package wl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import opennlp.tools.util.InvalidFormatException;
import wl.PDTB.PDTBParser;


/**
 * 
 * @author Liang Wang
 * @since 2013/9/4
 * @version 2013/10/28
 * 
 * this class contains entry point 
 *
 */
public class Main {


//	public static HashMap<String, Integer> frontWordPair = null;
//	public static HashMap<String, Integer> backWordPair = null;
//	public static HashMap<String, Integer> twoFrontWordPair = null;
//	public static HashMap<String, Integer> twoBackWordPair = null;
//	public static HashMap<String, Integer> frontWord = null;
//	public static HashMap<String, Integer> backWord = null;
	public static String TRAIN_DIR = "";
	public static String TEST_DIR = "";
	public static final String EDUS_SUFFIX = ".edus";
	public static final String OUT_SUFFIX = ".out";
	public static final String FEAT_SUFFIX = ".feat";
	public static final String DEP_SUFFIX = ".dep";
	public static final String PRE_SUFFIX = ".pre";
	public static final String DIS_SUFFIX = ".dis";
	public static final String PARA_SUFFIX = ".para";
	public static final String PRIM_SUFFIX = ".prim";
	public static final String SIM_SUFFIX = ".sim";
	public static final String SYN_SUFFIX = ".mrg";
//	public static final String EDU_COUNTER = "eduCounter.txt";
	public static final String TRAIN_PATH = ".\\traindata\\";
	public static final String TEST_PATH = ".\\testdata\\";
	public static final String RELATION_PATH = ".\\relations.txt";
	public static PDTBParser pdtbParser = new PDTBParser(); 
	public static HashMap<Integer, Integer> featureCounter = null;
	public static HashMap<Integer, String> reducedFeatures = null;
	public static boolean outputFeatures;
	public static final int THRESHOLD = 0;
	
	/**
	 * @param args should be input directory path.<br><br>
	 * <code>
	 * 		java Main train:traindata test:testdata<br>
	 * </code><br>
	 * 
	 * 	
	 * @throws IOException if any error occurs during writing data into file. 
	 * 
	 */
	public static void main(String[] args) throws IOException,InvalidFormatException {
		// TODO Auto-generated method stub
		File input = null;
		Main.featureCounter = new HashMap<Integer, Integer>();
		Main.reducedFeatures = new HashMap<Integer, String>();
		Main.featureCounter.clear();		
		Main.reducedFeatures.clear();
		
//		there should be at least one argument
		if(args.length != 2){
			System.err.println("You are required to specify exactly two input path like train:train test:test.");
			System.exit(1);
		}
		
//		parse parameters
		for(int i=0; i<args.length; i++){
			String[] str = args[i].trim().split(":");
			if(str[0].equalsIgnoreCase("train"))
				TRAIN_DIR = str[1];
			if(str[0].equalsIgnoreCase("test"))
				TEST_DIR = str[1];
		}
		
//		create an array to hold all files
		ArrayList<File> fileArray = new ArrayList<File>();
//		ArrayList<Integer> counterArray = EDUCounter.countEDU(new File(Main.TRAIN_PATH));
//		ArrayList<Integer> counterArray2 = EDUCounter.countEDU(new File(Main.TEST_PATH));
//		for(int i=0; i<counterArray2.size(); i++)
//			counterArray.add(counterArray2.get(i));
		fileArray.clear();
		File inputDir = new File(TRAIN_DIR);
//		invalid directory path
		if(inputDir.isDirectory() == false){
			System.err.println("Invalid input directory path: "+TRAIN_DIR);
			System.exit(1);
		}
//		add all file path ends with "edus"
		for(File file : inputDir.listFiles()){
			if(file.toString().endsWith(Main.EDUS_SUFFIX))
				fileArray.add(file);
		}// end for loop
		
//		for test data directory
		inputDir = new File(TEST_DIR);
		if(inputDir.isDirectory() == false){
			System.err.println("Invalid input directory path: "+TEST_DIR);
			System.exit(1);
		}
		for(File file : inputDir.listFiles()){
			if(file.toString().endsWith(Main.EDUS_SUFFIX))
				fileArray.add(file);
		}// end for loop
		
//		do preparing working including extract paragraph ID, preprocess text
		prepare();
		
//		just for test
//		if(INPUT_DIR.length() > 1)
//			return;
		
		
//		Vector<String> listVector = new Vector<String>();
//		listVector.clear();
//		Scanner cin = new Scanner(filelist);
//		while(cin.hasNextLine()){
//			listVector.add(cin.nextLine().trim());
//		}
		
//		int cur = 0;
//		iterate for each input file
		Main.outputFeatures = false;
		for(int i=0; i<fileArray.size(); i++){
			
//			if(filelist[i].toString().endsWith(EDUS_SUFFIX) == false)
//				continue;
			
			System.err.println("Feature Extraction 1: "+fileArray.get(i).toString()+"...");
//			System.out.println("Feature Extraction: "+fileArray.get(i).toString()+"...");
			input = fileArray.get(i);
//			input parameter should not be a directory or invalid path
//			if(input.exists()==false || input.isDirectory()==true){
//				System.err.println("parameter "+path+" is not a valid file path.");
//				continue;
//			}
			
			String dependency = input.toString().replace(EDUS_SUFFIX, DEP_SUFFIX);
			FeatureExtractor.parents = getParents(new File(dependency));
			
//			get a series of binary string representing features, every line pair should
//			correspond to one feature string, so I need to modify this.
			FeatureExtractor.getFeatureString(input);
			
		}// end for loop		
		
		Main.outputFeatures = true;
		for(int i=0; i<fileArray.size(); i++){			
			System.err.println("Feature Extraction 2: "+fileArray.get(i).toString()+"...");
			input = fileArray.get(i);
			String dependency = input.toString().replace(EDUS_SUFFIX, DEP_SUFFIX);
			FeatureExtractor.parents = getParents(new File(dependency));
			FeatureExtractor.getFeatureString(input);			
		}// end for loop	
		
//		FeatureIntegration.featureIntegration(new File(TRAIN_DIR));
//		FeatureIntegration.featureIntegration(new File(TEST_DIR));
		
//		130717 features
		System.err.println("Number of original features: "+FeatureExtractor.allFeatures.dict.getFeatureNumber());
//		
		FileWriter writer = new FileWriter("featuresReduced.log");
		HashMap<Integer, String> map = FeatureExtractor.allFeatures.dict.featMap;
		int featNum = map.size();
		for(int i=0; i<featNum; i++){
			writer.write(i+": "+map.get(i)+"   times: "+Main.featureCounter.get(i)+"\n");
		}
		writer.flush();
		System.err.println("Number of selected features: "+Main.reducedFeatures.size());
	}// end method main
	
	/**
	 * 
	 * @throws IOException
	 */
	private static void prepare() throws IOException{
//		ParagraphIDExtractor.extract(new File(TRAIN_DIR));
//		ParagraphIDExtractor.extract(new File(TEST_DIR));
//		EDUCounter.countEDU(new File(TRAIN_DIR));
//		EDUCounter.countEDU(new File(TEST_DIR));
//		Preprocess.process(new File(TRAIN_DIR));
//		Preprocess.process(new File(TEST_DIR));		
		return;
	}// end method prepare
	
	/**
	 * 
	 * @param file
	 * @return an integer array containing all parent node
	 * @throws FileNotFoundException
	 */
	private static Integer[] getParents(File file) throws FileNotFoundException{
		Scanner cin = new Scanner(file);
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.clear();
//		arr.add(-1);
		while(cin.hasNextLine()){
			arr.add(Integer.parseInt(cin.nextLine().trim().split("\\s++")[1])-1);
		}
		Integer[] res = new Integer[arr.size()];
		for(int i=0; i<arr.size(); i++)
			res[i] = arr.get(i);
		return res;
	}// end method getParents

}// end class Main
