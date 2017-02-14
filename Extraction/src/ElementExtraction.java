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
		
		int i = 5;
		Tree parse = parseSentence(sentences.get(i));
		
		//Get the dependencies
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		
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
		//System.out.println("Possession verbs = "+possessionVerbs);
		
		Iterator<Tree> it = parse.iterator();
		//Traverse the parse tree to find potential elements
		List<String> potentialClasses = new ArrayList<String>();
		List<Tree> potentialMethods = new ArrayList<Tree>();
		List<String> potentialAttributes = new ArrayList<String>(); //!!!!!!!! check types
		Tree node = null;
		while( it.hasNext() ) {
			node = it.next();
			
			//Check for nouns i.e. Classes
			if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNP") || node.value().equalsIgnoreCase("NNPS") || node.value().equalsIgnoreCase("NNS") ) {
				String compound = it.next().value();
				node = it.next();
				if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNP") || node.value().equalsIgnoreCase("NNPS") || node.value().equalsIgnoreCase("NNS") ) {
					//This means there are two nouns back to back i.e compound nouns
					String className = it.next().value();
					potentialClasses.add(compound+"_"+className);
				} else {
					potentialClasses.add(compound);
				}
			}
			
			//Check for verbs
			if( node.value().equalsIgnoreCase("VB") || node.value().equalsIgnoreCase("VBD") || node.value().equalsIgnoreCase("VBG") || node.value().equalsIgnoreCase("VBN") || node.value().equalsIgnoreCase("VBP") || node.value().equalsIgnoreCase("VBZ") ) {
				System.out.println("Checking verbs");
				node = it.next();
				if( possessionVerbs.contains(node.value()) ) {
					//verb is present in list of possession verbs
					//if yes, then the following noun becomes a potential attribute
					System.out.println(node.value()+" is present.\n");
					System.out.println("Object = "+getObject(tdl));
					potentialAttributes.add(getObject(tdl));
				} else {
					//node = it.next();
					potentialMethods.add(node);
				}
			}
		}
		
		System.out.println("\nPotential classes: ");
		System.out.println(potentialClasses);
		System.out.println("\nPotential methods: ");
		System.out.println(potentialMethods);
		System.out.println("\nPotential attributes: ");
		System.out.println(potentialAttributes);
		
		outputPW.close();
	}

	private static String getObject(List<TypedDependency> tdl) {
		// TODO Auto-generated method stub
		Iterator<TypedDependency> tdlIt = tdl.iterator();
		while( tdlIt.hasNext() ) {
			TypedDependency temp = tdlIt.next();
			if( temp.reln().toString().equalsIgnoreCase("dobj") ) {
				return temp.dep().value(); 
			}
		}
		return null;
	}

	private static Tree parseSentence(String sentence) {
		// TODO Auto-generated method stub
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
		List<CoreLabel> rawWords = tok.tokenize();
		Tree parse = lp.apply(rawWords);
		return parse;
	}

}
