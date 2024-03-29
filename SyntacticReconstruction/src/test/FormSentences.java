package test;

/*
 * Code to form proper sentences given a paragraph of text.
 * Called by Test.java
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.util.StringUtils;

public class FormSentences {
	public static List<String> getSentences(String fileName) throws FileNotFoundException {
		String paragraph = "";
		File file = new File(fileName);
		Scanner t = new Scanner(file);
		StringBuilder temp = new StringBuilder("");
		while(t.hasNextLine()) {
			temp.append(t.nextLine());
			temp.append(' ');
		}
		paragraph = temp.toString();
		//System.out.print(paragraph);
		/*try {
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.print(paragraph);
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		List<CoreLabel> tokens = new ArrayList<CoreLabel>();
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(paragraph), new CoreLabelTokenFactory(), ""); 
		while(tokenizer.hasNext()) {
			tokens.add(tokenizer.next());
		}
		
		List<List<CoreLabel>> sentences = new WordToSentenceProcessor<CoreLabel>().process(tokens);
		
		int end;
		int start = 0;
		List<String> sentenceList = new ArrayList<String>();
		for (List<CoreLabel> sentence: sentences) {
		    end = sentence.get(sentence.size()-1).endPosition();
		    sentenceList.add(paragraph.substring(start, end).trim());
		    start = end;
		}
		//System.out.println(StringUtils.join(sentenceList, "  "));
		/*for(int i=0; i<sentenceList.size(); i++) {
			System.out.println(sentenceList.get(i));
		}*/
		return sentenceList;
	}
	
	/*public static void main(String[] args) throws FileNotFoundException {
		String paragraph = "";
		File file = new File("anotherTest.txt");
		Scanner t = new Scanner(file);
		StringBuilder temp = new StringBuilder("");
		while(t.hasNextLine()) {
			temp.append(t.nextLine());
			temp.append(' ');
		}
		paragraph = temp.toString();
		//System.out.print(paragraph);
		try {
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.print(paragraph);
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<CoreLabel> tokens = new ArrayList<CoreLabel>();
		PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(paragraph), new CoreLabelTokenFactory(), ""); 
		while(tokenizer.hasNext()) {
			tokens.add(tokenizer.next());
		}
		
		List<List<CoreLabel>> sentences = new WordToSentenceProcessor<CoreLabel>().process(tokens);
		
		int end;
		int start = 0;
		List<String> sentenceList = new ArrayList<String>();
		for (List<CoreLabel> sentence: sentences) {
		    end = sentence.get(sentence.size()-1).endPosition();
		    sentenceList.add(paragraph.substring(start, end).trim());
		    start = end;
		}
		//System.out.println(StringUtils.join(sentenceList, "  "));
		for(int i=0; i<sentenceList.size(); i++) {
			System.out.println(sentenceList.get(i));
		}
	}*/
}
