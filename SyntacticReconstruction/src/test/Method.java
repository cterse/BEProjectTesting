package test;

public class Method {
	private String methodName;
	private Classes ofClass;
	private Classes onClass;
	
	public Method(String name) {
		methodName = name;
		ofClass = onClass = null;
	}
	
	public Method(String name, Classes c1, Classes c2) {
		methodName = name;
		ofClass = c1;
		onClass = c2;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public Classes getOfClass() {
		return ofClass;
	}
	
	public Classes getOnClass() {
		return onClass;
	}
	
	public void setOfClass(Classes c1) {
		ofClass = c1;
	}
	
	public void setOnClass(Classes c1) {
		onClass = c1;
	}
}
