package MiscellaneousAPIs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Parser {
	
	final static String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	static LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	
	public static Tree getParseTree(String sentence) {

		//Already initialized a lexicalized parser above
		
		//Think of this as a factory, that creates tokenizers... :P
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
				
		//Get a tokenizer from the above created factory
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
		
		//Use this tokenizer to tokenize the above String sent2
		List<CoreLabel> rawWords = tok.tokenize();
		
		//Parse the above tokenized sentence
		Tree parse = lp.apply(rawWords);
		
		//Create file "parserOutput.txt" and
		//Print the collapsed dependencies and tagged sentence to file
		File parserOutputFile = new File("parserOutput.txt");
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(parserOutputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TreePrint tp = null;
		tp = new TreePrint("wordsAndTags");
		tp.printTree(parse, printWriter);
		tp = new TreePrint("oneline");
		tp.printTree(parse, printWriter);
		printWriter.println();
	    tp = new TreePrint("typedDependenciesCollapsed");
	    tp.printTree(parse, printWriter);
	    tp = new TreePrint("penn");
	    tp.printTree(parse, printWriter);
	    //printWriter.println();
	    
	    printWriter.close();
	    
	    return parse;
	}

	public static List<TypedDependency> getTypedDependencies(Tree parse) {
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    return tdl;
	}

	public static void printDependencyList(List<TypedDependency> tdl) {
		for(int i=0; i<tdl.size(); i++) {
			System.out.println(tdl.get(i));
		}
	}
	
	public static void main(String[] args) {
		String sentence = "People have a last name.";
		Tree parse = Parser.getParseTree(sentence);
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		
		System.out.println(parse);
		Parser.printDependencyList(tdl);
		//System.out.println(tdl.get(2).dep().index());
		//System.out.println(tdl.get(2).dep().tag());
		//System.out.println(tdl.get(6).gov().equals(tdl.get(6).dep()));
		//System.out.println(tdl.get(1).gov().toString());
		//System.out.println(tdl.get(4).extra());
	
	
	
	}
}
