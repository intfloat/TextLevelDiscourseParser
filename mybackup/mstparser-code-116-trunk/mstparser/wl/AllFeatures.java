package wl;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * 
 * @author Wang Liang
 * @since 2013/10/6
 * @version 2013/11/26
 *
 */
public class AllFeatures extends Feature{

	public String featureString;
//	public StringBuffer stringBuffer;
	public FeatureDictionary dict;
	public static HashSet<String> featSet = new HashSet<String>();
	public static double[][][] similarity;	
//	public static int small, big;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return this.stringBuffer.toString();
		return featureString.trim();
	}// end method toString
	
	public AllFeatures() { 
		this.featureString="";
//		this.stringBuffer = new StringBuffer();
		this.dict = new FeatureDictionary();
	}
	
	public void addSingleSynFeature(EDU first){
		ArrayList<String> firstList = first.syntacticArray;		
		for(int i=0; i<firstList.size(); i++)
			this.addFeature("SSYNF_"+firstList.get(i));		
		return;
	}// end method addSingleSynFeature
	
//	add syntactic feature from PDTB
	public void addSyntacticFeature(EDU first, EDU second){
		ArrayList<String> firstList = first.syntacticArray;
		ArrayList<String> secondList = second.syntacticArray;
		for(int i=0; i<firstList.size(); i++)
			this.addFeature("SYNF_"+firstList.get(i));
		for(int i=0; i<secondList.size(); i++)
			this.addFeature("SYNS_"+secondList.get(i));
//		this.addFeature("SYNF_"+firstList.get(firstList.size()-1));
//		this.addFeature("SYNS_"+secondList.get(secondList.size()-1));
		return;
	}// end method addSyntacticFeature	
	
	
//	add wordnet similarity features
	public void addSimilarityFeature(int first, int second){
		if(first<0 || second<0)
			return;
		for(int i=0; i<5; i++){
			this.addFeature("SSIM1_"+i+"_"+Math.floor((similarity[i][first][second]/0.3)));
			this.addFeature("SSIM2_"+i+"_"+Math.floor((similarity[i][first][second]/0.1)));
			this.addFeature("SSIM3_"+i+"_"+Math.floor((similarity[i][first][second]/0.5)));
		}
		return;
	}// end method addSimilarityFeature
	
	public void addSinglePOSFeature(EDU first){		
		this.addFeature("POFS_"+first.POStags[0]);
		this.addFeature("POSFL_"+first.POStags[first.POStags.length-1]);
		for(int i=0; i<first.POStags.length; i++)
			this.addFeature("PFOS_"+first.POStags[i]);		
		return;
	}// end method addSinglePOSFeature
	
//	add POS features
	public void addPOSFeature(EDU first, EDU second){
		this.addFeature("POA_"+first.POStags[0]+" "+second.POStags[0]);
		this.addFeature("POF_"+first.POStags[0]);
		this.addFeature("POS_"+second.POStags[0]);
		for(int i=0; i<first.POStags.length; i++)
			this.addFeature("PFO_"+first.POStags[i]);
		for(int i=0; i<second.POStags.length; i++)
			this.addFeature("PSO_"+second.POStags[i]);
	}// end method addPOSFeature
	
	public void addSingleSentenceFeature(EDU first){
		this.addFeature("SSOFF_"+first.offset);		
		this.addFeature("SSOVF_"+first.revOffset);		
		this.addFeature("SSIDF_"+first.sentenceID);		
		this.addFeature("SSIDV_"+first.revSentenceID);
		
		return;
	}// end method addSingleSentenceFeature
	
//	add sentence features
	public void addSentenceFeature(EDU first, EDU second){
		this.addFeature("SOFF_"+first.offset);
		this.addFeature("SOFS_"+second.offset);
		this.addFeature("SOVF_"+first.revOffset);
		this.addFeature("SOVS_"+second.revOffset);
		this.addFeature("SOD_"+(first.offset-second.offset));
		this.addFeature("SODV_"+(first.revOffset-second.revOffset));
		this.addFeature("SODM_"+((first.offset-second.offset)/3));
		this.addFeature("SODVM_"+((first.revOffset-second.revOffset)/3));
		this.addFeature("SOF_"+first.offset+" "+second.offset);
		this.addFeature("SRV_"+first.revOffset+" "+second.revOffset);
		this.addFeature("SDF_"+(first.offset/3)+" "+(second.offset/3));
		this.addFeature("SDRV_"+(first.revOffset/3)+" "+(second.revOffset/3));
		this.addFeature("SDI_"+(first.lineID-second.lineID));
		if(first.sentenceID == second.sentenceID)
			this.addFeature("SSM_ same sentence");
		else
			this.addFeature("SDS_ different sentence");
		this.addFeature("SIDF_"+first.sentenceID);
		this.addFeature("SIDS_"+second.sentenceID);
		this.addFeature("SIDV_"+first.revSentenceID);
		this.addFeature("SIDVS_"+second.revSentenceID);
		this.addFeature("SDI_"+(first.sentenceID-second.sentenceID));
		this.addFeature("SDDI_"+((first.sentenceID-second.sentenceID)/3));
		this.addFeature("SDIV_"+(first.revSentenceID-second.revSentenceID));
		this.addFeature("SDDIV_"+((first.revSentenceID-second.revSentenceID)/3));
	}// end method addSentenceFeature
	
	public void addSingleLengthFeature(EDU first){
		int firstLength = first.tokens.length;		
		this.addFeature("SLSF5_"+(firstLength/5));
		this.addFeature("SLSF_"+(firstLength));
		return;
	}// end method addSingleLengthFeature
	
