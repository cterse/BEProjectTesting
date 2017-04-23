package MiscellaneousAPIs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Method {
	private String methodName;
	private String ofClass;
	private String dobject;
	private String iObject;
	private String ofClassQuantity;
	private String onClassQuantity;
	private String dObjectQuantity;
	private String type;
	
	public Method(String name) {
		methodName = name;
		ofClass = iObject = dobject = type = null;
	}
	
	public Method(String name, String c1, String o, String c2) {
		methodName = name;
		ofClass = c1==null?null:c1.toLowerCase();
		dobject = o==null?null:o.toLowerCase();
		iObject = c2==null?null:c2.toLowerCase();
		type = null;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getOfClass() {
		return ofClass;
	}
	
	public String getIndirectObject() {
		return iObject;
	}
	
	public String getDirectObject() {
		return dobject;
	}
	
	public String getMethodType() {
		return type;
	}
	
	public void setOfClass(String c1) {
		ofClass = c1.toLowerCase();
	}
	
	public void setIndirectObject(String c1) {
		iObject = c1.toLowerCase();
	}
	
	public void setDirectObject(String o) {
		dobject = o.toLowerCase();
	}
	
	public void setMethodType(String type) {
		this.type = type;
	}
	
	public String findAndSetMethodType() {
		String foundType = null;
		Scanner possVerbsFileScanner = null;
		List<String> possVerbs = new ArrayList<String>();
		try {
			possVerbsFileScanner = new Scanner(new File("possessionVerbs.txt"));
			while(possVerbsFileScanner.hasNext()) {
				possVerbs.add(possVerbsFileScanner.next());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(possVerbs.contains(this.getMethodName())) {
			foundType = "association";
		}
		else if(this.getMethodName().equalsIgnoreCase("is") || this.getMethodName().equalsIgnoreCase("are") || this.getMethodName().equalsIgnoreCase("be")) {
			foundType = "generalization";
		}
		else {
			foundType = "method";
		}
		this.setMethodType(foundType);
		return foundType;
	}
	
	public String toString() {
		String toReturn = methodName+"("+ofClass+", "+dobject+", "+iObject+", "+type+")";
		return toReturn;
	}
}
