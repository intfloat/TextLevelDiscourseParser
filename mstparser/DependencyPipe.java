package mstparser;

import mstparser.io.*;
import java.util.Vector;
import java.io.*;

import gnu.trove.*;
import java.util.*;

public class DependencyPipe {

    public Alphabet dataAlphabet;    
	
    public Alphabet typeAlphabet;

    private DependencyReader depReader;
    private DependencyWriter depWriter;

    public String[] types;
    public int[] typesInt;
	
    public boolean labeled = false;
    private boolean isCONLL = true;

    private ParserOptions options;

    public DependencyPipe (ParserOptions options) throws IOException {
	this.options = options;

	if (!options.format.equals("CONLL"))
	    isCONLL = false;

	dataAlphabet = new Alphabet();
	typeAlphabet = new Alphabet();

	depReader = DependencyReader.createDependencyReader(options.format, options.discourseMode);
    }

    public void initInputFile (String file) throws IOException {
	labeled = depReader.startReading(file);
    }

    public void initOutputFile (String file) throws IOException {
	depWriter = 
	    DependencyWriter.createDependencyWriter(options.format, labeled);
	depWriter.startWriting(file);
    }

    public void outputInstance (DependencyInstance instance) throws IOException {
	depWriter.write(instance);
    }

    public void close () throws IOException {
	if (null != depWriter) {
	    depWriter.finishWriting();
	}
    }

    public String getType (int typeIndex) {
	return types[typeIndex];
    }

    protected final DependencyInstance nextInstance() throws IOException {
	DependencyInstance instance = depReader.getNext();
	if (instance == null || instance.forms == null) return null;

	instance.setFeatureVector(createFeatureVector(instance));
	
	String[] labs = instance.deprels;
	int[] heads = instance.heads;

	StringBuffer spans = new StringBuffer(heads.length*5);
	for(int i = 1; i < heads.length; i++) {
	    spans.append(heads[i]).append("|").append(i).append(":").append(typeAlphabet.lookupIndex(labs[i])).append(" ");
	}
	instance.actParseTree = spans.substring(0,spans.length()-1);
	
	return instance;
    }


    public int[] createInstances(String file,
				 File featFileName) throws IOException {

	createAlphabet(file);

	System.out.println("Num Features: " + dataAlphabet.size());

	labeled = depReader.startReading(file);

	TIntArrayList lengths = new TIntArrayList();

	ObjectOutputStream out = options.createForest
	    ? new ObjectOutputStream(new FileOutputStream(featFileName))
	    : null;
		
	DependencyInstance instance = depReader.getNext();
	int num1 = 0;

	System.out.println("Creating Feature Vector Instances: ");
	while(instance != null) {
	    System.out.print(num1 + " ");
	    
	    instance.setFeatureVector(createFeatureVector(instance));
			
	    String[] labs = instance.deprels;
	    int[] heads = instance.heads;

	    StringBuffer spans = new StringBuffer(heads.length*5);
	    for(int i = 1; i < heads.length; i++) {
		spans.append(heads[i]).append("|").append(i).append(":").append(typeAlphabet.lookupIndex(labs[i])).append(" ");
	    }
	    instance.actParseTree = spans.substring(0,spans.length()-1);

	    lengths.add(instance.length());
			
	    if(options.createForest)
		writeInstance(instance,out);
	    instance = null;
			
	    instance = depReader.getNext();

	    num1++;
	}

	System.out.println();
	
	
	// added by me
	System.out.println("Num Features: " + dataAlphabet.size());

	closeAlphabets();
		
	if(options.createForest)
	    out.close();

	return lengths.toNativeArray();
		
    }

    private final void createAlphabet(String file) throws IOException {

	System.out.print("Creating Alphabet ... ");

	labeled = depReader.startReading(file);

	DependencyInstance instance = depReader.getNext();

	while(instance != null) {
	    
	    String[] labs = instance.deprels;
	    for(int i = 0; i < labs.length; i++)
		typeAlphabet.lookupIndex(labs[i]);
			
	    createFeatureVector(instance);
			
	    instance = depReader.getNext();
	}

	
	closeAlphabets();

	System.out.println("Done.");
    }
	
