public class Ticket implements Comparable {
	private Date datetime;
	private HourType hourType;
	private TicketType ticketType;
	private int employeeId;

	public Ticket(Date datetime, HourType hourType, TicketType ticketType, int employeeId) {
		this.datetime = datetime;
		this.hourType = hourType;
		this.ticketType = ticketType;
		this.employeeId = employeeId;
	}

	public int compareTo(Ticket ticket) {
		return this.datetime.compareTo(ticket.datetime);
	}
}