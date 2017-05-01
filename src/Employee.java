import java.io.Serializable;

public class Employee implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String name;

	public Employee(String id, String name) {
		this.id = id.toLowerCase();
		this.name = name;
	}
	
}