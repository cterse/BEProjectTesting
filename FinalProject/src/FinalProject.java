import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sentenceSplitting.*;
import syntaxReconstruction.*;
import MiscellaneousAPIs.*;
import elementExtraction.ElementExtractionAPI;

public class FinalProject {
	static Scanner t = new Scanner(System.in);
	static PrintWriter pw = null;
	
	public static void main(String[] args) {	
		
		//Step 1: Initiate the input file
		String pathToInputFile = null;
		if(args!=null && args.length != 0) {
			pathToInputFile = args[0];
		} else {
			pathToInputFile = "inputs/inputUniversity.txt";
		}
		//System.out.println(pathToInputFile);
		
		//Step 2: Sentence extraction
		List<String> sentences = null;
		try {
			sentences = FormSentences.getSentences(pathToInputFile);
			System.out.println("\nEXTRACTED SENTENCES:");
			MiscAPI.printListPerLine(sentences);
			pw = new PrintWriter("results/extractedSentences.txt");
			for(int i=0; i<sentences.size(); i++) {
				pw.println(sentences.get(i));
			}
			pw.close();
			System.out.print("\n\nPRESS ANY KEY TO CONTINUE...\n");
			t.next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Step 3: Syntactic Reconstruction
		List<String> simplifiedSentences = SyntaxRecon.reconstructSentences(sentences);
		System.out.println("\nSIMPLIFIED SENTENCES:");
		MiscAPI.printListPerLine(simplifiedSentences);
		System.out.print("\n\nPRESS ANY KEY TO CONTINUE...\n");
		try {
			pw = new PrintWriter("results/simplifiedSentences.txt");
			for(int i=0; i<simplifiedSentences.size(); i++) {
				pw.println(simplifiedSentences.get(i));
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.next();
		
		//Step 4: Entities Extraction
		List<Classes> classList = ElementExtractionAPI.extractEntities(simplifiedSentences);
		System.out.println(classList);
		
		//Refining classList
		//get empty classes
		List<Classes> emptyClassList = new ArrayList<Classes>();
		for(int i=0; i<classList.size(); i++) {
			if(classList.get(i).numberOfAttributes()==0 && classList.get(i).numberOfMethods()==0) {
				emptyClassList.add(classList.get(i));
			}
		}
		System.out.println("------------------------");
		System.out.println("Following are empty classes: ");
		for(int i=0; i<emptyClassList.size(); i++) {
			System.out.println(emptyClassList.get(i).getClassFullName()+", ");
		}
		System.out.println("Choose action: 1 - Keep All || 2 - Remove All");
		System.out.print("Choice: ");
		Scanner t = new Scanner(System.in);
		int choice = t.nextInt();
		
		if(choice==1) {
			for(int i=0; i<classList.size(); i++) {
				List<Attribute> tempAttrList = classList.get(i).getAttributesList();
				for(int j=0; j<emptyClassList.size(); j++) {
					for(int k=0; k<tempAttrList.size(); k++) {
						if(emptyClassList.get(j).getClassFullName().equalsIgnoreCase(Stemmer.getSingular(tempAttrList.get(k).getAttributeName()))) {
							classList.get(i).removeAttribute(tempAttrList.get(k));
						}
					}
				}
			}
		}
		else if(choice==2) {
			for(int i=0; i<classList.size(); ) {
				List<Method> tempMethodList = classList.get(i).getMethodsList();
				for(int j=0; j<tempMethodList.size(); ) {
					if(tempMethodList.get(j).getMethodType().equalsIgnoreCase("association")) {
						classList.get(i).removeMethod(tempMethodList.get(j));
					} else j++;
				}
				if(emptyClassList.contains(classList.get(i))) {
					classList.remove(i);
				} else i++;
			}
		}
		else {
			System.out.println("Wrong choice. Exiting.");
			System.exit(0);
		}
		System.out.println("\nOUTPUT:");
		System.out.println(classList);			//FINAL CLASSLIST. OUTPUT
		try {
			pw = new PrintWriter("results/classList.txt");
			for(int i=0; i<classList.size(); i++) {
				pw.println(classList.get(i));
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Relations in a separate ArrayList in (class1, class2, type, name) format.
		//Class is below
		List<MethodPlotFormat> methodPlotFormatList = new ArrayList<MethodPlotFormat>(); //final list
		for(int i=0; i<classList.size(); i++) {
			if(classList.get(i).numberOfMethods()!=0) {
				List<Method> tempMethodList = classList.get(i).getMethodsList();
				for(int j=0; j<tempMethodList.size(); j++) {
					if(tempMethodList.get(j).getMethodType().equalsIgnoreCase("method"))
						continue;
					String ofclass = tempMethodList.get(j).getOfClass();
					String onclass = tempMethodList.get(j).getIndirectObject();
					if(onclass == null)
						onclass = tempMethodList.get(j).getDirectObject();
					String type = tempMethodList.get(j).getMethodType();
					String name = tempMethodList.get(j).getMethodName();
					MethodPlotFormat x = new MethodPlotFormat(ofclass, onclass, type, name);
					methodPlotFormatList.add(x);
				}
			}
		}
		System.out.println(methodPlotFormatList);
	}	
}

class MethodPlotFormat {
	String ofClass;
	String onClass;
	String type;
	String name;
	
	public MethodPlotFormat(String a, String b, String c,  String d) {
		// TODO Auto-generated constructor stub
		ofClass = a;
		onClass = b;
		type = c;
		name = d;
	}
	
	public String toString() {
		String toReturn = ofClass+" "+onClass+" "+type+" "+name;
		return toReturn;
	}
}
