package mstparser;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import mstparser.io.*;

public class DependencyEvaluator {
	
	public static String getEDUContent(File file, int pos) throws FileNotFoundException{
		Scanner reader = new Scanner(file);
		for(int i=1; i<pos; i++)
			reader.nextLine();
		return reader.nextLine();
	}
	
    public static void evaluate (String act_file, 
				 String pred_file, 
				 String format) throws IOException {
	
	DependencyReader goldReader = DependencyReader.createDependencyReader(format);
	boolean labeled = goldReader.startReading(act_file);

	DependencyReader predictedReader = DependencyReader.createDependencyReader(format);
	boolean predLabeled = predictedReader.startReading(pred_file);

	if (labeled != predLabeled)
	    System.out.println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");


	int total = 0; int corr = 0; int corrL = 0;
	int numsent = 0; int corrsent = 0; int corrsentL = 0;
	int root_act = 0; int root_guess = 0; int root_corr = 0;
	
	int totalInterval = 0;
	int correctInterval = 0;
	int correctIntervalNuclearity = 0;
	int correctIntervalLabel = 0;
	MyEvaluator evaluator = new MyEvaluator();
    
	
	HashMap<String, Integer> totalMap = new HashMap<String, Integer>();
	HashMap<String, Integer> predMap = new HashMap<String, Integer>();
	HashMap<String, Integer> correctMap = new HashMap<String, Integer>();
	HashMap<String, Integer> factTotalMap = new HashMap<String, Integer>();
	HashMap<String, Integer> predTotalMap = new HashMap<String, Integer>();
	HashMap<String, HashMap<String, Integer>> misclass = new HashMap<String, HashMap<String, Integer>>();
	totalMap.clear(); predMap.clear(); correctMap.clear(); predTotalMap.clear();
	factTotalMap.clear(); misclass.clear();

	DependencyInstance goldInstance = goldReader.getNext();
	DependencyInstance predInstance = predictedReader.getNext();
	FileWriter myWriter = new FileWriter(Math.floor(Math.random()*1000)+"detailedOut.txt");

	while(goldInstance != null) {

	    int instanceLength = goldInstance.length();
	    String[] forms = goldInstance.forms;
	    int fileNumber = Integer.parseInt(forms[forms.length-1]);
	    String fileStr = DependencyParser.featureFiles[fileNumber].toString();
	    fileStr = fileStr.replaceAll(".prim", "");
    	fileStr = fileStr.replaceAll(".feat", "");
	    myWriter.write(fileStr+"\n");

	    if (instanceLength != predInstance.length())
		System.out.println("Lengths do not match on sentence "+numsent);

	    int[] goldHeads = goldInstance.heads;
	    String[] goldLabels = goldInstance.deprels;
	    int[] predHeads = predInstance.heads;
	    String[] predLabels = predInstance.deprels;
	    
//	    added by Liang Wang
//	    internal node is leaf node minus 1, and also remove root, so comes this formula.
	    totalInterval += 2*(goldHeads.length)-1;
	    int[] result = evaluator.evaluate(goldHeads, predHeads, goldLabels, predLabels, fileStr);
	    correctInterval += result[0];
	    correctIntervalNuclearity += result[1];
	    correctIntervalLabel += result[2];
//	    end	    
	    
	    boolean whole = true;
	    boolean wholeL = true;

	    // NOTE: the first item is the root info added during nextInstance(), so we skip it.

	    for (int i = 1; i < instanceLength; i++) {
	    	
	    	myWriter.write(i+" "+goldHeads[i]+" "+predHeads[i]+" "+goldLabels[i]+" "+predLabels[i]);
	    	
	    	myWriter.write(getEDUContent(new File(fileStr), i)+"\n");
	    	
//		    added by me
		    if(predLabels[i].equals("<no-type>"))
		    	predLabels[i] = "Elaboration";
		    if(factTotalMap.containsKey(goldLabels[i]))
		    	factTotalMap.put(goldLabels[i], factTotalMap.get(goldLabels[i])+1);
		    else factTotalMap.put(goldLabels[i], 1);
		    
		    if(predTotalMap.containsKey(predLabels[i]))
		    	predTotalMap.put(predLabels[i], predTotalMap.get(predLabels[i])+1);
		    else predTotalMap.put(predLabels[i], 1);
	    	
		if (predHeads[i] == goldHeads[i]) {
			
			if(predMap.containsKey(predLabels[i]))
		    	predMap.put(predLabels[i], predMap.get(predLabels[i])+1);
		    else predMap.put(predLabels[i], 1);
			if(totalMap.containsKey(goldLabels[i]))
		    	totalMap.put(goldLabels[i], totalMap.get(goldLabels[i])+1);
		    else totalMap.put(goldLabels[i], 1);
			
		    corr++;
		    if (labeled) {
//		    	System.out.println(goldLabels[i]+" "+predLabels[i]);		    
			if (goldLabels[i].equals(predLabels[i])){
				if(correctMap.containsKey(predLabels[i]))
			    	correctMap.put(predLabels[i], correctMap.get(predLabels[i])+1);
			    else correctMap.put(predLabels[i], 1);
			    corrL++;
			}
//			label is wrong classified
			else{
//				meet this gild label first time
				if(misclass.containsKey(goldLabels[i]) == false){
					misclass.put(goldLabels[i], new HashMap<String, Integer>());
					misclass.get(goldLabels[i]).put(predLabels[i], 1);
				}
				else{
//					already see this wrong prediction label
					if(misclass.get(goldLabels[i]).containsKey(predLabels[i])){
						int tmp = misclass.get(goldLabels[i]).get(predLabels[i]);
						misclass.get(goldLabels[i]).put(predLabels[i], tmp+1);
					}
//					meet this wrong label first time
					else{
						misclass.get(goldLabels[i]).put(predLabels[i], 1);
					}
				}
			    wholeL = false;
				}
		    }
		}
//		prediction is wrong
		else {			
		    whole = false; wholeL = false; 
		}
	    }
	    total += instanceLength - 1; // Subtract one to not score fake root token

	    if(whole) corrsent++;
	    if(wholeL) corrsentL++;
	    numsent++;
						
	    goldInstance = goldReader.getNext();
	    predInstance = predictedReader.getNext();
	}
	
	myWriter.flush();
//	output corresponding result
	FileWriter writer = new FileWriter(new File("statistics.txt"));
	writer.write("RelationName AppearanceTimes(correctHead) CorrectPredictionTimes" +
			"  PredictionTimes(correctHead) CorretRatio	TotalAppearanceTimes  TotalPredictionTimes" +
			"   MostFrequentMistake\n");
	Iterator<String> it = factTotalMap.keySet().iterator();
	while(it.hasNext()){
		
//		added by Wang Liang
		String str = it.next();
		int col1 = 0; int col2 = 0; int col3 = 0; int col5 = 0; int col6 = 0;
		String col7 = "";
		
		if(misclass.containsKey(str) == false){
			col7 = "NULL";
		}// end if clause
		else{
			int mx = 0;
			Iterator<String> myit = misclass.get(str).keySet().iterator();
//			get the largest label
			while(myit.hasNext()){
				String current = myit.next();
				if(misclass.get(str).get(current) > mx){
					mx = misclass.get(str).get(current);
					col7 = current;
				}
			}
			if(mx > 0) col7 += ":"+mx;
			else col7 = "NULL";
		}// end else
		
//		added by Wang Liang
		if(totalMap.containsKey(str)) col1 = totalMap.get(str);
		if(correctMap.containsKey(str)) col2 = correctMap.get(str);
		if(predMap.containsKey(str)) col3 = predMap.get(str);
		if(factTotalMap.containsKey(str)) col5 = factTotalMap.get(str);
		if(predTotalMap.containsKey(str)) col6 = predTotalMap.get(str);
		if(col1 == 0){
			writer.write(str+" "+col1+" "+col2+" "+col3+" NaN "+col5+" "+col6+" "+col7+"\n");
		}
		else{
			writer.write(str+" "+col1+" "+col2+" "+col3+" "+((double)col2/col1)+" "+col5+" "+col6+" "+col7+"\n");
		}		
	}
	
	writer.flush();

	System.out.println("Tokens: " + total);
	System.out.println("Correct: " + corr);
	System.out.println("Unlabeled Accuracy: " + ((double)corr/total));
	System.out.println("Unlabeled Complete Correct: " + ((double)corrsent/numsent));
	if(labeled) {
	    System.out.println("Labeled Accuracy: " + ((double)corrL/total));
	    System.out.println("Labeled Complete Correct: " + ((double)corrsentL/numsent));
	}
	
//	added by Liang Wang
	System.out.println();
	System.out.println("This is output from my evaluator.");
	System.out.println("Span precision/recall: "+((double)correctInterval/totalInterval));
	System.out.println("Nuclearity precision/recall: "+((double)correctIntervalNuclearity/totalInterval));
	System.out.println("Relation precision/recall: "+ ((double)correctIntervalLabel/totalInterval));
	System.out.println("End for my evalutor.");
	System.out.println();
		
    }
    
