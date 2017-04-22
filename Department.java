import java.util.HashMap;
import java.util.ArrayList;

public class Department {
	int code;
	DepartmentType type;
	HashMap<Int, Employee> employees;
	HashMap<Date, ArrayList<Ticket>> tickets;

	public Department(int code, DepartmentType type, HashMap<Int, Employee> employees, HashMap<Date, ArrayList<Ticket>> tickets) {
		this.code = code;
		this.type = type;
		this.employees = employees;
		this.tickets = tickets;
	}

	public Department(int code, DepartmentType type) {
		this(code, type, new HashMap<Int, Employee>(), new HashMap<Date, ArrayList<Ticket>>());
	}

	public Employee getEmployee(int id) {
		return employees.get(id);
	}

	public ArrayList<Tickets> getEmployeeTickets() {
		
	}
}