    public void closeAlphabets() {
	dataAlphabet.stopGrowth();
	typeAlphabet.stopGrowth();

	types = new String[typeAlphabet.size()];
	Object[] keys = typeAlphabet.toArray();
	for(int i = 0; i < keys.length; i++) {
	    int indx = typeAlphabet.lookupIndex(keys[i]);
	    types[indx] = (String)keys[i];
	}

	KBestParseForest.rootType = typeAlphabet.lookupIndex("<root-type>");
    }


    // add with default 1.0
    public final void add(String feat, FeatureVector fv) {
	int num = dataAlphabet.lookupIndex(feat);
//	System.out.println("feature: " + feat + " index: " + num);
	if(num >= 0)
	    fv.add(num, 1.0);
    }

    public final void add(String feat, double val, FeatureVector fv) {
	int num = dataAlphabet.lookupIndex(feat);
	if(num >= 0)
	    fv.add(num, val);
    }

	
    public FeatureVector createFeatureVector(DependencyInstance instance) throws IOException {

	final int instanceLength = instance.length();

	String[] labs = instance.deprels;
	int[] heads = instance.heads;

	FeatureVector fv = new FeatureVector();
	for(int i = 0; i < instanceLength; i++) {
//		System.err.print("in: "+i);
	    if(heads[i] == -1)
		continue;
	    int small = i < heads[i] ? i : heads[i];
	    int large = i > heads[i] ? i : heads[i];
	    boolean attR = i < heads[i] ? false : true;
	    addCoreFeatures(instance,small,large,attR,fv);
	    if(labeled) {
		addLabeledFeatures(instance,i,labs[i],attR,true,fv);
		addLabeledFeatures(instance,heads[i],labs[i],attR,false,fv);
	    }
	}

	addExtendedFeatures(instance, fv);

	return fv;
    }

    protected void addExtendedFeatures(DependencyInstance instance, 
				       FeatureVector fv) {}


    public void addCoreFeatures(DependencyInstance instance,
				int small,
				int large,
				boolean attR,
				FeatureVector fv) throws IOException {

//	String[] forms = instance.forms;
//	String[] pos = instance.postags;
//	String[] posA = instance.cpostags;
	
//	added by me
//	int index = Integer.parseInt(forms[forms.length-1].trim());
	Vector<String> vector = new Vector<String>();
	vector.clear();
	if (attR == true)
		vector = DependencyParser.webParser.getFeatureVector(small, large);
	else
		vector = DependencyParser.webParser.getFeatureVector(large, small);
	
//	System.out.println("small: "+small+" large: "+large);
//	if(DependencyParser.options.preload == false){
////		to run in limited memory cases
//		if(DependencyParser.currentFeature != index){
//			DependencyParser.singleFeature = new iFeature(DependencyParser.featureFiles[index]);
//			DependencyParser.currentFeature = index;
//		}
//		if(attR == true)
//			vector = DependencyParser.singleFeature.vector[small][large];
//		else
//			vector = DependencyParser.singleFeature.vector[large][small];
//	}// end if clause
//	
////	it has been verified, when attR is true, small is head, large is dependent
//	else if(DependencyParser.options.preload == true){
//		if(attR == true)
//			vector = DependencyParser.ifeatures[index].vector[small][large];
//		else if(attR == false)
//			vector = DependencyParser.ifeatures[index].vector[large][small];
//	}// end else if clause
	
//	add feature iteratively
	String status = "";
	if(attR == true) status += "R";
	else status += "L";
	for(int i=0; i<vector.size(); i++){
		add(vector.get(i), fv);
		add(status+vector.get(i), fv);
	}
			
	return; 
    }
 
