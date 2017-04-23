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

	public ArrayList<Ticket> getEmployeeTickets(String id, Date date) {
		ArrayList<Ticket> employeeTickets = new ArrayList<Ticket>();
		ArrayList<Ticket> ticketsForDate = tickets.get(Ticket.keyFromDate(date));
		
		if(ticketsForDate == null) {
			return employeeTickets;
		}
		
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
			return null;
		}
		ticketsForDay.add(ticket);
		return ticket;
	}

	public Ticket punch(String employeeId, HourType hourType) {
		return punch(employeeId, hourType, new Date());
	}

	public double getHoursWorked(String employeeId, Date date) {
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId, date);
		Collections.sort(employeeTickets);

		long regularTime = getRegularTime(employeeTickets);
		long callbackTime = getCallbackTime(employeeTickets);
		long holidayTime = getHolidayTime(employeeTickets);
		
		double totalTime = (double) regularTime + callbackTime + holidayTime;

		return ((totalTime / (1000*60*60)));
	}

	private long getRegularTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.Regular);
		return getTotalTime(tickets);
	}

	private long getCallbackTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.Callback);
		return getTotalTime(tickets);
	}

	private long getHolidayTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.Holiday);
		return getTotalTime(tickets);
	}

	private long getJuryTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.JuryDuty);
		return getTotalTime(tickets);
	}

	private long getBereavementTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.Bereavement);
		return getTotalTime(tickets);
	}

	private long getVacationTime(ArrayList<Ticket> source) {
		ArrayList<Ticket> tickets = filterTicketsByHour(source, HourType.Vacation);
		return getTotalTime(tickets);
	}

	private long getTotalTime(ArrayList<Ticket> tickets) {
		long total = 0;
		for(int i = 0; i < tickets.size()-1; i++) {
			Ticket ticketIn = tickets.get(i);
			Ticket ticketOut = tickets.get(i+1);
			long timeInBetween = ticketOut.datetime.getTime() - ticketIn.datetime.getTime();
			total += timeInBetween;
			i = i + 1;
		}
		return total;
	}

	private ArrayList<Ticket> filterTicketsByHour(ArrayList<Ticket> source, HourType type) {
		ArrayList<Ticket> filteredTickets = new ArrayList<Ticket>();
		for(int i = 0; i < source.size(); i++) {
			Ticket ticket = source.get(i);
			if(ticket.hourType == type) {
				filteredTickets.add(ticket);
			}
		}
		return filteredTickets;
	}

	public Ticket punch(String employeeId, HourType hourType, Date date) {
		Employee employee = getEmployee(employeeId);
		
		if(employee == null) {
			return null;
		}

		if(tickets.isEmpty()) {
			return clockIn(employee.id, hourType, date);
		}
		
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId, date);
		if(employeeTickets.isEmpty()) {
			return clockIn(employee.id, hourType, date);	
		}

		int lastIndex = employeeTickets.size() - 1;
		Ticket latestTicket = employeeTickets.get(lastIndex);
		if(latestTicket.ticketType == TicketType.ClockIn) {
			return clockOut(employee.id, hourType, date);
		}
		return clockIn(employee.id, hourType, date);
	}
}