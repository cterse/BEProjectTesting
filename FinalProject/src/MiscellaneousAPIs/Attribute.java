package MiscellaneousAPIs;

public class Attribute {
	private String attributeName;
	private String ofClass;
	
	Attribute(String name) {
		attributeName = name;
		ofClass = null;
	}
	
	public Attribute(String name, String c1) {
		// TODO Auto-generated constructor stub
		attributeName = name;
		ofClass = c1;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public String getAttributeClass() {
		return ofClass;
	}
	
	public void setAttributeClass(String c1) {
		ofClass = c1;
	}
	
	public String toString() {
		return attributeName+"("+ofClass+")";
	}
}
