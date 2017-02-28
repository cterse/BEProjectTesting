package test;

/*
Program to open a specified in Sublime Text
*/

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class OpenFile {
	static void open(String fileName) {
		ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Sublime Text 3\\sublime_text.exe", fileName);
		try {
			pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}