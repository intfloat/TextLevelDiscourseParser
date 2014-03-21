package mstparser;

/**
 * 
 * @author Liang Wang
 * @since 2013/10/13
 * @version 2013/11/12
 */
public class MyEvaluatorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		added by Liang Wang
		int totalInterval = 0;
		int correctInterval = 0;
		int correctIntervalNuclearity = 0;
		int correctIntervalLabel = 0;
		MyEvaluator evaluator = new MyEvaluator();
//		end
		
		
//		change these values and you can get different test cases
	    int[] goldHeads = {-1, 2, 0, 2, 5, 2, 5, 5};
	    String[] goldLabels = {"root", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN"};
//	    span: 100%, nuclearity: 100%, relation: 100%
	    int[] predHeads = {-1, 2, 0, 2, 5, 2, 5, 5};
//	    span: 80%, nuclearity:66.7%, relation:80%
	    int[] predHeads1 = {-1, 0, 1, 2, 1, 4, 5, 5};
//	    span: 11/12, nuclearity: 10/12, relation: 11/12
	    int[] predHeads2 = {-1, 2, 0, 2, 2, 2, 5, 5};
//	    there should be error output
	    int[] predHeads3 = {-1, 2, 0, 2, 1, 2, 5, 5};
	    String[] predLabels = {"root", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN", "SPAN"};
		
//	    added by Liang Wang
//	    internal node is leaf node minus 1, and also remove root, so comes this formula.
	    totalInterval += 2*(goldHeads.length)-1;
//	    int[] result = evaluator.evaluate(goldHeads, predHeads, goldLabels, predLabels);
//	    int[] result = evaluator.evaluate(goldHeads, predHeads1, goldLabels, predLabels);
	    int[] result = evaluator.evaluate(goldHeads, predHeads2, goldLabels, predLabels);
//	    int[] result = evaluator.evaluate(goldHeads, predHeads3, goldLabels, predLabels);
	    correctInterval += result[0];
	    correctIntervalNuclearity += result[1];
	    correctIntervalLabel += result[2];
//	    end	    
	    
//		added by Liang Wang
		System.out.println("This is output from my evaluator.");
		System.out.println("Span precision/recall: "+((double)correctInterval/totalInterval));
		System.out.println("Nuclearity precision/recall: "+((double)correctIntervalNuclearity/totalInterval));
		System.out.println("Relation precision/recall: "+ ((double)correctIntervalLabel/totalInterval));
		System.out.println("End for my evalutor.");
		System.out.println();
//		end
	}// end method main

}// end class MyEvaluatorTest
