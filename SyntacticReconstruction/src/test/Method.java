package test;

public class Method {
	private String methodName;
	private String ofClass;
	private String onClass;
	
	public Method(String name) {
		methodName = name;
		ofClass = onClass = "";
	}
	
	public Method(String name, String c1, String c2) {
		methodName = name;
		ofClass = c1;
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
	
	public void setOfClass(String c1) {
		ofClass = c1;
	}
	
	public void setOnClass(String c1) {
		onClass = c1;
	}
	
	public String toString() {
		String toReturn = methodName+"("+ofClass+", "+onClass+")";
		return toReturn;
	}
}