    public void addLabeledFeatures(DependencyInstance instance,
				   int word,
				   String type,
				   boolean attR,
				   boolean childFeatures,
				   FeatureVector fv) throws IOException {
	
	if(!labeled) 
	    return;
//	int curHead = instance.heads[word];
	
//	System.out.println("word: " + word + " type: " + type);
//	added by Wang Liang
	String status = "";
	if(attR == true)
		status += "R";
	else status += "L";
	if(childFeatures == true)
		status += "C";
	else status += "P";
	
//	String[] forms = instance.forms;
//	String[] pos = instance.postags;
//	String[] posA = instance.cpostags;
	
//	added by me
//	int index = Integer.parseInt(forms[forms.length-1].trim());
	Vector<String> vector = new Vector<String>();
	vector.clear();
	vector = DependencyParser.webParser.getSingleFeatureVector(word);
//	vector = DependencyParser.ifeatures[index].singleVector[word];
	
//	int small = Math.min(word, curHead);
//	int large = Math.max(curHead, word);
////	edge coming into ROOT node, there is no actual edge, so just return
//	if(small<0 || large<0)
//		return;
//	
//	if(DependencyParser.options.preload == false){
////		to run in limited memory cases
//		if(DependencyParser.currentFeature != index){
//			DependencyParser.singleFeature = new iFeature(DependencyParser.featureFiles[index]);
//			DependencyParser.currentFeature = index;
//		}		
//		if(attR == true)
//			vector = DependencyParser.singleFeature.vector[small][large];
//		else
//			vector = DependencyParser.singleFeature.vector[large][small];
//	}// end if clause
//	else if(DependencyParser.options.preload == true){
////		it has been verified, when attR is true, small is head, large is dependent
//		if(attR == true)
//			vector = DependencyParser.ifeatures[index].vector[small][large];
//		else if(attR == false)
//			vector = DependencyParser.ifeatures[index].vector[large][small];
//	}// end else if clause
	
//	add feature iteratively
//	System.err.println("size: " + vector.size());
	for(int i=0; i<vector.size(); i++){
//		add(vector.get(i), fv);		
		add(status+type+vector.get(i), fv);
	}
			
	return;

    }


    public void fillFeatureVectors(DependencyInstance instance,
				   FeatureVector[][][] fvs,
				   double[][][] probs,
				   FeatureVector[][][][] nt_fvs,
				   double[][][][] nt_probs, Parameters params) throws IOException {

	final int instanceLength = instance.length();

	// Get production crap.		
	for(int w1 = 0; w1 < instanceLength; w1++) {
	    for(int w2 = w1+1; w2 < instanceLength; w2++) {
		for(int ph = 0; ph < 2; ph++) {
		    boolean attR = ph == 0 ? true : false;
		    
		    int childInt = attR ? w2 : w1;
		    int parInt = attR ? w1 : w2;
		    
		    FeatureVector prodFV = new FeatureVector();
		    addCoreFeatures(instance,w1,w2,attR, prodFV);
		    double prodProb = params.getScore(prodFV);
		    fvs[w1][w2][ph] = prodFV;
		    probs[w1][w2][ph] = prodProb;
		}
	    }
	}

	if(labeled) {
	    for(int w1 = 0; w1 < instanceLength; w1++) {
		for(int t = 0; t < types.length; t++) {
		    String type = types[t];
		    for(int ph = 0; ph < 2; ph++) {

			boolean attR = ph == 0 ? true : false;
			for(int ch = 0; ch < 2; ch++) {

			    boolean child = ch == 0 ? true : false;

			    FeatureVector prodFV = new FeatureVector();
			    addLabeledFeatures(instance,w1,
					       type,attR,child, prodFV);
			    
			    double nt_prob = params.getScore(prodFV);
			    nt_fvs[w1][t][ph][ch] = prodFV;
			    nt_probs[w1][t][ph][ch] = nt_prob;
			    
			}
		    }
		}
	    }
	}		
    }


