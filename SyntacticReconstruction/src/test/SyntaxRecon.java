package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
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
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/*
Take individual sentences and reconstruct them.
See that the sentences follow the following 8 rules after the reconstruction is done.
*/
public class SyntaxRecon {
	/*
	Syntax reconstruction rules: 
	1. Discard prepositional phrase (PP), adjective phrase
	(ADJP), determiner (DT) or adjective (JJ), if they
	precedes the subject of the sentence.
	
	2. If NP and VP is preceded by �No�, then convert it
	into �NP not VP�.
	
	3. Noun phrases (NP) which are separated by
	connectives like �and, or� are taken as individual
	sentences. If {{NP1}{VP1{ VBZ NP2,NP3 and
	NP4}}} then convert it into {{NP1}{VP1{ VBZ
	NP2 }}}, {{NP1}{VP1{ VBZ NP3}}},
	{{NP1}{VP1{ VBZ NP4}}}.
	
	4. Sentences which are connected by connectives like
	�and, or, but, yet� are split at their connectives
	and created at two individual sentences. If sentence1
	and/or sentence2, then convert it into two sentences
	{sentence1} {sentence2}.
	
	5. If a sentence has no verbs (VP) then discard that
	sentence.
	
	6. If a sentence is of the form {{NP1} {VP1 {NP2}
	{VP2 {NP3}}}}, then convert it into two sentences
	like {{NP1} {VP1 {NP2}}} and {{NP2} {VP2
	{NP3}}}.
	
	7. In the Sentences which are having a semicolon, treat
	the sentence after the semicolon as extra
	information for the preceding sentence and so
	discard sentence after semicolon.
	
	8. If a sentence is in passive voice, ask user to convert
	it into active voice. Normally passive voice
	sentences will contain word �be� which gives the
	sense as passive voice form. This needs some user
	interference to decide which sentence acts as
	passive voice.
	*/
	
