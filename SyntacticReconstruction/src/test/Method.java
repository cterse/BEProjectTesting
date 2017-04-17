package test;

public class Method {
	private String methodName;
	private String ofClass;
	private String dobject;
	private String iObject;
	private String ofClassQuantity;
	private String onClassQuantity;
	private String dObjectQuantity;
	
	public Method(String name) {
		methodName = name;
		ofClass = iObject = dobject = null;
	}
	
	public Method(String name, String c1, String o, String c2) {
		methodName = name;
		ofClass = c1==null?null:c1.toLowerCase();
		dobject = o==null?null:o.toLowerCase();
		iObject = c2==null?null:c2.toLowerCase();
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
	
	public void setOfClass(String c1) {
		ofClass = c1.toLowerCase();
	}
	
	public void setIndirectObject(String c1) {
		iObject = c1.toLowerCase();
	}
	
	public void setDirectObject(String o) {
		dobject = o.toLowerCase();
	}
	
	public String toString() {
		String toReturn = methodName+"("+ofClass+", "+dobject+", "+iObject+")";
		return toReturn;
	}
}
