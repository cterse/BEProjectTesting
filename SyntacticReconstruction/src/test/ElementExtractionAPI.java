package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
					classList.add(new Classes(compound.toLowerCase(), Stemmer.getSingular(className).toLowerCase()));
				} else {
					classList.add(new Classes(Stemmer.getSingular(compound).toLowerCase()));
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
					classList.add(new Classes(compound, Stemmer.getSingular(className)));
				} else {
					classList.add(new Classes(Stemmer.getSingular(compound)));
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
						attrList.add(new Attribute(attrName, Stemmer.getSingular(ofClassName)));
					} else {
						//the verb is an aux, ignore it
					}
				}
			}
		}
		
		return attrList;
	}

	static String getNmodOfNsubj(List<TypedDependency> tdl) {
		String nmod = null;
		String nsubjgov = "", nsubjdep = "";
		for(int i=0; i<tdl.size(); i++) {
			if(tdl.get(i).reln().toString().equalsIgnoreCase("nsubj")) {
				nsubjgov = tdl.get(i).gov().value();
				nsubjdep = tdl.get(i).dep().value();
			}
			if(tdl.get(i).reln().toString().contains("nmod")) {
				if(tdl.get(i).gov().value().equalsIgnoreCase(nsubjgov)) {
					nmod = tdl.get(i).dep().value();
					if(tdl.get(i-1).reln().toString().equalsIgnoreCase("compound")) {
						nmod = tdl.get(i-1).dep().value() + "_" + nmod;
					}
					break;
				}
			}
		}
		
		return nmod;
	}
	
	static List<Method> extractMethods(String sentence) {
		List<Method> methodsList = new ArrayList<Method>();
		
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
				if( !possessionVerbs.contains(node.value()) ) {
					boolean isMethod = true;
					Iterator<TypedDependency> tdlTempIt = tdl.iterator();
					while( tdlTempIt.hasNext() ) {
						TypedDependency tempDep = tdlTempIt.next();
						if( tempDep.dep().value().equalsIgnoreCase(node.value()) && (tempDep.reln().toString().equalsIgnoreCase("aux") || tempDep.reln().toString().equalsIgnoreCase("auxpass")) ) {
							//the present verb is an auxiliary hence ignore it
							isMethod = false;
							break;
						}
						if( tempDep.dep().value().equalsIgnoreCase(node.value()) && tempDep.reln().toString().equalsIgnoreCase("cop") ) {
							//the verb is a copula hence not a method
							//but determines a relationship!!!!
							isMethod = false;
							break;
						}
					}
					if( isMethod ) {
						//the verb is a method. Append it with its aux/auxpass(if present) and add to list
						tdlTempIt = tdl.iterator();
						String temp = "";
						while( tdlTempIt.hasNext() ) {
							TypedDependency tempDep = tdlTempIt.next();
							if( tempDep.gov().value().equalsIgnoreCase(node.value()) && (tempDep.reln().toString().equalsIgnoreCase("aux") || tempDep.reln().toString().equalsIgnoreCase("auxpass")) ) {
								temp = tempDep.dep().value() + "_" + node.value();
								break;
							}
							if( temp.equalsIgnoreCase("") ) {
								temp = node.value();
								break;
							}
						}
						String ofClass = getSubject(tdl);
						String object = getObject(tdl);
						String nmodOfSubject = getNmodOfNsubj(tdl);
						String onClass = (object==null)?((nmodOfSubject==null)?(null):nmodOfSubject):(object); 
						methodsList.add(new Method(temp, Stemmer.getSingular(ofClass), Stemmer.getSingular(onClass)));
					}
				}
			}
		}
		
		return methodsList;
	}
	
	static List<Classes> extractEntities(String sentence) {
		List<Classes> classList = extractClasses(sentence);
		List<Method> methodsList = extractMethods(sentence);
		List<Attribute> attributesList = extractAttributes(sentence);
		for(int i=0; i<methodsList.size(); ) {
			boolean removed = false;
			for(int j=0; j<classList.size(); j++) {
				if(methodsList.get(i).getOfClass().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.get(j).addMethod(methodsList.remove(i));
					removed = true;
					break;
				}
			}
			if(!removed)
				i++;
		}
		for(int i=0; i<attributesList.size(); i++) {
			boolean removed = false;
			for(int j=0; j<classList.size(); j++) {
				if(attributesList.get(i).getAttributeClass().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.get(j).addAttribute(attributesList.remove(i));
					removed = true;
					break;
				}
			}
			if(!removed)
				i++;
		}
		
		if(!methodsList.isEmpty())
			System.out.println("NEEDS TO BE EMPTY! ="+methodsList);
		if(!attributesList.isEmpty())
			System.out.println("NEEDS TO BE EMPTY! ="+attributesList);
		
		return classList;
	}
	
	static List<Classes> extractEntities(List<String> sentences) {
		List<Classes> classList = new ArrayList<Classes>();
		List<Method> methodsList = new ArrayList<Method>();
		List<Attribute> attributesList = new ArrayList<Attribute>();
		for(int i=0; i<sentences.size(); i++) {
			classList.addAll(extractClasses(sentences.get(i)));
			methodsList.addAll(extractMethods(sentences.get(i)));
			attributesList.addAll(extractAttributes(sentences.get(i)));
		}
		
		//remove duplicates from classList
		for(int i=0; i<classList.size(); i++) {
			for(int j=i+1; j<classList.size(); j++) {
				if(classList.get(i).getClassFullName().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.remove(j);
				}
			}
		}
		
		//add the methods and attributes to proper classes in classList
		for(int i=0; i<methodsList.size(); ) {
			boolean removed = false;
			for(int j=0; j<classList.size(); j++) {
				if(methodsList.get(i).getOfClass().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.get(j).addMethod(methodsList.remove(i));
					removed = true;
					break;
				} 
			}
			if(!removed)
				i++;
		}
		for(int i=0; i<attributesList.size(); ) {
			boolean removed = false;
			for(int j=0; j<classList.size(); j++) {
				if(attributesList.get(i).getAttributeClass().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.get(j).addAttribute(attributesList.remove(i));
					removed = true;
					break;
				}
			}
			if(!removed)
				i++;
		}
		if(!methodsList.isEmpty())
			System.out.println("NEEDS TO BE EMPTY! ="+methodsList);
		if(!attributesList.isEmpty())
			System.out.println("NEEDS TO BE EMPTY! ="+attributesList);
		
		return classList;
	}
	
	public static void main(String[] args) {
		String sentence = "Some research departments play with research heads.";
		System.out.println(extractClasses(sentence));
		System.out.println("--------------------");
		System.out.println(extractMethods(sentence));
		System.out.println("--------------------");
		System.out.println(extractAttributes(sentence));
		System.out.println("--------------------");
		System.out.println(extractEntities(sentence));
		
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		List<String> sentences = new ArrayList<String>();
		sentences.add("Some research departments play with research heads.");
		sentences.add("Some research departments have research heads.");
		System.out.println(extractEntities(sentences));
	}
}
