import java.io.Serializable;

public class Employee implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;

	public Employee(String id) {
		this.id = id.toLowerCase();
	}
}