    /**
     * Write an instance to an output stream for later reading.
     *
     **/
    protected void writeInstance(DependencyInstance instance, ObjectOutputStream out) {

	int instanceLength = instance.length();

	try {

	    for(int w1 = 0; w1 < instanceLength; w1++) {
		for(int w2 = w1+1; w2 < instanceLength; w2++) {
		    for(int ph = 0; ph < 2; ph++) {
			boolean attR = ph == 0 ? true : false;
			FeatureVector prodFV = new FeatureVector();
			addCoreFeatures(instance,w1,w2,attR,prodFV);
			out.writeObject(prodFV.keys());
		    }
		}
	    }
	    out.writeInt(-3);

	    if(labeled) {
		for(int w1 = 0; w1 < instanceLength; w1++) {		    
		    for(int t = 0; t < types.length; t++) {
			String type = types[t];			
			for(int ph = 0; ph < 2; ph++) {
			    boolean attR = ph == 0 ? true : false;
			    for(int ch = 0; ch < 2; ch++) {
				boolean child = ch == 0 ? true : false;
				FeatureVector prodFV = new FeatureVector();
				addLabeledFeatures(instance,w1,
						   type, attR,child,prodFV);
				out.writeObject(prodFV.keys());
			    }
			}
		    }
		}
		out.writeInt(-3);
	    }

	    writeExtendedFeatures(instance, out);

	    out.writeObject(instance.fv.keys());
	    out.writeInt(-4);

	    out.writeObject(instance);
	    out.writeInt(-1);

	    out.reset();

	} catch (IOException e) {}
		
    }
	

    /**
     * Override this method if you have extra features that need to be
     * written to disk. For the basic DependencyPipe, nothing happens.
     *
     */
    protected void writeExtendedFeatures (DependencyInstance instance, ObjectOutputStream out) 
	throws IOException {}


    /**
     * Read an instance from an input stream.
     *
     **/
    public DependencyInstance readInstance(ObjectInputStream in,
					   int length,
					   FeatureVector[][][] fvs,
					   double[][][] probs,
					   FeatureVector[][][][] nt_fvs,
					   double[][][][] nt_probs,
					   Parameters params) throws IOException {

	try {

	    // Get production crap.		
	    for(int w1 = 0; w1 < length; w1++) {
		for(int w2 = w1+1; w2 < length; w2++) {
		    for(int ph = 0; ph < 2; ph++) {
			FeatureVector prodFV = new FeatureVector((int[])in.readObject());
			double prodProb = params.getScore(prodFV);
			fvs[w1][w2][ph] = prodFV;
			probs[w1][w2][ph] = prodProb;
		    }
		}
	    }
	    int last = in.readInt();
	    if(last != -3) { System.out.println("Error reading file."); System.exit(0); }
	    
	    if(labeled) {
		for(int w1 = 0; w1 < length; w1++) {
		    for(int t = 0; t < types.length; t++) {
			String type = types[t];
//			System.out.println(t+" "+type);
			
			for(int ph = 0; ph < 2; ph++) {						
			    for(int ch = 0; ch < 2; ch++) {
				FeatureVector prodFV = new FeatureVector((int[])in.readObject());
				double nt_prob = params.getScore(prodFV);
				nt_fvs[w1][t][ph][ch] = prodFV;
				nt_probs[w1][t][ph][ch] = nt_prob;
			    }
			}
		    }
		}
		last = in.readInt();
		if(last != -3) { System.out.println("Error reading file."); System.exit(0); }
	    }

	    FeatureVector nfv = new FeatureVector((int[])in.readObject());
	    last = in.readInt();
	    if(last != -4) { System.out.println("Error reading file."); System.exit(0); }

	    DependencyInstance marshalledDI;
	    marshalledDI = (DependencyInstance)in.readObject();
	    marshalledDI.setFeatureVector(nfv);	

	    last = in.readInt();
	    if(last != -1) { System.out.println("Error reading file."); System.exit(0); }

	    return marshalledDI;

	} catch(ClassNotFoundException e) { 
	    System.out.println("Error reading file."); System.exit(0); 
	} 

	// this won't happen, but it takes care of compilation complaints
	return null;
    }
		
}
