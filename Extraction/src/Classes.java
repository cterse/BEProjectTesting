import java.util.ArrayList;
import java.util.List;

public class Classes {
	private String cname = "";
	private String compound = "";
	
	private List<String> methods = new ArrayList<String>();
	private List<String> attributes = new ArrayList<String>();
	
	Classes(String cname) {
		this.cname = cname;
	}
	
	Classes(String compound, String cname) {
		this.compound = compound;
		this.cname = cname;
	}
	
	public void addMethod(String name) {
		methods.add(name);
	}
	
	public void addAttribute(String name) {
		attributes.add(name);
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
	
	public List<String> getAttributesList() {
		return attributes;
	}
	
	public List<String> getMethodsList() {
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
	
}
