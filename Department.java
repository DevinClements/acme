import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;

public class Department {
	String code;
	DepartmentType type;
	HashMap<String, Employee> employees;
	HashMap<String, ArrayList<Ticket>> tickets;

	public Department(String code, DepartmentType type, HashMap<String, Employee> employees, HashMap<String, ArrayList<Ticket>> tickets) {
		this.code = code;
		this.type = type;
		this.employees = employees;
		this.tickets = tickets;
	}

	public Department(String code, DepartmentType type) {
		this(code, type, new HashMap<String, Employee>(), new HashMap<String, ArrayList<Ticket>>());
	}

	public Employee addEmployee(String id) {
		Employee employee = new Employee(id);
		employees.put(employee.id, employee);
		return employee;
	}

	public Employee getEmployee(String id) {
		return employees.get(id);
	}

	public ArrayList<Ticket> getEmployeeTickets(String id) {
		ArrayList<Ticket> employeeTickets = new ArrayList<Ticket>();
		for(ArrayList<Ticket> ticketsForDate : tickets.values()) {
			for(Ticket ticket : ticketsForDate) {
				if(ticket.employeeId.equals(id)) {
					employeeTickets.add(ticket);
				}
			}
		}
		return employeeTickets;
	}

	public ArrayList<Ticket> getEmployeeTickets(String id, Date date) {
		ArrayList<Ticket> employeeTickets = new ArrayList<Ticket>();
		ArrayList<Ticket> ticketsForDate = tickets.get(date);
		for(Ticket ticket : ticketsForDate) {
			if(ticket.employeeId.equals(id)) {
				employeeTickets.add(ticket);
			}
		}
		return employeeTickets;
	}

	private Ticket clockIn(String employeeId, HourType hourType, Date date) {
		Ticket ticket = new Ticket(date, hourType, TicketType.ClockIn, employeeId);
		ArrayList<Ticket>ticketsForDay = tickets.get(ticket.dateKey());
		if(ticketsForDay == null) {
			ticketsForDay = new ArrayList<Ticket>();
			tickets.put(ticket.dateKey(), ticketsForDay);
		}
		ticketsForDay.add(ticket);
		return ticket;
	}

	private Ticket clockOut(String employeeId, HourType hourType, Date date) {
		Ticket ticket = new Ticket(date, hourType, TicketType.ClockOut, employeeId);
		ArrayList<Ticket>ticketsForDay = tickets.get(ticket.dateKey());
		if(ticketsForDay == null) {
			ticketsForDay = new ArrayList<Ticket>();
			tickets.put(ticket.dateKey(), ticketsForDay);
		}
		ticketsForDay.add(ticket);
		ticketsForDay.add(ticket);
		return ticket;
	}

	public Ticket punch(String employeeId, HourType hourType, Date date) {
		Employee employee = getEmployee(employeeId);
		
		if(employee == null) {
			return null;
		}

		if(tickets.isEmpty()) {
			return clockIn(employee.id, hourType, date);
		}
		
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId);
		int lastIndex = employeeTickets.size() - 1;
		Ticket latestTicket = employeeTickets.get(lastIndex);
		if(latestTicket.ticketType == TicketType.ClockIn) {
			return clockOut(employee.id, hourType, date);
		}
		return clockIn(employee.id, hourType, date);
	}

	public Ticket punch(String employeeId, HourType hourType) {
		return punch(employeeId, hourType, new Date());
	}

	public double getHours(String employeeId, Date start, Date end) {
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId);
		ArrayList<Ticket> ticketsToCalculate = new ArrayList<Ticket>();
		for(Ticket ticket : employeeTickets) {
			if(ticket.datetime.compareTo(start) >= 0 && ticket.datetime.compareTo(end) <= 0) {
				ticketsToCalculate.add(ticket);
			}
		}
		
		Collections.sort(ticketsToCalculate);
		
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
			millisecondsWorked += ticketOut.datetime.getTime() - ticketIn.datetime.getTime();
			i = i + 2;
		}

		return (double) ((millisecondsWorked / (1000*60*60)) % 24);
	}
}