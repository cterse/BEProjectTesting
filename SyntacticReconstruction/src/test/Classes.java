package test;
/*

 * Classes class.
*/

import java.util.ArrayList;
import java.util.List;

public class Classes {
	private String cname = "";
	private String compound = "";
	
	private List<Method> methods = new ArrayList<Method>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	Classes(String cname) {
		this.cname = cname;
	}
	
	Classes(String compound, String cname) {
		this.compound = compound;
		this.cname = cname;
	}
	
	public void addMethod(String name) {
		methods.add(new Method(name, this, null));
	}
	
	public void addMethod(Method m) {
		methods.add(m);
	}
	
	public void addAttribute(String name) {
		attributes.add(new Attribute(name, this));
	}
	
	public void addAttribute(Attribute a) {
		attributes.add(a);
	}
	
	public String getClassName() {
		return cname;
	}
	
	public String getClassFullName() {
		if( compound == "" ) {
			return cname;
		} else return (compound+"_"+cname);
	}
	
	public int numberOfAttributes() {
		return attributes.size();
	}
	
	public int numberOfMethods() {
		return methods.size();
	}
	
	public List<Attribute> getAttributesList() {
		return attributes;
	}
	
	public List<Method> getMethodsList() {
		return methods;
	}
	
	public String toString() {
		if(compound == "")
			System.out.println("Class full name = "+cname);
		else System.out.println("Class full name = "+compound+"_"+cname);
		System.out.println("Methods = "+methods);
		System.out.println("Attributes = "+attributes);
		return "";
	}
	
	public static void main(String[] args) {
		Classes c = new Classes("testClass");
		c.addAttribute("testAttr");
		c.addMethod("testMethod");
		System.out.println(c);
	}
	
}
