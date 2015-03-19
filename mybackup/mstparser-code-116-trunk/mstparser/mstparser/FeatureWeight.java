package mstparser;

public class FeatureWeight implements Comparable<FeatureWeight>{
	
	public double weight;
	public String description;
	
//	constructor
	public FeatureWeight(String description, double weight){
		this.weight = weight;
		this.description = description;
	}// end constructor

	@Override
	public int compareTo(FeatureWeight feat) {
		// TODO Auto-generated method stub
		double res = Math.abs(feat.weight)-Math.abs(this.weight);
		if(res > 0) return 1;
		else return -1;
	}// end method compareTo
	
}// end class FeatureWeight
