import java.io.Serializable;

public class Employee implements Serializable {
	String id;

	public Employee(String id) {
		this.id = id.toLowerCase();
	}
}