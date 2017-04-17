package MiscellaneousAPIs;

import java.util.List;

import edu.stanford.nlp.trees.TypedDependency;

public class MiscAPI {
	
	public static String placeTerminator(String sentence) {
		//Here, the terminator is taken to be at the end after trimming the string
		//This method moves the terminator to the left.
		sentence = sentence.trim();
		char terminator = sentence.charAt(sentence.length()-1);
		int jump = 0;
		for(int i=sentence.length()-2; i>=0; i--) {
			if(sentence.charAt(i) == ' ')
				jump++;
			else break;
		}
		sentence = sentence.substring(0, sentence.length()-jump-1) + terminator;
		return sentence;
	}
	
	public static int getVoice(List<TypedDependency> tdl) {
		//Use the dependencies for checking the voice
		//0 = Passive, 1 = Active
		//used by removeAnd()
		boolean sentencePassive = false;
		for(int i=0; i<tdl.size(); i++) {
			String relation = tdl.get(i).reln().toString();
			if(relation.equalsIgnoreCase("auxpass") || relation.equalsIgnoreCase("nsubjpass")) {
				sentencePassive = true;
				break;
			}
		}
		if(sentencePassive)	
			return 0;
		return 1;
	}
	
	public static int getVoice(String sentence) {
		//Use the dependencies for checking the voice
		//0 = Passive, 1 = Active
		//used by removeAnd()
		List<TypedDependency> tdl = Parser.getTypedDependencies(Parser.getParseTree(sentence));
		boolean sentencePassive = false;
		for(int i=0; i<tdl.size(); i++) {
			String relation = tdl.get(i).reln().toString();
			if(relation.equalsIgnoreCase("auxpass") || relation.equalsIgnoreCase("nsubjpass")) {
				sentencePassive = true;
				break;
			}
		}
		if(sentencePassive)	
			return 0;
		return 1;
	}
	
	public static void main(String[] args) {
		String sentence = "This is test                  .      ";
		
		MiscAPI ms = new MiscAPI();
		System.out.println(ms.placeTerminator(sentence));
	}
}
