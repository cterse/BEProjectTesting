import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sentenceSplitting.*;
import syntaxReconstruction.*;
import MiscellaneousAPIs.*;

public class FinalProject {
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
			System.out.println(sentences);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Step 3: Syntactic Reconstruction
		List<String> simplifiedSentences = SyntaxRecon.reconstructSentences(sentences);
		System.out.println(simplifiedSentences);
		
		//Step 4: Entities Extraction
		
		
	}
}
