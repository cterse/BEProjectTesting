package test;

public class Attribute {
	private String attributeName;
	private Classes ofClass;
	
	Attribute(String name) {
		attributeName = name;
		ofClass = null;
	}
	
	public Attribute(String name, Classes c1) {
		// TODO Auto-generated constructor stub
		attributeName = name;
		ofClass = c1;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public Classes getAttributeClass() {
		return ofClass;
	}
	
	public void setAttributeClass(Classes c1) {
		ofClass = c1;
	}
	
	public String toString() {
		return attributeName;
	}
}
