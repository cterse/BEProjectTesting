package syntaxReconstruction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import MiscellaneousAPIs.*;
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
 * Take individual sentences and reconstruct them. 
 * See that the sentences follow the following 8 rules after the reconstruction is done.
 * 
 * Input file = sentences.txt
 * Output file = TBD
*/

public class SyntaxRecon {
	/*
	Syntax reconstruction rules: 
	1. Discard prepositional phrase (PP), adjective phrase
	(ADJP), determiner (DT) or adjective (JJ), if they
	precede the subject of the sentence. [no]
	
	2. If NP and VP is preceded by “No”, then convert it
	into “NP not VP”. [no]
	
	3. Noun phrases (NP) which are separated by
	connectives like “and, or” are taken as individual
	sentences. If {{NP1}{VP1{ VBZ NP2,NP3 and
	NP4}}} then convert it into {{NP1}{VP1{ VBZ
	NP2 }}}, {{NP1}{VP1{ VBZ NP3}}},
	{{NP1}{VP1{ VBZ NP4}}}. [done for single and]
	
	4. Sentences which are connected by connectives like
	“and, or, but, yet” are split at their connectives
	and created at two individual sentences. If sentence1
	and/or sentence2, then convert it into two sentences
	{sentence1} {sentence2}. [done for single and]
	
	5. If a sentence has no verbs (VP) then discard that
	sentence. [DONE]
	
	6. If a sentence is of the form {{NP1} {VP1 {NP2}
	{VP2 {NP3}}}}, then convert it into two sentences
	like {{NP1} {VP1 {NP2}}} and {{NP2} {VP2
	{NP3}}}. 
	
	7. In the Sentences which are having a semicolon, treat
	the sentence after the semicolon as extra
	information for the preceding sentence and so
	discard sentence after semicolon. [DONE]
	
	8. If a sentence is in passive voice, ask user to convert
	it into active voice. Normally passive voice
	sentences will contain word “be” which gives the
	sense as passive voice form. This needs some user
	interference to decide which sentence acts as
	passive voice. [DONE]
	
	9. Resolve apostrophes 
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
		int count = 0;
		List<String> result = new ArrayList<String>();
		while(sentencesFileScanner.hasNext()) {
			System.out.println("Analysing sentence = "+(++count));
			String sentence = sentencesFileScanner.nextLine();
		
			List<String> temp = reconstructSentence(sentence);
			if(temp!=null)
				result.addAll(temp);
			System.out.println(temp);
			System.out.println("-------------------------------------------------");
			
		}
		try {
			PrintWriter pw = new PrintWriter(new File("andRemovedSentences2.txt"));
			for(int i=0; i<result.size(); i++)
				pw.println(result.get(i));
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<String> reconstructSentence(String sentence) {
		List<String> simpleSentences = null;
		//if sentence is null, something is wrong. Exit program.
		if(sentence == null) {
			System.out.println("SyntaxRecon.reconstructSentence() : sentence is null. Exiting.");
			System.exit(1);
		}
		
		Tree parse = Parser.getParseTree(sentence);
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		
	    //If sentence has no verb, discard it
		if(!TreeManipulation.checkVerbPresent(parse)) {
			System.out.println("Verb not present.");
			return null;
		}
		
		//Check voice of sentence
		//1 = active, 0 = passive
		if(MiscAPI.getVoice(tdl) == 0) {
			System.out.println("Passive sentence.");
			return null;
			//ArrayList<String> temp = new ArrayList<String>();
			//temp.add(sentence);
			//return temp;
		}
		
		//Discard the sentence after the semi-colon
		String removedSemicolon = RemoveConjunction.removeSemicolon(sentence);
		
		//Method to remove and from sentence. Works for a single "and" for now
		simpleSentences = RemoveConjunction.removeAllConjunctions(removedSemicolon);
	    
		return simpleSentences;
	}
	
	public static List<String> reconstructSentences(List<String> sentences) {
		List<String> simpleSentences = new ArrayList<String>();
		
		if(sentences==null || sentences.isEmpty()) {
			System.out.println("SyntaxRecon.reconstructSentences() : sentences are null/empty. Exiting.");
			System.exit(1);
		}
		
		for(int i=0; i<sentences.size(); i++) {
			System.out.println("Simplifying sentence "+(i+1));
			List<String> temp = reconstructSentence(sentences.get(i));
			if(temp!=null)
				simpleSentences.addAll(temp);
		}
		
		return simpleSentences;
	}
	
	
}
