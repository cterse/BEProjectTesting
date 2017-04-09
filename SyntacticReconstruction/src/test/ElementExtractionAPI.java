package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class ElementExtractionAPI {
	static List<String> possessionVerbs = new ArrayList<String>();
	
	public static boolean isVerbTag(Tree node) {
		if( node.value().equalsIgnoreCase("VB") || node.value().equalsIgnoreCase("VBD") || node.value().equalsIgnoreCase("VBG") || node.value().equalsIgnoreCase("VBN") || node.value().equalsIgnoreCase("VBP") || node.value().equalsIgnoreCase("VBZ") )
			return true;
		return false;
	}
	
	static void fillPossessionVerbsList() {
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
	}
	
	static List<Classes> extractClasses(String sentence) {
		List<Classes> classList = new ArrayList<Classes>();
		Tree parse = Parser.getParseTree(sentence);
		
		Iterator<Tree> it = parse.iterator();
		while( it.hasNext() ) {
			Tree node = it.next();
			
			//Check for nouns i.e. Classes
			if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
				String compound = it.next().value();
				node = it.next();
				if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
					//This means there are two nouns back to back i.e compound nouns
					String className = it.next().value();
					classList.add(new Classes(compound, className));
				} else {
					classList.add(new Classes(compound));
				}
			}
		}
		
		return classList;
	}
	
	static List<Classes> extractClasses(Tree parse) {
		List<Classes> classList = new ArrayList<Classes>();
		
		Iterator<Tree> it = parse.iterator();
		while( it.hasNext() ) {
			Tree node = it.next();
			
			//Check for nouns i.e. Classes
			if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
				String compound = it.next().value();
				node = it.next();
				if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
					//This means there are two nouns back to back i.e compound nouns
					String className = it.next().value();
					classList.add(new Classes(compound, className));
				} else {
					classList.add(new Classes(compound));
				}
			}
		}
		
		return classList;
	}
	
	public static String getSubject(List<TypedDependency> tdl) {
		Iterator<TypedDependency> tdlIt = tdl.iterator();
		while( tdlIt.hasNext() ) {
			TypedDependency temp1 = tdlIt.next();
			if( temp1.reln().toString().equalsIgnoreCase("nsubj") ) {
				Iterator<TypedDependency> it2 = tdl.iterator();
				while( it2.hasNext() ) {
					TypedDependency temp2 = it2.next();
					if( temp2.gov().value().equalsIgnoreCase(temp1.dep().value()) && temp2.reln().toString().equalsIgnoreCase("compound") ) {
						return temp2.dep().value()+"_"+temp1.dep().value();
					}
				}
				return temp1.dep().value();
			}
		}
		return null;
	}
	
	public static String getObject(List<TypedDependency> tdl) {
		// TODO Auto-generated method stub
		Iterator<TypedDependency> tdlIt = tdl.iterator();
		while( tdlIt.hasNext() ) {
			TypedDependency temp1 = tdlIt.next();
			if( temp1.reln().toString().equalsIgnoreCase("dobj") ) {
				Iterator<TypedDependency> it2 = tdl.iterator();
				while( it2.hasNext() ) {
					TypedDependency temp2 = it2.next();
					if( temp2.gov().value().equalsIgnoreCase(temp1.dep().value()) && temp2.reln().toString().equalsIgnoreCase("compound") ) {
						return temp2.dep().value()+"_"+temp1.dep().value();
					}
				}
				return temp1.dep().value();
			}
		}
		return null;
	}
	
	static List<Attribute> extractAttributes(String sentence) {
		List<Attribute> attrList = new ArrayList<Attribute>();
		
		if(possessionVerbs.isEmpty()) {
			fillPossessionVerbsList();
		}
		Tree parse = Parser.getParseTree(sentence);
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		
		Iterator<Tree> it = parse.iterator();
		while(it.hasNext()) {
			Tree node = it.next();
			if(isVerbTag(node)) {
				node = it.next();
				if( possessionVerbs.contains(node.value()) ) {
					//now check if its an auxiliary verb
					boolean auxVerb = false;
					Iterator<TypedDependency> tdlTempIt = tdl.iterator();
					while( tdlTempIt.hasNext() ) {
						TypedDependency tempDep = tdlTempIt.next();
						if( tempDep.dep().value().equalsIgnoreCase(node.value()) && tempDep.reln().toString().equalsIgnoreCase("aux") ) {
							//the possessive verb is an aux
							//System.out.println("AUX FOUND AT = reln = "+tempDep.reln().toString()+" dep = "+tempDep.gov().value());
							auxVerb = true; 
							System.out.println("Found poss verb is an aux, hence object cannot be attribute");
							break;
						}
					}
					
					//if possessive verb is not aux, it is related to an attribute
					//here the dobj is considered as attribute
					//System.out.println(auxVerb);
					if(!auxVerb) {
						String attrName = getObject(tdl);
						String ofClassName = getSubject(tdl);
						attrList.add(new Attribute(attrName, ofClassName));
					} else {
						//the verb is an aux, ignore it
					}
				}
			}
		}
		
		return attrList;
	}
	
	public static void main(String[] args) {
		String sentence = "A research departments hold a course.";
		System.out.println(extractClasses(sentence));
		
	}
}
