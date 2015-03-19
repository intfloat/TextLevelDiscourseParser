package wl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;


import opennlp.tools.util.InvalidFormatException;

/**
 * 
 * @author Liang Wang
 * @since 2013/9/5
 * @version 2013/11/26
 * 
 *  
 *  this is a class for feature extraction, you can modify this code
 *  and add any features you want conveniently.
 *
 */
public class FeatureExtractor {
	
	
//	contains all EDUs
	public static EDU[] edus;
//	final binary features result
	public static String result = "";
//	contain separate feature for each circumstance 
	public static Vector<Feature> featureVector = new Vector<Feature>();
//	record current position of feature vector, this variable will be modified across several classes,
//	be careful about that when you change its value
	public static int featurePosition = 0;
	public static AllFeatures allFeatures = new AllFeatures();
	public static Integer[] parents;
	public static EDU root = null;	
//	public static boolean GOLD;
	

	
	/**
	 * 
	 * @param input it is input file's absolute path	 * 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public static void getFeatureString(File input) throws InvalidFormatException, IOException{
		
		featureVector.clear();
		root = EDU.getRootEDU();
//		create edu array
		FeatureExtractor.createEDUs(input);
		FileWriter writer = null;
		if(Main.outputFeatures)
			writer = new FileWriter(input.toString()+Main.PRIM_SUFFIX);
		
		allFeatures.dict.startAddFeatures();
		for(int i=0; i<parents.length; i++){
			addSingleFeature(i);
		}
		for(int i=0; i<parents.length; i++){
			addFeatures(parents[i], i);
		}
		allFeatures.dict.stopAddFeatures();
		
		if(Main.outputFeatures==false) return;
		
		if(Main.outputFeatures)
			writer.write(edus.length+"\n");
		
//		add single features
		for(int i=0; i<edus.length; i++){
			addSingleFeature(i);
			result = "";
			for(Feature feature : featureVector)
				result += feature.toString();
			if(Main.outputFeatures)
				writer.write(i+"\n"+result+"\n");
		}// end for loop
		
//		add root leaf features
		for(int i=0; i<edus.length; i++){
			addFeatures(-1, i);
			result = "";
//			output feature string for debugging
			for(Feature feature : featureVector){
				result += feature.toString();
			}// end for loop				
			if(Main.outputFeatures)
				writer.write("-1 "+i+"\n"+result+"\n");
		}// end for loop
		
//		iterate on all combinations of edu, notice it is in directed graph,
//		so (i, j) and (j, i) are different pairs.		
		for(int i=0; i<edus.length; i++){
			for(int j=0; j<edus.length; j++){
				if(i==j) continue;			
				
				featurePosition = 0;				
//				add features for this edu pair
				addFeatures(i, j);								
				
				result = "";
//				output feature string for debugging
				for(Feature feature : featureVector){
					result += feature.toString();
				}// end for loop				
				if(Main.outputFeatures)
					writer.write(i+" "+j+"\n"+result+"\n");				
			}// end inner for loop
		}// end external for loop
		
		if(Main.outputFeatures)
			writer.flush();
		
//		System.err.println("Number of features: "+featurePosition+"\n");
		
//		results has been written into file, so no return statement needed.
		return;
		
	}// end method getFeatureString
	
	
	public static void addSingleFeature(int index){
		featureVector.clear();
		AllFeatures.featSet.clear();		

		allFeatures.featureString = "";
		EDU first = edus[index];
		allFeatures.addSingleWordPairFeature(first);
		allFeatures.addSinglePOSFeature(first);
		allFeatures.addSingleSynFeature(first);
		allFeatures.addSingleLengthFeature(first);
		allFeatures.addSingleParaFeature(first);
		allFeatures.addSingleSentenceFeature(first);
		
		featureVector.add(allFeatures);		
		return;
	}// end method addSingleFeature
	
	/**
	 * 
	 * @param i is index of first EDU
	 * @param j is index of second EDU
	 * @throws FileNotFoundException
	 * 
	 *  this static method is used to add various features into feature vector
	 */
	public static void addFeatures(int i, int j) throws FileNotFoundException{
		
		featureVector.clear();
		AllFeatures.featSet.clear();		

		allFeatures.featureString = "";		
		EDU first = null;
		EDU second = edus[j];
		if(i < 0)
			first = root;
		else first = edus[i];
		
//		1
		allFeatures.addWordPairFeature(first, second);
//		2
		allFeatures.addPOSFeature(first, second);
//		3
		allFeatures.addParagraphFeature(first, second);		
		allFeatures.addSentenceFeature(first, second);
//		4 sum to 128912 features
		allFeatures.addLengthFeature(first, second);
//		5 sum to 129862 features
		allFeatures.addSyntacticFeature(first, second);
//		6 sum to 130717 features
		allFeatures.addSimilarityFeature(i, j);		
		
		featureVector.add(allFeatures);
		
		return;
	}// end method addFeatures
	
