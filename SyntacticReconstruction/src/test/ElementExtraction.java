package test;
/*
 * Code to extract classes from the sentences generated after Syntactic Reconstruction phase.
 * 
 * Input File = TBD. Output file of SyntacticRecontruction.java class
 * Output File = TBD.  
 * 
 * For attribute extraction, a list of possession verbs is created.
 * Objects of sentences containing these verbs in non-auxiliary form are taken as attributes.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class ElementExtraction {
	
	final static String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	static LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	
	static List<Classes> classList = new ArrayList<Classes>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		//input file and load sentences into an ArrayList
		File inputFile = null;
		if(args.length!=0)
			inputFile = new File(args[0]);
		else inputFile = new File("andRemovedSentences2.txt");
		Scanner inputFileScanner = null;
		try {
			inputFileScanner = new Scanner(inputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<String> sentences = new ArrayList<String>();
		while( inputFileScanner.hasNextLine() ) {
			sentences.add(inputFileScanner.nextLine());
		}
		
		//output file
		PrintWriter outputPW = null;
		try {
			outputPW = new PrintWriter("testOutput.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Classes> classList = ElementExtractionAPI.extractEntities(sentences);
		for(int i=0; i<classList.size(); i++) {
			System.out.println(classList.get(i)+"\n-----------------------");
		}
		
		outputPW.close();
	}

}
