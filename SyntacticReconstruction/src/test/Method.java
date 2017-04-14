package test;

public class Method {
	private String methodName;
	private String ofClass;
	private String dobject;
	private String onClass;
	
	public Method(String name) {
		methodName = name;
		ofClass = onClass = dobject = null;
	}
	
	public Method(String name, String c1, String o, String c2) {
		methodName = name;
		ofClass = c1==null?null:c1.toLowerCase();
		dobject = o==null?null:o.toLowerCase();
		onClass = c2==null?null:c2.toLowerCase();
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
		ofClass = c1.toLowerCase();
	}
	
	public void setOnClass(String c1) {
		onClass = c1.toLowerCase();
	}
	
	public void setDirectObject(String o) {
		dobject = o.toLowerCase();
	}
	
	public String toString() {
		String toReturn = methodName+"("+ofClass+", "+dobject+", "+onClass+")";
		return toReturn;
	}
}
