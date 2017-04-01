package test;

/*
 * Parse Tree Manipulation API by Chinmay Terse.
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class TreeManipulation {
	
	public static List<Tree> searchNode(String key, Tree parse) {
		List<Tree> nodes = new ArrayList<Tree>();
		Iterator<Tree> it = parse.iterator();
		while( it.hasNext() ) {
			Tree node = it.next();
			if( node.value().equalsIgnoreCase(key) ) {
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public static Tree getNextSibling(Tree node, Tree root) {
		Tree nextSibling = null;
		List<Tree> children = node.ancestor(1, root).getChildrenAsList();	//contains children of parent of node
		int i = 0;
		for(i=0; i<children.size(); i++)
			if( children.get(i).equals(node) )
				break;
		i++;
		//System.out.println("index = "+i+" size = "+children.size());
		/*
		Iterator<Tree> it = root.iterator();
		while( it.hasNext() && i<children.size() ) {
			nextSibling = it.next();
			if( nextSibling.equals(children.get(i)) )
				return nextSibling;
		}
		*/
		if(i<children.size())
			return children.get(i);
		return null;
	}
	
	public static Tree getPreviousSibling(Tree node, Tree root) {
		Tree prevSibling = null;
		Iterator<Tree> it = root.iterator();
		List<Tree> children = node.ancestor(1, root).getChildrenAsList();
		int i = 0;
		for(; i< children.size(); i++)
			if( children.get(i).equals(node) )
				break;
		i--;
		while( it.hasNext() && i<children.size() ) {
			prevSibling = it.next();
			if( prevSibling.equals(children.get(i)) )
				return prevSibling;
		}
		return null;
	}
	
	public static String getSentence(Tree parse) {
		String sentence = null;
		if( parse != null ) {
			sentence = "";
			Iterator<Tree> it = parse.iterator();
			while( it.hasNext() ) {
				Tree node = it.next();
				if( node.isLeaf() )
					sentence = sentence +" "+ node.value();
			}
		}
		
		//after the above processing, the terminator comes after a space.
		//removing that space before the terminator
		char temp = sentence.charAt(sentence.length()-1);
		sentence = sentence.substring(0, sentence.length()-2) + temp;
		
		return sentence;
	}
	
	public static boolean checkVerbPresent(Tree parse) {
		// TODO Auto-generated method stub
		Iterator<Tree> it = parse.iterator();
		while(it.hasNext()) {
			Tree temp = it.next();
			if(temp.value().toString().equalsIgnoreCase("VB"))
				return true;
			if(temp.value().toString().equalsIgnoreCase("VBD"))
				return true;
			if(temp.value().toString().equalsIgnoreCase("VBG"))
				return true;
			if(temp.value().toString().equalsIgnoreCase("VBN"))
				return true;
			if(temp.value().toString().equalsIgnoreCase("VBP"))
				return true;
			if(temp.value().toString().equalsIgnoreCase("VBZ"))
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//For testing
		String sentence = "We had sums and writing and I went home.";
		
		Tree parse = Parser.getParseTree(sentence);
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		System.out.println(parse);
		//System.out.println(tdl);
		
		//System.out.println("Sentence from parse tree = "+TreeManipulation.getSentence(parse));
		//System.out.println("Number of ands = "+TreeManipulation.searchNode("and", parse).size());
		System.out.println(parse.getNodeNumber(3));
		System.out.println("next sib of node number 3 = "+TreeManipulation.getNextSibling(parse.getNodeNumber(3), parse).nodeNumber(parse));
		System.out.println(parse.getNodeNumber(14).equals(parse.getNodeNumber(19)));
	
	}

}
