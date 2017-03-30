import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import sentenceSplitting.*;


public class FinalProject {
	public static void main(String[] args) {	
		//Get sentences from the input paragraph
		List<String> sentenceList = new ArrayList<String>();
		try {
			sentenceList = FormSentences.getSentences("input.txt");
			//Create a file containing separate sentences
			File file = new File("sentences.txt");
			PrintWriter printWriter = new PrintWriter(file);
			for(int i=0; i<sentenceList.size(); i++)
				printWriter.println(sentenceList.get(i));
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
