package wl;

import java.util.HashMap;


public class FeatureDictionary {
	
	public HashMap<String, Integer> map;
	public HashMap<Integer, String> featMap;
	private boolean stop;
	
//	a simple constructor
	public FeatureDictionary(){
		this.map = new HashMap<String, Integer>();
		this.featMap = new HashMap<Integer, String>();
		this.map.clear();
		this.featMap.clear();
		this.stop = false;
	}// end constructor	
	
	public void stopAddFeatures(){
		this.stop = true;
	}
	
	public void startAddFeatures(){
		this.stop = false;
	}
	
	
	/**
	 * 
	 * @param feat
	 * @return index of above feature, or push it back if not exist.
	 */
	public int getIndex(String feat){
		if(this.map.containsKey(feat))
			return this.map.get(feat);
		
//		stop adding any new features
		if(this.stop == true)
			return -1;
		int size = this.map.size();
		this.map.put(feat, size);
		this.featMap.put(size, feat);
		return size;
	}// end method getIndex
	
	public int getFeatureNumber(){
		return this.map.size();
	}// end method getFeatureNumber
	
}// end class FeatureDictionary
