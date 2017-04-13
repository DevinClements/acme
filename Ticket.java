public class Ticket {
	private Date datetime;
	private HourType hourType;
	private TicketType ticketType;

	public Ticket(Date datetime, HourType hourType, TicketType ticketType) {
		this.datetime = datetime;
		this.hourType = hourType;
		this.ticketType = ticketType;
	}
}