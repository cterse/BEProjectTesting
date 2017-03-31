package test;

import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class TreeManiTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sentence = "This is a test sentence.";
		
		Tree parse = Parser.getParseTree(sentence);
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		System.out.println(parse);
		System.out.println(tdl);
		
		System.out.println("Sentence from parse tree = "+TreeManipulation.getSentence(parse));
	}

}
