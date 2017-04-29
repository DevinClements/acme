import java.util.Calendar;
import java.util.Date;
import java.io.Serializable;

public class Ticket implements Comparable<Ticket>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Date datetime;
	HourType hourType;
	TicketType ticketType;
	String employeeId;

	public Ticket(Date datetime, HourType hourType, TicketType ticketType, String employeeId) {
		this.datetime = datetime;
		this.hourType = hourType;
		this.ticketType = ticketType;
		this.employeeId = employeeId;
	}

	public Ticket(HourType hourType, TicketType ticketType, String employeeId) {
		this(new Date(), hourType, ticketType, employeeId);
	}

	public static String keyFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return "" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_WEEK) + "-" + cal.get(Calendar.YEAR);
	}

	public int compareTo(Ticket ticket) {
		return this.datetime.compareTo(ticket.datetime);
	}

	public String dateKey() {
		return Ticket.keyFromDate(datetime);
	}
}