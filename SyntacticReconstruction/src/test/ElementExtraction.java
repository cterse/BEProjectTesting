package test;
/*
 * Code to extract classes from the sentences generated after Syntactic Reconstruction phase.
 * 
 * Input File = TBD. Output file of SyntacticRecontruction.java class
 * Output File = TBD.  
 * 
 * For attribute extraction, a list of possession verbs is created.
 * Objects of sentences containing these verbs in non-auxiliary form are taken as attributes.
 */

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
	
	static List<Classes> classList = new ArrayList<Classes>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		//input file
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
		
		for(int i=0; i<sentences.size(); i++) {
			
			System.out.println("Analysing sentence "+(i+1));
			
			//Get the parse tree and dependencies
			Tree parse = Parser.getParseTree(sentences.get(i));
		    List<TypedDependency> tdl = Parser.getTypedDependencies(parse);
			
			Iterator<Tree> it = parse.iterator();
			
			List<String> potentialClasses = new ArrayList<String>();
			List<String> potentialMethods = new ArrayList<String>();
			List<String> potentialAttributes = new ArrayList<String>(); //!!!!!!!! check generics
			Tree node = null;
			
			int classesFoundInSentence = 0;
			while( it.hasNext() ) {
				node = it.next();
				
				//Check for nouns i.e. Classes
				if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
					String compound = it.next().value();
					node = it.next();
					if( node.value().equalsIgnoreCase("NN") || node.value().equalsIgnoreCase("NNS") ) {
						//This means there are two nouns back to back i.e compound nouns
						String className = it.next().value();
						potentialClasses.add(compound+"_"+className);
						//classFound = findClassInList(classList, className);
						classList.add(new Classes(compound, className));
						classesFoundInSentence++;
					} else {
						potentialClasses.add(compound);
						classList.add(new Classes(compound));
						classesFoundInSentence++;
					}
				}
				
				//Check for verbs
				if(isVerb(node)) {
					//System.out.println("\nChecking verbs");
					node = it.next();
					
					//Check for attributes
					if( possessionVerbs.contains(node.value()) ) {
						//verb is present in list of possession verbs
						//System.out.println("Possession verb = \""+node.value()+"\" is present.");
						
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
						if( !auxVerb ) {
							String attribute = getObject(tdl);
							potentialAttributes.add( attribute );
							classList.get(classList.size()-classesFoundInSentence).addAttribute(attribute);
						} else {
							//the verb is an aux, ignore it
						}
					} else {
						//the verb is not possession verb
						//check for aux/auxpass verbs and copulas
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
									potentialMethods.add(temp);
									classList.get(classList.size()-classesFoundInSentence).addMethod(temp, getObject(tdl));
									break;
								}
							}
							if( temp.equalsIgnoreCase("") ) {
								potentialMethods.add(node.value());
								classList.get(classList.size()-classesFoundInSentence).addMethod(node.value(), getObject(tdl));
							}
						}
					}
				}
			}
			
		}
		
		//Resolve duplicates in the classesList
		for(int j=0; j<classList.size(); j++) {
			for(int k=j+1; k<classList.size(); k++) {
				if( classList.get(j).getClassFullName().equalsIgnoreCase(classList.get(k).getClassFullName()) ) {
					List<Method> temp = classList.get(k).getMethodsList();
					for(int z=0; z<temp.size(); z++) {
						classList.get(j).addMethod(temp.get(z));
					}
					
					List<Attribute> temp2 = classList.get(k).getAttributesList();
					for(int z=0; z<temp2.size(); z++) {
						classList.get(j).addAttribute(temp2.get(z));
					}
					
					classList.remove(k);
				}
			}
		}
		
		/*
		System.out.println("\nPotential classes: ");
		System.out.println(potentialClasses);
		System.out.println("\nPotential methods: ");
		System.out.println(potentialMethods);
		System.out.println("\nPotential attributes: ");
		System.out.println(potentialAttributes);
		*/
		System.out.println("Classes List: ");
		System.out.println(classList);
		refineClasses(classList);
		System.out.println();
		System.out.println("---------------------------");
		System.out.println("Refined Class List: ");
		System.out.println(classList);
		
		System.out.println("---------------------------");
		System.out.println("---------------------------");
		System.out.println(ElementExtractionAPI.extractEntities(sentences));
		
		outputPW.close();
	}

	public static void refineClasses(List<Classes> classList) {
		// TODO Auto-generated method stub
		//remove those nouns from class list who have no attributes and methods.
		for(int i=0; i<classList.size(); ) {
			if(classList.get(i).numberOfAttributes() == 0 && classList.get(i).numberOfMethods() == 0) {
				classList.remove(i);
			} else {
				i++;
			}
		}
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

	public static boolean isVerb(Tree node) {
		if( node.value().equalsIgnoreCase("VB") || node.value().equalsIgnoreCase("VBD") || node.value().equalsIgnoreCase("VBG") || node.value().equalsIgnoreCase("VBN") || node.value().equalsIgnoreCase("VBP") || node.value().equalsIgnoreCase("VBZ") )
			return true;
		return false;
	}
	
	
}
