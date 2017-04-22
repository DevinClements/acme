import java.util.HashMap;

public class Department {
	int code;
	DepartmentType type;
	HashMap<Int, Employee> employees;
	HashMap<Date, Ticket> tickets;

	public Department(int code, DepartmentType type, HashMap<Int, Employee> employees, HashMap<Date, Ticket> tickets) {
		this.code = code;
		this.type = type;
		this.employees = employees;
		this.tickets = tickets;
	}

	public Department(int code, DepartmentType type) {
		this(code, type, new HashMap<Int, Employee>(), new HashMap<Date, Ticket>());
	}
}