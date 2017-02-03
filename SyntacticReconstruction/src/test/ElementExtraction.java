package test;

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
import edu.stanford.nlp.trees.Tree;

public class ElementExtraction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		//input file
		File inputFile = new File("test.txt");
		Scanner inputFileScanner = null;
		try {
			inputFileScanner = new Scanner(inputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//output file
		PrintWriter outputPW = null;
		try {
			outputPW = new PrintWriter("testOutput.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> sentences = new ArrayList<String>();
		while( inputFileScanner.hasNextLine() ) {
			sentences.add(inputFileScanner.nextLine());
		}
		
		int i = 4;
		Tree parse = parseSentence(sentences.get(i));
		System.out.println(sentences.get(i));
		System.out.println(parse);
		
		//Create a list of possession verbs
		List<String> possessionVerbs = new ArrayList<String>();
		File possVerbsFile = new File("possessionVerbs.txt");
		Scanner possVerbsFileScanner = null;
		try {
			possVerbsFileScanner = new Scanner(possVerbsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while( possVerbsFileScanner.hasNext() ) {
			possessionVerbs.add(possVerbsFileScanner.next());
		}
		
		Iterator<Tree> it = parse.iterator();
		//Traverse the parse tree to find potential elements
		List<Tree> potentialClasses = new ArrayList<Tree>();
		List<Tree> potentialMethods = new ArrayList<Tree>();
		List<Tree> potentialAttributes = new ArrayList<Tree>();
		Tree node = null;
		while( it.hasNext() ) {
			node = it.next();
			if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
				node = it.next();
				potentialClasses.add(node);
			}
			if( node.value().equalsIgnoreCase("VB") || node.value().equalsIgnoreCase("VBD") || node.value().equalsIgnoreCase("VBG") || node.value().equalsIgnoreCase("VBN") || node.value().equalsIgnoreCase("VBP") || node.value().equalsIgnoreCase("VBZ") ) {
				if( possessionVerbs.contains(node.value()) ) {
					//verb is present in list of possession verbs
					//if yes, then the following noun becomes a potential attribute
					
				}
				node = it.next();
				potentialMethods.add(node);
			}
		}
		System.out.println("\nPotential classes: ");
		System.out.println(potentialClasses);
		System.out.println("\nPotential methods: ");
		System.out.println(potentialMethods);
		
		outputPW.close();
	}

	private static Tree parseSentence(String sentence) {
		// TODO Auto-generated method stub
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
		List<CoreLabel> rawWords = tok.tokenize();
		Tree parse = lp.apply(rawWords);
		return parse;
	}

}