//	add length features
	public void addLengthFeature(EDU first, EDU second){
		int firstLength = first.tokens.length;
		int secondLength = second.tokens.length;
		this.addFeature("LPA_"+(firstLength/5)+" "+(secondLength/5));
		this.addFeature("LSF_"+(firstLength/5));
		this.addFeature("LSS_"+(secondLength/5));
		this.addFeature("LDI_"+((firstLength-secondLength)/5));
	}// end method addLengthFeature
	
	public void addSingleWordPairFeature(EDU first){
		String[] iStr = first.tokens;		 
		
//		add single word feature
		this.addFeature("SWSI_"+iStr[0]);		
		this.addFeature("SWSIL_"+iStr[iStr.length-1]);		
		
		if(iStr.length >= 2){
			this.addFeature("SW2GF_"+iStr[0]+" "+iStr[1]);
			this.addFeature("SW2GB_"+iStr[iStr.length-2]+" "+iStr[iStr.length-1]);
		}				
		return;
	}// end method addSingleWordPairFeature
	
//	add word pair feature
	public void addWordPairFeature(EDU first, EDU second){
		String[] iStr = first.tokens;
		String[] jStr = second.tokens; 
		
//		add single word feature
		this.addFeature("WSI_"+iStr[0]);
		this.addFeature("WSJ_"+jStr[0]);
		this.addFeature("WSIL_"+iStr[iStr.length-1]);
		this.addFeature("WSJL_"+jStr[jStr.length-1]);
		
		if(iStr.length >= 2){
			this.addFeature("W2GF_"+iStr[0]+" "+iStr[1]);
			this.addFeature("W2GB_"+iStr[iStr.length-2]+" "+iStr[iStr.length-1]);
		}
		
		if(jStr.length >= 2){
			this.addFeature("W2GJF_"+jStr[0]+" "+jStr[1]);
			this.addFeature("W2GJB_"+jStr[jStr.length-2]+" "+jStr[jStr.length-1]);
		}
		
		this.addFeature("WPF_"+iStr[0]+" "+jStr[0]);
		this.addFeature("WPB_"+iStr[iStr.length-1]+" "+jStr[jStr.length-1]);
		if(iStr.length>=2 && jStr.length>=2){
			String front = iStr[0]+" "+iStr[1]+""+jStr[0]+" "+jStr[1];
			String back = iStr[iStr.length-2]+" "+iStr[iStr.length-1]
					+" "+jStr[jStr.length-2]+" "+jStr[jStr.length-1];
			this.addFeature("WP2F_"+front);
			this.addFeature("WP2B_"+back);
		}// end if
		
		return;
	}// end method addWordPairFeature
	
	public void addSingleParaFeature(EDU first){
		this.addFeature("SPA_"+first.paragraphID);
		this.addFeature("SPA5_"+(first.paragraphID/5));
		return;
	}// end method addSingleParaFeature
	
//	add paragraph features
	public void addParagraphFeature(EDU first, EDU second){
		if(first.paragraphID == second.paragraphID)
			this.addFeature("PAS_ same para");
		else if(first.paragraphID < second.paragraphID)
			this.addFeature("PAO_ first");
		else
			this.addFeature("PAO_ second");
		
		int diff = first.paragraphID - second.paragraphID;
		this.addFeature("PAD_"+diff);
		this.addFeature("PADM_"+(diff)/3);
	}// end method addParagraphFeature
	
//	add feature which is represented as a string
	public void addFeature(String feat){
		if(AllFeatures.featSet.contains(feat)) return;
		AllFeatures.featSet.add(feat);
		int index = dict.getIndex(feat);
		if(index < 0)
			return;
//		this.featureString += index+" :"+feat+"\n";		
		if(Main.outputFeatures==false){
			if(Main.featureCounter.containsKey(index))
				Main.featureCounter.put(index, Main.featureCounter.get(index)+1);
			else Main.featureCounter.put(index, 1);
		}
		else{
			if(Main.featureCounter.get(index)>Main.THRESHOLD){
				this.featureString += index+" ";
				Main.reducedFeatures.put(index, feat);
			}
//			else
//				System.err.println("Too small feature: "+index);
		}
		return;
	}// end method addFeature

}// end class AllFeatures
