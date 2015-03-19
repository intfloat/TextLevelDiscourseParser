package mstparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class WebParser {
	
//	use openNLP Tagger to get POS tag
//	set their attribute to static to avoid instantiate more than one time.
//	for more details, refer to http://opennlp.apache.org/
	public static InputStream modelTaggerIn = null;
	public static POSModel modelTagger = null;
	public static POSTaggerME tagger = null;
	
//	use openNLP tokenizer to tokenize
//	set their attribute to static to avoid instantiate more than one time.
//	for more details, refer to http://opennlp.apache.org/
	public static InputStream modelIn = null;
	public static TokenizerModel model = null;
	public static Tokenizer tokenizer = null;
	
	private static HashMap<String, String> map = null;
	private static final File feature = new File("lib/features.log");
	private ArrayList<String> text = null;
	private ArrayList<MyEDU> edus = null;
	private static MyEDU root = null;
	private Scanner reader = null;
	private Vector<String> vector = null;
	private static int counter = 0;
	
    private static DocumentBuilderFactory dbf;
    private static DocumentBuilder db;
    private static Document doc;
    private static Element eroot;
	
	public WebParser(File file) throws InvalidFormatException, IOException {
		// guarantee that tagger and tokenizer only be instantiated once, therefore program would run faster.
		if(modelTaggerIn == null){
			modelTaggerIn = new FileInputStream("lib/en-pos-maxent.bin");
			System.err.println("Loading tagger...");
			modelTagger = new POSModel(modelTaggerIn);
			tagger = new POSTaggerME(modelTagger);
			modelIn = new FileInputStream("lib/en-token.bin");
			System.err.println("Loading tokenizer...");
			model = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(model);
		} 
		
		vector = new Vector<String>();
		edus = new ArrayList<MyEDU>();
//		load feature dictionary
//		to be implemented
		map = new HashMap<String, String>();
		reader = new Scanner(feature);
		while (reader.hasNextLine()) {
			String cur = reader.nextLine();			
			int colon = cur.indexOf(':');
			if (colon < 0) {
				System.err.println("Can not find colon: " + cur);
				continue;
			}
			String first = cur.substring(0, colon).trim();
			String second = cur.substring(colon + 1).trim();
//			string to digit
			map.put(second, first);
		} // end while loop
		
//		load text file and store it in an array
		text = new ArrayList<String>();
		reader = new Scanner(file);
		while (reader.hasNextLine()) {
			String cur = reader.nextLine().trim();
			if (cur.length() > 0) text.add(cur);			
//			System.out.println(cur);
		} // end while loop
		
//		generate data file
		generateData();
		
//		denote root edu
		root = MyEDU.getROOT();
		if (edus == null) {
			edus = new ArrayList<MyEDU>();
		}
		edus.clear();
		for (int i=0; i<text.size(); i++) {
			edus.add(new MyEDU(i, text.get(i)));
		} // end for loop
		
	} // end constructor
	
//	generate xml data
	public void generateXML(File out) throws IOException {
		reader = new Scanner(new File("data/out.txt"));
		reader.nextLine();
		reader.nextLine();
		String[] relation = reader.nextLine().trim().split("\\s++");
		String[] parent = reader.nextLine().trim().split("\\s++");
		if (parent.length !=relation.length || parent.length!=text.size()) {
			System.err.println("Length is inconsistent! something is wrong!");
			System.exit(1);
		}
	    dbf = DocumentBuilderFactory.newInstance();
        db = null;
        doc = null;
        try {
            db=dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block            
            e.printStackTrace();
        }
        doc = db.newDocument();
        eroot = doc.createElement("doc");
        doc.appendChild(eroot);        
        for (int i = 0; i < text.size(); ++i) {
            Element element = doc.createElement("EDU");
            element.setAttribute("id", String.valueOf(i + 1));
            element.setAttribute("cont", text.get(i));
            element.setAttribute("parent", parent[i]);
            element.setAttribute("relation", relation[i]);
            eroot.appendChild(element);
        }
        
        //output the xml content into certain file
        FileOutputStream outStream=null;
        try {
            outStream=new FileOutputStream(out);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block                
            e.printStackTrace();
        }
            
        OutputStreamWriter outstreamWriter=new OutputStreamWriter(outStream);
//      call writer
        callWriteXmlFile(doc, outstreamWriter, "utf-8");
        try {
            outstreamWriter.close();
        } catch (IOException e) {                    
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {                    
            // TODO Auto-generated catch block
            e.printStackTrace();
        }                
//		writer.write("</doc>");
//		writer.flush();
		return;
	}
	
//	generate json data
	public void generateJson(File out) throws IOException {
		reader = new Scanner(new File("data/out.txt"));
		reader.nextLine();
		reader.nextLine();
		String[] relation = reader.nextLine().trim().split("\\s++");
		String[] parent = reader.nextLine().trim().split("\\s++");
		if (parent.length !=relation.length || parent.length!=text.size()) {
			System.err.println("Length is inconsistent! something is wrong!");
			System.exit(1);
		}
		FileWriter writer = new FileWriter(out);
		JSONArray arr = new JSONArray();
		for (int i=0; i<text.size(); i++) {
			JSONObject obj = new JSONObject();
			obj.put("id", i + 1);
			obj.put("cont", text.get(i));
			obj.put("parent", parent[i]);
			obj.put("relation", relation[i]);
			arr.put(obj);
		}
		writer.write(arr.toString());
		writer.flush();
		return;
	} // end method generateJson
	
	private void generateData() throws IOException {
		File testFile = new File("data/web.lab");
		int N = text.size();
		FileWriter writer = new FileWriter(testFile);
//		first line
		for (int i=0; i<N-1; i++) {
			writer.write("0 ");
		}
		writer.write("0\n");
//		second line
		for (int i=0; i<N-1; i++) {
			writer.write("NN ");
		}
		writer.write("NN\n");
//		thrid line
		for (int i=0; i<N-1; i++) {
			writer.write("Elaboration ");
		}
		writer.write("Elaboration\n");
//		fourth line
		for (int i=0; i<N-1; i++) {
			writer.write("1 ");
		}
		writer.write("1\n");
		
		writer.flush();
		return;
	} // end method generateData
	
	public Vector<String> getFeatureVector(int i, int j) {
		i--; j--;
		MyEDU first =null;
		MyEDU second = null;
		if (i < 0) first = root;
		else first = edus.get(i);
		if (j < 0) second = root;
		else second = edus.get(j);
		vector.clear();
		
		this.addLengthFeature(first, second);
		this.addPOSFeature(first, second);
		this.addWordPairFeature(first, second);
		
		return vector;
	} // end method getFeatureVector
	
	public Vector<String> getSingleFeatureVector(int index) {
		index--;
		MyEDU first = null;
		if (index < 0) first = root;
		else first = edus.get(index);
		vector.clear();
		
		this.addSingleLengthFeature(first);
		this.addSinglePOSFeature(first);
		this.addSingleWordPairFeature(first);
		
		return vector;
	} // end method getFeatureVector
	
	private void addFeature(String feat) {
//		convert string into digits
		if (map.containsKey(feat)) {
//			System.out.println("counter: " + (counter++) + " feature: "+feat + " " + map.get(feat));
			vector.add(map.get(feat));
		}
		return;
	} // end method addFeature
	
	
	private void addSinglePOSFeature(MyEDU first){		
		this.addFeature("POFS_"+first.POStags[0]);
		this.addFeature("POSFL_"+first.POStags[first.POStags.length-1]);
		for(int i=0; i<first.POStags.length; i++)
			this.addFeature("PFOS_"+first.POStags[i]);		
		return;
	}// end method addSinglePOSFeature
	
//	add POS features
	private void addPOSFeature(MyEDU first, MyEDU second){
		this.addFeature("POA_"+first.POStags[0]+" "+second.POStags[0]);
		this.addFeature("POF_"+first.POStags[0]);
		this.addFeature("POS_"+second.POStags[0]);
		for(int i=0; i<first.POStags.length; i++)
			this.addFeature("PFO_"+first.POStags[i]);
		for(int i=0; i<second.POStags.length; i++)
			this.addFeature("PSO_"+second.POStags[i]);
	}// end method addPOSFeature
	
	
	private void addSingleLengthFeature(MyEDU first){
		int firstLength = first.tokens.length;		
		this.addFeature("SLSF5_"+(firstLength/5));
		this.addFeature("SLSF_"+(firstLength));
		return;
	}// end method addSingleLengthFeature
	
//	add length features
	private void addLengthFeature(MyEDU first, MyEDU second){
		int firstLength = first.tokens.length;
		int secondLength = second.tokens.length;
		this.addFeature("LPA_"+(firstLength/5)+" "+(secondLength/5));
		this.addFeature("LSF_"+(firstLength/5));
		this.addFeature("LSS_"+(secondLength/5));
		this.addFeature("LDI_"+((firstLength-secondLength)/5));
	}// end method addLengthFeature
	
	private void addSingleWordPairFeature(MyEDU first){
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
	private void addWordPairFeature(MyEDU first, MyEDU second){
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
	
	  /**
     * 
     * @param Document doc
     * @param Writer w
     * @param encoding
     */
    private void callWriteXmlFile(Document doc, Writer w, String encoding) {
          try {
              Source source = new DOMSource(doc);
              Result result = new StreamResult(w);
              Transformer xformer = TransformerFactory.newInstance()
                      .newTransformer();
              xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
              xformer.setOutputProperty(OutputKeys.INDENT, "yes");            
              xformer.transform(source, result);
          } 
          catch (TransformerConfigurationException e) {
              e.printStackTrace();
          }
          catch (TransformerException e) {
              e.printStackTrace();
          }
    }  // end method callWriteXmlFile



} // end class WebParser
