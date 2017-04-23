import java.util.ArrayList;
import java.util.Collections;

public class Timesheet {

	ArrayList<Ticket> tickets;

	public Timesheet(ArrayList<Ticket> tickets) {
		this.tickets = new ArrayList<Ticket>(tickets);
		Collections.sort(this.tickets);
	}

	public double getHoursWorked() {
		long regularTime = getRegularTime();
		long callbackTime = getCallbackTime();
		long holidayTime = getHolidayTime();
		
		double totalTime = (double) regularTime + callbackTime + holidayTime;

		return ((totalTime / (1000*60*60)));
	}

	private long getRegularTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.Regular);
		return getTotalTime(tickets);
	}

	private long getCallbackTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.Callback);
		return getTotalTime(tickets);
	}

	private long getHolidayTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.Holiday);
		return getTotalTime(tickets);
	}

	private long getJuryTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.JuryDuty);
		return getTotalTime(tickets);
	}

	private long getBereavementTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.Bereavement);
		return getTotalTime(tickets);
	}

	private long getVacationTime() {
		ArrayList<Ticket> tickets = filterTicketsByHour(HourType.Vacation);
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

	private ArrayList<Ticket> filterTicketsByHour(HourType type) {
		ArrayList<Ticket> filteredTickets = new ArrayList<Ticket>();
		for(int i = 0; i < this.tickets.size(); i++) {
			Ticket ticket = this.tickets.get(i);
			if(ticket.hourType == type) {
				filteredTickets.add(ticket);
			}
		}
		return filteredTickets;
	}
}