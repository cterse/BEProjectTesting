package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class TreeManipulation {
	
	public static List<Integer> searchNode(String searchNodeValue, Tree parse) {
		List<Integer> nodeNumbers = new ArrayList<Integer>();
		Iterator<Tree> it = parse.iterator();
		while( it.hasNext() ) {
			Tree node = it.next();
			if( node.value().equalsIgnoreCase(searchNodeValue) ) {
				nodeNumbers.add(node.nodeNumber(parse));
			}
		}
		return nodeNumbers;
	}
	
	public static Tree getNextSibling(Tree node, Tree root) {
		Tree nextSibling = null;
		Iterator<Tree> it = root.iterator();
		List<Tree> children = node.ancestor(1, root).getChildrenAsList();
		int i = 0;
		for(i=0; i<children.size(); i++)
			if( children.get(i).equals(node) )
				break;
		i++;
		//System.out.println("index = "+i+" size = "+children.size());
		while( it.hasNext() && i<children.size() ) {
			nextSibling = it.next();
			if( nextSibling.equals(children.get(i)) )
				return nextSibling;
		}
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
	
}