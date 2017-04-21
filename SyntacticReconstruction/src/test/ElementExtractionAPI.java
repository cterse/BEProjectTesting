package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.stanford.nlp.ie.NumberNormalizer;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class ElementExtractionAPI {
	static List<String> possessionVerbs = new ArrayList<String>();
	
	public static boolean isVerbTag(Tree node) {
		if( node.value().equalsIgnoreCase("VB") || node.value().equalsIgnoreCase("VBD") || node.value().equalsIgnoreCase("VBG") || node.value().equalsIgnoreCase("VBN") || node.value().equalsIgnoreCase("VBP") || node.value().equalsIgnoreCase("VBZ") )
			return true;
		return false;
	}
	
	public static boolean isVerbTag(String tag) {
		if(tag.equalsIgnoreCase("VB") || tag.equalsIgnoreCase("VBD") || tag.equalsIgnoreCase("VBG") || tag.equalsIgnoreCase("VBN") || tag.equalsIgnoreCase("VBP") || tag.equalsIgnoreCase("VBZ") )
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
		
		//remove duplicates from classList
		for(int i=0; i<classList.size(); i++) {
			for(int j=i+1; j<classList.size(); ) {
				if(classList.get(i).getClassFullName().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.remove(j);
				}
				else j++;
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
		
		//remove duplicates from classList
		for(int i=0; i<classList.size(); i++) {
			for(int j=i+1; j<classList.size(); ) {
				if(classList.get(i).getClassFullName().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.remove(j);
				}
				else j++;
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
						attrList.add(new Attribute(attrName, Stemmer.getSingular(ofClassName).toLowerCase()));
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
							System.out.println("COPULA FOUND: "+node.value());
							
							String methodName = node.value();
							//using tdl, the copula relation's dep is the copula verb, gov is the word to which the cop is linking the nsubj to.
							//if gov of cop relation is a noun, store it as onClass. Check for compound
							for(int i=0; i<tdl.size(); i++) {
								if(tdl.get(i).reln().toString().equalsIgnoreCase("cop")) {
									String[] copGov = tdl.get(i).gov().toString().split("/");
									if(copGov[1].equalsIgnoreCase("NN") || copGov[1].equalsIgnoreCase("NNS")) {
										//the copula links to a noun. Add it(with its compound) as onClass
										String onClass = copGov[0].toLowerCase();
										for(int j=0; j<tdl.size(); j++) {
											if(tdl.get(j).reln().toString().equalsIgnoreCase("compound") && tdl.get(j).gov().value().equalsIgnoreCase(copGov[0])) {
												onClass = tdl.get(j).dep().value() + "_" + copGov[0].toLowerCase();
												break;
											}
										}
										onClass = Stemmer.getSingular(onClass);
										String ofClass = Stemmer.getSingular(getSubject(tdl)).toLowerCase();
										methodsList.add(new Method(methodName, ofClass, "", onClass));
										break;
									}
								}
							}
							
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
						String onClass = (nmodOfSubject==null)?((object==null)?(null):object.toLowerCase()):(nmodOfSubject.toLowerCase()); 
						methodsList.add(new Method(temp, Stemmer.getSingular(ofClass), "", Stemmer.getSingular(onClass)));
					}
				}
			}
		}
		
		return methodsList;
	}
	
	static List<Method> extractMethods(Tree parse) {
		List<Method> methodsList = new ArrayList<Method>();
		List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
		//Parser.printDependencyList(tdl);
		
		for(int i=0; i<tdl.size(); i++) {
			IndexedWord ofClass = null, iObject = null, dObject = null, methodName = null;
			
			if(tdl.get(i).reln().toString().equalsIgnoreCase("nsubj") || tdl.get(i).reln().toString().equalsIgnoreCase("nsubjpass")) {
				ofClass = tdl.get(i).dep();
				if(!isVerbTag(tdl.get(i).gov().tag())) {
					//a copula is linking the nsubj not a verb
					//System.out.println("copula found");
					for(int j=0; j<tdl.size(); j++) {
						if(tdl.get(j).reln().toString().equalsIgnoreCase("cop")) {
							if(tdl.get(j).gov().tag().equalsIgnoreCase("NN") || tdl.get(j).gov().tag().equalsIgnoreCase("NNS")) {
								dObject = tdl.get(j).gov();
								methodName = tdl.get(j).dep();
							}
						}
					}
				}
				else {
					methodName = tdl.get(i).gov();
					
					//if there is a neg relation for the current methodName, it means there is no such method.
					//hence, if for a neg relation gov = methodName, then continue.
					boolean negativeMethod = false;
					for(int j=0; j<tdl.size(); j++) {
						if(tdl.get(j).reln().toString().equalsIgnoreCase("neg")) {
							if(tdl.get(j).gov().equals(methodName)) {
								negativeMethod = true; break;
							}
						}
					}
					if(negativeMethod)
						continue;
					
					for(int j=0; j<tdl.size(); j++) {
						if(tdl.get(j).gov().equals(methodName)) {
							if(tdl.get(j).reln().toString().equalsIgnoreCase("dobj")) {
								dObject = tdl.get(j).dep();
								continue;
							}
							if(tdl.get(j).reln().toString().equalsIgnoreCase("iobj")) {
								iObject = tdl.get(j).dep();
								continue;
							}
						}
					}
					if(iObject==null) {
						//Process nmod relation
						//NOT SURE ABOUT THIS CODE!!!
						for(int j=0; j<tdl.size(); j++) {
							if(tdl.get(j).reln().toString().contains("nmod")) {
								String[] nmodReln = tdl.get(j).reln().toString().split(":");
								if(nmodReln[1].equalsIgnoreCase("to") || nmodReln[1].equalsIgnoreCase("for")) {
									if(tdl.get(j).gov().equals(methodName)) {
										iObject = tdl.get(j).dep();
									}
								}
							}
						}
					}
					if(dObject==null) {
						//process nmod.
						//now, the nmod relation should have the methodname as gov, and not have to or for as prepositions
						for(int j=0; j<tdl.size(); j++) {
							if(tdl.get(j).reln().toString().contains("nmod")) {
								String[] nmodReln = tdl.get(j).reln().toString().split(":");
								if(!(nmodReln[1].equalsIgnoreCase("to") || nmodReln[1].equalsIgnoreCase("for"))) {
									if(tdl.get(j).gov().equals(methodName)) {
										dObject = tdl.get(j).dep();
									}
								}
							}
						}
					}
				}
				
				String toReturnMethodName = methodName==null?null:methodName.value();
				String toReturnOfClass = ofClass==null?null:ofClass.value();
				String toReturnIObject = iObject==null?null:iObject.value();
				String toReturnDObject = dObject==null?null:dObject.value();
				//System.out.println("methodname = "+methodName);
				//System.out.println("dobject = "+dObject);
				//System.out.println("iobject = "+iObject);
				//System.out.println("ofclass = "+ofClass);
				
				//get compounds and aux/auxpass of the above entitites, if any
				for(int j=0; j<tdl.size(); j++) {
					if(tdl.get(j).reln().toString().contains("compound")) {
						if(tdl.get(j).gov().equals(ofClass)) {
							toReturnOfClass = tdl.get(j).dep().value() + "_" + toReturnOfClass;
							continue;
						}
						if(tdl.get(j).gov().equals(iObject)) {
							toReturnIObject = tdl.get(j).dep().value() + "_" + toReturnIObject;
							continue;
						}
						if(tdl.get(j).gov().equals(methodName)) {
							toReturnMethodName = toReturnMethodName+ "_" + tdl.get(j).dep().value();
							continue;
						}
						if(tdl.get(j).gov().equals(dObject)) {
							toReturnDObject = tdl.get(j).dep().value() + "_" + toReturnDObject;
							continue;
						}
					}
					if(tdl.get(j).reln().toString().equalsIgnoreCase("case")) {
						if(dObject!=null && tdl.get(j).gov().equals(dObject)) {
							toReturnMethodName = toReturnMethodName + "_" + tdl.get(j).dep().value();
							continue;
						}
					}
					if(tdl.get(j).reln().toString().equalsIgnoreCase("aux") || tdl.get(j).reln().toString().equalsIgnoreCase("auxpass")) {
						if(tdl.get(j).gov().equals(methodName)) {
							toReturnMethodName = tdl.get(j).dep().value() + "_" + toReturnMethodName;
							continue;
						}
					}
					/*
					if(tdl.get(j).reln().toString().contains("nmod")) {
						if(iObject!=null && iObject.equals(tdl.get(j).dep()) && tdl.get(j).gov().equals(methodName)) {
							toReturnMethodName = toReturnMethodName + "_" + tdl.get(j).reln().toString().split(":")[1];
						}
						if(dObject!=null && dObject.equals(tdl.get(j).dep()) && tdl.get(j).gov().equals(methodName)) {
							toReturnMethodName = toReturnMethodName + "_" + tdl.get(j).reln().toString().split(":")[1];
						}
					}
					*/
				}
				
				Method newMethod = new Method(toReturnMethodName, Stemmer.getSingular(toReturnOfClass), Stemmer.getSingular(toReturnDObject), Stemmer.getSingular(toReturnIObject));
				methodsList.add(newMethod);
			}
		}
		
		return methodsList;
	}
	
	static List<Classes> extractEntities(String sentence) {
		List<Classes> classList = extractClasses(sentence);
		List<Method> methodsList = extractMethods(Parser.getParseTree(sentence));
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
			//System.out.println("ANALYSING SENTENCE "+(i+1));
			classList.addAll(extractClasses(sentences.get(i)));
			methodsList.addAll(extractMethods(Parser.getParseTree(sentences.get(i))));
			attributesList.addAll(extractAttributes(sentences.get(i)));
		}
		
		//remove duplicates from classList
		for(int i=0; i<classList.size(); i++) {
			for(int j=i+1; j<classList.size(); j++) {
				if(classList.get(i).getClassFullName().equalsIgnoreCase(classList.get(j).getClassFullName())) {
					classList.remove(j);
					j--;
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
	
	static String extractMulitplicity(IndexedWord entity, List<TypedDependency> tdl) {
		String multi = null;
		for(int i=0; i<tdl.size(); i++) {
			if(tdl.get(i).reln().toString().equalsIgnoreCase("nummod")) {
				if(tdl.get(i).gov().equals(entity)) {
					multi = NumberNormalizer.wordToNumber(tdl.get(i).dep().value()).toString();
				}
			}
			
			if(tdl.get(i).reln().toString().equalsIgnoreCase("det")) {
				if(tdl.get(i).gov().equals(entity)) {
					String relnDep = tdl.get(i).dep().value();
					if(relnDep.equalsIgnoreCase("some")) {
						multi = "0...*";
					}
				}
			}
		}
		
		return multi;
	}
	
	public static void main(String[] args) {
		String sentence = "A coach can coach multiple teams.";
		List<String> sentences = new ArrayList<String>();
		Parser.printDependencyList(Parser.getTypedDependencies(Parser.getParseTree(sentence)));
		System.out.println("--------------------");
		
		//System.out.println(extractClasses(sentence));
		//System.out.println("--------------------");
		
		//System.out.println(extractAttributes(sentence));
		//System.out.println("--------------------");
		
		File inputFile = null;
		if(args.length!=0)
			inputFile = new File(args[0]);
		else inputFile = new File("andRemovedSentences2.txt");
		Scanner inputFileScanner = null;
		try {
			inputFileScanner = new Scanner(inputFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sentences = new ArrayList<String>();
		while( inputFileScanner.hasNextLine() ) {
			sentences.add(inputFileScanner.nextLine());
		}
		/*
		for(int i=0; i<sentences.size(); i++) {
			System.out.println(sentences.get(i));
			Parser.printDependencyList(Parser.getTypedDependencies(Parser.getParseTree(sentences.get(i))));
			System.out.println(extractMethods(Parser.getParseTree(sentences.get(i))));
			System.out.println("---------------------------------------");
		}
		*/
		System.out.println(extractEntities(sentences));
		System.out.println("--------------------");
		
		/*
		System.out.println(extractEntities(sentence));
		
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		sentences.add("Some research departments play with research heads.");
		sentences.add("Some research departments have research heads.");
		System.out.println(extractEntities(sentences));
		
		
		
		System.out.println(extractEntities(sentences));
		*/
	}
}
