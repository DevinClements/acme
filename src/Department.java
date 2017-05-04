import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.Serializable;
import java.util.Iterator;

public class Department implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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

	public Employee getEmployee(String id) {
		return employees.get(id);
	}
	
	public Employee[] getAllEmployee() {
		return employees.values().toArray(new Employee[employees.size()]);
	}

	public Employee addEmployee(String id, String name) {
		Employee employee = new Employee(id, name);
		employees.put(employee.id, employee);
		return employee;
	}
	
	public Employee removeEmployee(String id) {
		Employee removedEmployee = employees.remove(id);
		for(String key: tickets.keySet()) {
			ArrayList<Ticket> ticketsForDate = tickets.get(key);
			Iterator<Ticket> iterator = ticketsForDate.iterator();
			while(iterator.hasNext()) {
				Ticket ticket = iterator.next();
				if(ticket.employeeId.equals(removedEmployee.id)) {
					iterator.remove();
				}
			}
		}
		return removedEmployee;
	}


	public Ticket punch(String employeeId, HourType hourType) {
		return punch(employeeId, hourType, new Date());
	}

	public void punch(String id, HourType hourType, int day, int hours, int minutes) {
		Calendar calStart = Calendar.getInstance();
		calStart.set(Calendar.DAY_OF_WEEK, day);
		calStart.set(Calendar.HOUR_OF_DAY, 1);
		calStart.set(Calendar.MINUTE, 0);
		calStart.set(Calendar.SECOND, 0);
		calStart.set(Calendar.MILLISECOND, 0);

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.DAY_OF_WEEK, day);
		calEnd.set(Calendar.HOUR_OF_DAY, hours+1);
		calEnd.set(Calendar.MINUTE, minutes);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);

		Date start = calStart.getTime();
		Date end = calEnd.getTime();

		punch(id, hourType, start);
		punch(id, hourType, end);
	}

	public Timesheet getTimesheet(String employeeId, Date date) {
		ArrayList<Ticket> employeeTickets = getEmployeeTickets(employeeId, date);
		Timesheet sheet = new Timesheet(employeeTickets);
		return sheet;
	}

	public Timesheet getTimesheet(String id, Date[] dates) {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		for(Date date : dates) {
			ArrayList<Ticket> ticketsForDate = getEmployeeTickets(id, date);
			tickets.addAll(ticketsForDate);
		}
		return new Timesheet(tickets);
	}
	
	public Timesheet getTimesheet(String id, Date from, Date to) {
		ArrayList<Date> dates = new ArrayList<Date>();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(to);
		cal.add(Calendar.DATE, 1);
		Date lastDay = cal.getTime();
		
		cal.setTime(from);
		
		while(!cal.getTime().equals(lastDay)) {
			dates.add(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}
		
		return getTimesheet(id, dates.toArray(new Date[dates.size()]));
		
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
	
	public void removeTicket(String id, Ticket toRemove) {
		this.tickets.get(Ticket.keyFromDate(toRemove.datetime)).remove(toRemove);
	}
	
	public void addTicket(String id, Ticket toAdd) {
		this.tickets.get(Ticket.keyFromDate(toAdd.datetime)).add(toAdd);
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
}