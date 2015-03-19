package mstparser;

import java.util.ArrayList;


public class MyEDU {
	
//	line number, 0 indexed
	public int lineID;
//	store original raw string
	public String rawString;
//	include non-character token
	public String[] rawTokens;
//	include tags for non-character token
	public String[] rawTags;
//	store tokens of raw string, 
//	without any punctuation such as comma or period
	public String[] tokens;
//	POS for each token
	public String[] POStags;
	
	public MyEDU(int lineID, String rawString) {
		this.lineID = lineID;
		this.rawString = rawString;
		this.rawTokens = WebParser.tokenizer.tokenize(rawString);
		this.rawTags = WebParser.tagger.tag(rawTokens);
		ArrayList<Integer> valid = new ArrayList<Integer>();
		for (int i=0; i<rawTokens.length; i++) {
			if (rawTokens[i].matches(".*[0-9a-zA-Z].*")) {
				valid.add(i);
			}
		}
		this.POStags = new String[valid.size()];
		this.tokens = new String[valid.size()];
		for (int i=0; i<valid.size(); i++) {
			this.POStags[i] = this.rawTags[valid.get(i)];
			this.tokens[i] = this.rawTokens[valid.get(i)];
		}
	} // end constructor
	
	public static MyEDU getROOT() {
		MyEDU root = new MyEDU(-1, "ROOT");
//		root.lineID = -1;
		root.rawString = "ROOT";
		root.rawTokens = new String[1];
		root.rawTokens[0] = "ROOT";
		root.rawTags = root.rawTokens;
		root.POStags = root.rawTokens;
		root.tokens = root.rawTokens;
		return root;
	}

} // end class MyEDU
