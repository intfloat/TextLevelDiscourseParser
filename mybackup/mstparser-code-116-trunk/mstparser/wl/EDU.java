package wl;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;


/**
 * 
 * @author Liang Wang
 * @since 2013/9/4
 * @version 2013/11/26
 *
 *	this class is for describing EDU(short for Elementary Discourse Unit, as you know),
 *	it contains its sentence id, line id and sentence content. 
 */
public class EDU {
	
//	sentence number, 0 indexed.
	public int sentenceID;	
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
//	offset within a whole sentence
	public int offset;
//	Paragraph number of current EDU
	public int paragraphID;
//	offset from tail of the sentence
	public int revOffset;
//	offset from the tail of the sentence
	public int revSentenceID;
//	store syntactic information
	public ArrayList<String> syntacticArray;
	
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
	
	/**
	 * 
	 * @throws InvalidFormatException
	 * @throws IOException
	 * 
	 * an empty constructor
	 */
	public EDU() throws InvalidFormatException, IOException{
		// guarantee that tagger and tokenizer only be instantiated once, therefore program would run faster.
		if(modelTaggerIn == null){
			modelTaggerIn = new FileInputStream("en-pos-maxent.bin");
			System.err.println("Loading tagger...");
			modelTagger = new POSModel(modelTaggerIn);
			tagger = new POSTaggerME(modelTagger);
			modelIn = new FileInputStream("en-token.bin");
			System.err.println("Loading tokenizer...");
			model = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(model);
		}
	}// end empty constructor
	
	/**
	 * 
	 * @param line is content of string, space separated.<br>
	 * 
	 * this is a constructor function, you can create a EDU instance 
	 * with code as follows:<br><br>
	 * <code> EDU("this is an example.", 1, 1)</code><br>
	 * 
	 * <br>
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public EDU(String line, String[] rawTokens, String[] rawTags, String[] tokens, String[] tags, 
			int sentenceID, int lineID, int offset, int paraID, ArrayList<String> syntacticArray)
					throws InvalidFormatException, IOException{
		
		// guarantee that tagger and tokenizer only be instantiated once, therefore program would run faster.
		if(modelTaggerIn == null){
			modelTaggerIn = new FileInputStream("en-pos-maxent.bin");
			System.err.println("Loading tagger...");
			modelTagger = new POSModel(modelTaggerIn);
			tagger = new POSTaggerME(modelTagger);
			modelIn = new FileInputStream("en-token.bin");
			System.err.println("Loading tokenizer...");
			model = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(model);
		}// end if clause
		
//		assignment for variables
		this.rawString = line;
		this.sentenceID = sentenceID;
		this.lineID = lineID;
		this.offset = offset;		
		this.paragraphID = paraID;
		this.rawTokens = rawTokens;
		this.rawTags = rawTags;
		this.tokens = tokens;
		this.POStags = tags;
		this.syntacticArray = syntacticArray;
		
		
		// print this extra information only when in test mode
//		if(Main.TEST_MODE == true){
//			System.out.println("EDU information: rawString: "+this.rawString+"\n sentenceID: "+this.sentenceID+"\n +lineID: "
//					+this.lineID+"\n offset "+this.offset+"\n paragraph ID: "+this.paragraphID+"\n");
//			for(int i=0; i<tokens.length; i++)
//				System.out.print(tokens[i]+" "+POStags[i]+" ");		
//			System.out.println();
//		}// end if clause
		
	}// end constructor
	
	/**
	 * 
	 * @return a virtual root node with index -1
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static EDU getRootEDU() throws InvalidFormatException, IOException{
		EDU edu = new EDU();
		String[] str = {"<root>"};
		
		edu.sentenceID = -1;
		edu.lineID = -1;		
		edu.rawString = str[0];
		edu.rawTokens = str;
		edu.rawTags = str;
		edu.tokens = str;
		edu.POStags = str;
		edu.offset = 0;
		edu.paragraphID = -1;
		edu.syntacticArray = new ArrayList<String>();
		
		return edu;
	}// end method getRootEDU

}// end class EDU
