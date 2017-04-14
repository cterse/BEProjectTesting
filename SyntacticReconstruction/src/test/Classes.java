package test;
/*

 * Classes class.
*/

import java.util.ArrayList;
import java.util.List;

public class Classes implements Comparable<Classes> {
	private String cname = "";
	private String compound = "";
	
	private List<Method> methods = new ArrayList<Method>();
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	Classes(String cname) {
		this.cname = cname.toLowerCase();
	}
	
	Classes(String compound, String cname) {
		this.compound = compound.toLowerCase();
		this.cname = cname.toLowerCase();
	}
	
	public int compareTo(Classes c) {
		return this.getClassFullName().compareTo(c.getClassFullName());
	}
	
	public void addMethod(String name) {
		methods.add(new Method(name, this.getClassFullName(), "", ""));
	}
	
	public void addMethod(String methName, String onClassName) {
		methods.add(new Method(methName, this.getClassFullName(), "", onClassName));
	}
	
	public void addMethod(String methName, String ofClassName, String dobj, String onClassName) {
		methods.add(new Method(methName, this.getClassFullName(), dobj, onClassName));
	}
	
	public void addMethod(Method m) {
		methods.add(m);
	}
	
	public void addAttribute(String name) {
		attributes.add(new Attribute(name, this.getClassFullName()));
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
		Classes d = new Classes("testClass");
		System.out.println(d.compareTo(c));
	}
	
}