	final static String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	static LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Open the file containing individual sentences for reconstruction
		File sentencesFile = new File("sentences.txt");
		Scanner sentencesFileScanner = null;
		try {
			sentencesFileScanner = new Scanner(sentencesFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Now, scan each of the sentences and perform syntactic reconstruction.
		//For now, we will test only the first sentence.
		String sentence1 = sentencesFileScanner.nextLine();
		
		//First we need to parse the sentence.
		Tree parse = parseSentence(sentence1);
		
		//Get the dependencies
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		//System.out.println(tdl);
	    
		//Now, we have the parser output for the sentence in the "parserOutput.txt" file
		//a scanner to read the parserOutput.txt file
		File parserOutput = new File("parserOutput.txt");
		Scanner parserOutputFileScanner = null;
		try {
			parserOutputFileScanner = new Scanner(parserOutput);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Method to remove and from sentence.
		removeAnd(parse, tdl);
	}

	private static void removeAnd(Tree parse, List<TypedDependency> tdl) {
		// TODO Auto-generated method stub
		
		//First, check if there is an "and" in the sentence
		Iterator<Tree> treeIterator = parse.iterator();
		Tree CCNode = null;		//this will point to the CC node
		while( treeIterator.hasNext() ) {
			Tree node = treeIterator.next();
			if(node.value().equalsIgnoreCase("CC"))
				if(node.firstChild().value().equalsIgnoreCase("and")) {
					CCNode = node;
					break;
				}
		}
		if(CCNode == null) {
			//"And" not found
			System.out.println("And not found "+CCNode);
			return;
		} else {
			//"And" is found in the sentence
			System.out.println("And found at node = "+CCNode.nodeNumber(parse));
			
			//Create a file to store sentences after removing and from them
			File file = new File("andRemovedSentences.txt");
			PrintWriter andRemovedFilePW = null; 
			try {
				andRemovedFilePW = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Check if the sentence is in active or passive voice
			//Use the dependencies for checking the voice
			boolean sentencePassive = false;
			for(int i=0; i<tdl.size(); i++) {
				String relation = tdl.get(i).reln().toString();
				if(relation.equalsIgnoreCase("auxpass") || relation.equalsIgnoreCase("nsubjpass")) {
					sentencePassive = true;
					break;
				}
			}
			if( sentencePassive ) {
				//We know that sentence is in passive voice
				System.out.println("Sentence is in passive voice");
				
			} else {
				//We know that sentence is in active voice
				System.out.println("Sentence is in active voice ");
				
				//Store parent of the CC node
				Tree CCParent = CCNode.ancestor(1, parse);
				System.out.println("CC parent = "+CCParent.value());
				
				//Get the words in the relation of AND!!!!!!!!!
				//Considering only one "and" in the sentence....For now, this isn't needed
				/*String gov = "";
				String dep = "";
				for(int i=0; i<tdl.size(); i++) {
					if( tdl.get(i).reln().toString().equalsIgnoreCase("conj:and") ) {
						gov = tdl.get(i).gov().value();
						dep = tdl.get(i).dep().value();
					}
				}
				System.out.println("Gov = "+gov+" dep = "+dep);
				
				Check the placing of "and" wrt to gov and dep
				Tree prevSibling = TreeManipulation.getPreviousSibling(CCNode, parse);
				Tree nextSibling = TreeManipulation.getNextSibling(CCNode, parse);
				System.out.println("prev = "+prevSibling+" next = "+nextSibling);
				String beforeAnd = prevSibling.lastChild().toString();
				String afterAnd = nextSibling.firstChild().toString();
				System.out.println("beforeAnd = "+beforeAnd+" afterAnd = "+afterAnd);
				*/	
				
				Iterator<Tree> CCParentIterator = parse.iterator();
				//get the first sentence
				while( CCParentIterator.hasNext() ) {
					Tree node = CCParentIterator.next();
					//if( node.value().equals(".") )
						//break;
					if( node.equals(CCNode) ) {
						Tree CCParentNextSibling = TreeManipulation.getNextSibling(CCParent, parse);
						if( CCParentNextSibling != null && CCParentNextSibling.value().equalsIgnoreCase("PP") ) {
							CCParentIterator = CCParentNextSibling.iterator();
						} else {
							break;
						}
					}
					if( node.isLeaf() ) {
						andRemovedFilePW.print(node.value()+" ");
						System.out.print(node.value()+" ");
					}
				}
				System.out.println();
				andRemovedFilePW.println();
				
				//get the second sentence
				CCParentIterator = parse.iterator();
				if( !TreeManipulation.getNextSibling(CCNode, parse).value().equalsIgnoreCase("VP") ) {
					//There is no VP after CC
					//E.g. library issues books and loans to students
					//Take NP+VBZ from the VP before the CC
					while(CCParentIterator.hasNext()) {
						Tree node = CCParentIterator.next();
						//if( node.value().equals(".") )
							//break;
						if( node.equals(CCParent) ) {
							//break;
							while( !node.equals(CCNode) ) {
								node = CCParentIterator.next();
							}
							//System.out.println(node.value());
							node = CCParentIterator.next();
							node = CCParentIterator.next();
							//System.out.println(node.value());
						} else if(node.isLeaf()) {
							andRemovedFilePW.print(node.value()+" ");
							System.out.print(node.value()+" ");
						}
					}
					System.out.println();
				}
				else if ( TreeManipulation.getNextSibling(CCNode, parse).value().equalsIgnoreCase("VP") ) {
					//There is a VP after CC
					//E.g. The library issues books to students and issues loans to teachers.
					//Take NP from before CC and the rest after CC
					Tree node = null;
					while( CCParentIterator.hasNext() ) {
						node = CCParentIterator.next();
						//if( node.value().equalsIgnoreCase(".") )
							//break;
						if( node.value().equalsIgnoreCase("VP") ) {
							while( !node.equals(TreeManipulation.getNextSibling(CCNode, parse)) ) {
								node = CCParentIterator.next();
							}
						}
						if( node.isLeaf() ) {
							andRemovedFilePW.print(node.value()+" ");
							System.out.print(node.value()+" ");
						}
					}
					System.out.println();
					andRemovedFilePW.println();
				}
				else if ( TreeManipulation.getNextSibling(CCNode, parse).value().equalsIgnoreCase("NP") ) {
					//THere is NP after CC
					//E.g. Library issues books to students and school is a building. 
					//Directly split at the CC
					Tree node = TreeManipulation.getNextSibling(CCParent, parse);
					CCParentIterator = node.iterator();
					while( CCParentIterator.hasNext() ) {
						node = CCParentIterator.next();
						//if( node.value().equals(".") )
							//break;
						if( node.isLeaf() ) {
							andRemovedFilePW.print(node.value()+" ");
							System.out.print(node.value()+" ");
						}
					}
					System.out.println();
				}
				
				//OpenFile.open("andRemovedSentences.txt");
				andRemovedFilePW.close();
				
				//System.out.println("\n"+TreeManipulation.getNextSibling(CCNode, parse));
				//System.out.println(TreeManipulation.getPreviousSibling(CCNode, parse));
			}
		}
	}

	private static Tree parseSentence(String sentence1) {
		// TODO Auto-generated method stub
		//String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		//LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		
		//Think of this as a factory, that creates tokenizers... :P
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
				
		//Get a tokenizer from the above created factory
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence1));
		
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
	    
	   /* TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    tdl = gs.typedDependenciesCCprocessed();
	    System.out.println(tdl);*/
	    /*String temp = tdl.get(3).toString();
	    System.out.println(tdl.get(3));
	    System.out.println(tdl.get(3).dep());
	    System.out.println(tdl.get(3).gov());
	    System.out.println(tdl.get(3).reln());
	    System.out.println();*/
	    
	    printWriter.close();
	    
	    //OpenFile.open("parserOutput.txt");
	    //System.out.print(parse.size()+" ");
	    //System.out.print(parse.numChildren()+"\n");
	    //System.out.println(parse.toString());
	    //parse.pennPrint();
	    //return tdl;
	    return parse;
	}

}