	/**
	 * 
	 * @param input it is input file path
	 * @return if array was created correctly
	 * @throws InvalidFormatException
	 * @throws IOException
	 * 
	 * this static method is used to create edus when given file name with suffix ".edus"
	 */
	public static boolean createEDUs(File input) throws InvalidFormatException, IOException {
		
//		notice all information has already been stored in ".pre" files, so we need to
//		do some transformation on file name
		String name = input.toString();
		name = name.replace(Main.EDUS_SUFFIX, "");
		File synFile = new File(name.replace(Main.OUT_SUFFIX, "")+Main.SYN_SUFFIX);
		String simName = input.toString()+Main.SIM_SUFFIX;
		File simFile = new File(simName);
		File file = new File(name+Main.PRE_SUFFIX);
		
//		given file actually does not exist or it is a directory
		if(file.exists()==false || file.isDirectory()){
			System.err.println("File "+file.toString()+" does not exist!");
			return false;
		}
		
//		define and initialize some variables
		Vector<EDU> vector = new Vector<EDU>();
		vector.clear();
		int sentenceID = 0;
		int lineID = 0;
		int offset = 0;
		int paraID = 0;
		String rawString;
		String[] rawTokens, rawTags, tokens, tags;
		Scanner cin = new Scanner(file);	
		
		ArrayList<ArrayList<String>> words = new ArrayList<ArrayList<String>>();		
		while(cin.hasNextLine()){
			ArrayList<String> currentList = new ArrayList<String>();
			cin.nextLine();
			cin.nextLine();
			rawTokens = cin.nextLine().trim().split("\\s++");
			for(String str:rawTokens)
				currentList.add(str);
			words.add(currentList);
			cin.nextLine();
			cin.nextLine();
			cin.nextLine();
		}// end while loop
		Main.pdtbParser.parse(synFile);
		ArrayList<ArrayList<String>> labels = null;
		labels = Main.pdtbParser.loader.getSyntacticLabels(words);
		
//		sorry about reading file again, when you modify code written 2 months ago,
//		it is really difficult to have a good style.
		cin = new Scanner(file);
		int current = 0;
//		read in corresponding information from ".pre" files line by line
		while(cin.hasNextLine()){
			
//			split the first line and get some parameters such as sentenceID, lineID, etc
			String[] numbers = cin.nextLine().trim().split("\\s++");
			if(numbers.length != 4){
				System.err.println("Format error in forst line in "+file.toString());
			}
			else{
				sentenceID = Integer.parseInt(numbers[0]);
				lineID = Integer.parseInt(numbers[1]);
				offset = Integer.parseInt(numbers[2]);
				paraID = Integer.parseInt(numbers[3]);
			}
//			get remaining lines
			rawString = cin.nextLine().trim();
			rawTokens = cin.nextLine().trim().split("\\s++");
			rawTags = cin.nextLine().trim().split("\\s++");
			tokens = cin.nextLine().trim().split("\\s++");
			tags = cin.nextLine().trim().split("\\s++");
//			call constructor for class EDU and add this new instance into vector
			vector.add(new EDU(rawString, rawTokens, rawTags, tokens, tags, 
					sentenceID, lineID, offset, paraID, labels.get(current)));
			++current;
		}// end while loop
		
		Scanner simReader = new Scanner(simFile);
		int eduNumber = Integer.parseInt(simReader.nextLine().trim());
		AllFeatures.similarity = new double[5][eduNumber][eduNumber];		
		for(int i=0; i<5; i++)
			for(int j=0; j<eduNumber; j++)
				for(int k=0; k<eduNumber; k++)
					AllFeatures.similarity[i][j][k] = 0;
		while(simReader.hasNextLine()){
			String[] numberPair = simReader.nextLine().trim().split("\\s++");
			int small = Integer.parseInt(numberPair[0]);
			int large = Integer.parseInt(numberPair[1]);
			String[] simi = simReader.nextLine().trim().split("\\s++");
			if(simi.length != 5){
				System.err.println("Error in similarity representation.");				
			}
			for(int i=0; i<5; i++){
				AllFeatures.similarity[i][small][large] = Double.parseDouble(simi[i]);
				AllFeatures.similarity[i][large][small] = Double.parseDouble(simi[i]);
			}
		}
		
//		transform edu vector into a linear array.
		FeatureExtractor.edus = new EDU[vector.size()];
		vector.toArray(edus);
		
//		compute revOffset and revSentenceID 
		int len = FeatureExtractor.edus.length;
		if(len == 0) return true;
		
		FeatureExtractor.edus[len-1].revOffset = 0;
		FeatureExtractor.edus[len-1].revSentenceID = 0;
		for(int i=len-2; i>=0; i--){
			int para1 = FeatureExtractor.edus[i].paragraphID;
			int para2 = FeatureExtractor.edus[i+1].paragraphID;
			int sent1 = FeatureExtractor.edus[i].sentenceID;
			int sent2 = FeatureExtractor.edus[i+1].sentenceID;
			if(para1 == para2)
				FeatureExtractor.edus[i].revSentenceID = FeatureExtractor.edus[i+1].revSentenceID+1;			
			else FeatureExtractor.edus[i].revSentenceID = 0;
			if(sent1 == sent2)
				FeatureExtractor.edus[i].revOffset = FeatureExtractor.edus[i+1].revOffset+1;
			else FeatureExtractor.edus[i].revOffset = 0;
		}
		return true;		
	}// end method createEDUs
	
	
	
}// end class FeatureExtractor
