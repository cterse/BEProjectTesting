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
	
	public static void main(String[] args) {	
		
		//Step 1: Initiate the input file
		String pathToInputFile = null;
		if(args.length != 0) {
			pathToInputFile = args[0];
		} else {
			pathToInputFile = "input.txt";
		}
		//System.out.println(pathToInputFile);
		
		//Step 2: Sentence extraction
		List<String> sentences = null;
		try {
			sentences = FormSentences.getSentences(pathToInputFile);
			System.out.println("\nEXTRACTED SENTENCES:");
			MiscAPI.printListPerLine(sentences);
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
		System.out.println(classList);
	}
}
