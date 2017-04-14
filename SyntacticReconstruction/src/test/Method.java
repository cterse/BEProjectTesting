package test;

public class Method {
	private String methodName;
	private String ofClass;
	private String dobject;
	private String onClass;
	
	public Method(String name) {
		methodName = name;
		ofClass = onClass = dobject = "";
	}
	
	public Method(String name, String c1, String o, String c2) {
		methodName = name;
		ofClass = c1;
		dobject = o;
		onClass = c2;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getOfClass() {
		return ofClass;
	}
	
	public String getOnClass() {
		return onClass;
	}
	
	public String getDirectObject() {
		return dobject;
	}
	
	public void setOfClass(String c1) {
		ofClass = c1;
	}
	
	public void setOnClass(String c1) {
		onClass = c1;
	}
	
	public void setDirectObject(String o) {
		dobject = o;
	}
	
	public String toString() {
		String toReturn = methodName+"("+ofClass+", "+dobject+", "+onClass+")";
		return toReturn;
	}
}
