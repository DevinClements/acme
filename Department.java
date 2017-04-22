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

	public ArrayList<Ticket> getEmployeeTickets(int id, Date date) {
		ArrayList<Ticket> employeeTickets = ArrayList<Ticket>();
		ArrayList<Ticket> ticketsOnDate = tickets.get(date);
		for(Ticket ticket : ticketsForDate) {
			if(ticket.employeeId == id) {
				employeeTickets.add(ticket);
			}
		}
		return employeeTickets;
	}

	private Ticket clockIn(int employeeId, HourType hourType) {
		Ticket ticket = new Ticket(hourType, TicketType.ClockIn, employeeId);
		tickets.put(ticket.datetime, ticket);
		return ticket;
	}

	private Ticket clockOut(int employeeId, HourType hourType) {
		Ticket ticket = new Ticket(hourType, TicketType.ClockOut, employeeId);
		tickets.put(ticket.datetime, ticket);
		return ticket;
	}

	public Ticket punch(int employeeId, HourType hourType) {
		Employee employee = getEmployee(employeeId);
		if(employee == null) {
			return null;
		}
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId);
		int lastIndex = employeeTickets.size() - 1;
		Ticket latestTicket = employeeTickets.get(lastIndex);
		if(latestTicket.ticketType == TicketType.ClockIn) {
			return clockOut(employee.id, hourType);
		}
		return clockIn(employee.id, hourType);
	}

	public double getHours(int employeeId, Date start, Date end) {
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId);
		ArrayList<Ticket> ticketsToCalculate = ArrayList<Ticket>();
		for(Ticekt ticket : employeeTickets) {
			if(ticket.dateTime.compareTo(start) >= 0 && ticket.datetime.compareTo(end) <= 0) {
				ticketsToCalculate.add(ticket);
			}
		}
		
		Collection.sort(ticketsToCalculate);
		
		// check to make sure all clock ins have an associated clock out.
		// it is not possible to clock out without having a saved clock in
		// if there is an odd number of tickets, the last ticket must be a
		// clock in without an associated clock out, so we remove it
		if(ticketsToCalculate.size() % 2 != 0) {
			int lastIndex = ticketsToCalculate.size() - 1;
			tickets.remove(lastIndex);
		}
		
		long millisecondsWorked = 0;
		for(int i = 0; i < ticketsToCalculate.size() - 1; i++) {
			Ticket ticketIn = ticketsToCalculate.get(i);
			Ticket ticketOut = ticketsToCalculate.get(i+1);
			millisecondsWorked += ticketOut.getTime() - ticketIn.getTime();
			i = i + 2;
		}

		return (double) ((milliseconds / (1000*60*60)) % 24)
	}
}