    public static void myEvaluate (String act_file, String format) throws IOException {

DependencyReader goldReader = DependencyReader.createDependencyReader(format);
boolean labeled = goldReader.startReading(act_file);

if(labeled == false) return;

//
int total = 0; int corr = 0; int corrL = 0;
int numsent = 0; int corrsent = 0; int corrsentL = 0;
int root_act = 0; int root_guess = 0; int root_corr = 0;

DependencyInstance goldInstance = goldReader.getNext();
FileWriter myWriter = new FileWriter(Math.floor(Math.random()*1000)+"detailedOutVersion2.txt");

HashMap<String, Integer> relationMap = new HashMap<String, Integer>();
HashMap<String, Integer> correctPredMap = new HashMap<String, Integer>();
HashMap<String, Integer> predTimeMap = new HashMap<String, Integer>();
HashMap<String, Integer> totalMap = new HashMap<String, Integer>();
relationMap.clear(); correctPredMap.clear(); predTimeMap.clear(); totalMap.clear();

int current = 0;
while(goldInstance != null) {


   int instanceLength = goldInstance.length();
   String[] forms = goldInstance.forms;
   int fileNumber = Integer.parseInt(forms[forms.length-1]);
   String fileStr = DependencyParser.featureFiles[fileNumber].toString();
   fileStr = fileStr.replaceAll(".prim", "");
   fileStr = fileStr.replaceAll(".feat", "");
   myWriter.write(fileStr+"\n");

//   if (instanceLength != predInstance.length())
//	System.out.println("Lengths do not match on sentence "+numsent);

   int[] goldHeads = goldInstance.heads;
   String[] goldLabels = goldInstance.deprels;
   int[] predHeads = goldHeads;
   String[] predLabels = new String[goldLabels.length];
   for(int i=1; i<instanceLength; i++){
	   try{
	   predLabels[i] = DependencyDecoder.maxTypes[current][goldHeads[i]][i];
	   if(predLabels[i].equals("<no-type>"))
		   predLabels[i] = "Elaboration";	   
	   }
	   catch(Exception e){
		   System.out.println(i+": "+i+" goldhead: "+goldHeads[i]);
		   System.out.println(current+" "+fileStr);
		   e.printStackTrace();
		   System.exit(1);
		}
   }
   current++;
   
   boolean whole = true;
   boolean wholeL = true;

   // NOTE: the first item is the root info added during nextInstance(), so we skip it.

   for (int i = 1; i < instanceLength; i++) {
   	
   	myWriter.write(i+" "+goldHeads[i]+" "+predHeads[i]+" "+goldLabels[i]+" "+predLabels[i]);   	
   	myWriter.write(getEDUContent(new File(fileStr), i)+"\n");
   	
	if(relationMap.containsKey(goldLabels[i]))
	   relationMap.put(goldLabels[i], relationMap.get(goldLabels[i])+1);
	else relationMap.put(goldLabels[i], 1);		
	if(predTimeMap.containsKey(predLabels[i]))
		predTimeMap.put(predLabels[i], predTimeMap.get(predLabels[i])+1);
	else predTimeMap.put(predLabels[i], 1);
	
	if (predHeads[i] == goldHeads[i]) {		
		corr++;
	    if (labeled) {
//	    	System.out.println(goldLabels[i]+" "+predLabels[i]);		    
		if (goldLabels[i].equals(predLabels[i])){
			if(correctPredMap.containsKey(goldLabels[i]))
				correctPredMap.put(goldLabels[i],correctPredMap.get(goldLabels[i])+1);
			else correctPredMap.put(goldLabels[i], 1);
			
		    corrL++;
		}
//		label is wrong classified
		else{
		    wholeL = false;
			}
	    }
	}
//	prediction is wrong
	else {			
	    whole = false; wholeL = false; 
	}
   }
   total += instanceLength - 1; // Subtract one to not score fake root token

   if(whole) corrsent++;
   if(wholeL) corrsentL++;
   numsent++;
					
   goldInstance = goldReader.getNext();   
}

myWriter.flush();


//relationMap.clear(); correctPredMap.clear(); predTimeMap.clear(); totalMap.clear();
FileWriter writer = new FileWriter("statistics-2.txt");
writer.write("RelationName CorrectPredictionTimes PredictionTimes TotalAppearanceTimes\n");
Iterator<String> it = relationMap.keySet().iterator();
while(it.hasNext()){
	String relation = it.next();
	int col2 = 0, col3 = 0, col4 = 0;
	if(correctPredMap.containsKey(relation)) col2 = correctPredMap.get(relation);
	if(predTimeMap.containsKey(relation)) col3 = predTimeMap.get(relation);
	if(relationMap.containsKey(relation)) col4 = relationMap.get(relation);
	writer.write(relation+" "+col2+" "+col3+" "+col4+"\n");
}
writer.flush();

System.out.println();
System.out.println("PERFORMANCE VERSION2");
System.out.println("Tokens: " + total);
System.out.println("Correct: " + corr);
System.out.println("Unlabeled Accuracy: " + ((double)corr/total));
System.out.println("Unlabeled Complete Correct: " + ((double)corrsent/numsent));
if(labeled) {
   System.out.println("Labeled Accuracy: " + ((double)corrL/total));
   System.out.println("Labeled Complete Correct: " + ((double)corrsentL/numsent));
}
	
}


    public static void main (String[] args) throws IOException {
	String format = "CONLL";
	if (args.length > 2)
	    format = args[2];

	evaluate(args[0], args[1], format);
    }

}
