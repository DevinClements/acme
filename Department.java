import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

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

	public ArrayList<Ticket> getEmployeeTickets(int id) {
		ArrayList<Ticket> employeeTickets = ArrayList<Ticket>();
		for(ArrayList<Ticket> ticketsForDate : tickets.values()) {
			for(Ticket ticket : ticketsForDate) {
				if(ticket.employeeId == id) {
					employeeTickets.add(ticket);
				}
			}
		}
		return employeeTickets;
	}
}