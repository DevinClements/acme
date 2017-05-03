import java.util.ArrayList;
import java.util.Collections;
import java.io.Serializable;

public class Timesheet implements Serializable {

	private static final long serialVersionUID = 1L;
	
	final long THIRTY_MINUTES = 1800000;
	final long FOUR_HOURS = 14400000;
	final long FOURTY_HOURS = 144000000;

	ArrayList<Ticket> tickets;

	private long timeRegular = 0;
	private long timeCallback = 0;
	private long timeJury = 0;
	private long timeVacation = 0;
	private long timeHoliday = 0;
	private long timeBereavement = 0;

	public Timesheet(ArrayList<Ticket> tickets) {
		this.tickets = new ArrayList<Ticket>(tickets);
		Collections.sort(this.tickets);
		calculateHours();
	}

	public long getRegular() {
		return timeRegular;
	}

	public long getCallback() {
		return timeCallback;
	}

	public long getJury() {
		return timeJury;
	}

	public long getVacation() {
		return timeVacation;
	}

	public long getHoliday() {
		return timeHoliday;
	}

	public long getBereavement() {
		return timeBereavement;
	}

	public double getHoursWorked() {		
		double totalTime = (double) getRegular() + getCallback() + getJury() + getVacation() + getHoliday() + getBereavement();
		return ((totalTime / (1000*60*60)));
	}

	public double getHoursPaid() {
		long timeAndHalfNotApplicable = getBereavement() + getJury() + getVacation();
		long timeAndHalfApplicable = getRegular() + getHoliday();
		long callback = getCallback();

		long timeAndHalf = 0;
		if(timeAndHalfApplicable > FOURTY_HOURS) {
			timeAndHalf += timeAndHalfApplicable - FOURTY_HOURS;
			timeAndHalfApplicable -= timeAndHalf;
		} else if(callback != 0) {
			timeAndHalfNotApplicable += Math.max(FOUR_HOURS, callback);
		}
		
		if(timeAndHalf != 0 && callback != 0) {
			long callbackExtra = Math.max(0, (FOUR_HOURS - callback));
			timeAndHalfNotApplicable += callbackExtra;
			timeAndHalf += callback;
		}

		timeAndHalf += (timeAndHalf*0.5);
		double totalPay = (double) timeAndHalfNotApplicable + timeAndHalfApplicable + timeAndHalf;
		
		return ((totalPay / (1000*60*60)));
	}

	private void calculateHours() {
		for(int i = 0; i < tickets.size()-1; i++) {
			Ticket ticketIn = tickets.get(i);
			Ticket ticketOut = tickets.get(i+1);
			long timeInBetween = ticketOut.datetime.getTime() - ticketIn.datetime.getTime();
			switch(ticketIn.hourType) {
				case Regular:
					timeRegular += timeInBetween;
					break;
				case Callback: 	
					timeCallback += timeInBetween;
					break;
				case JuryDuty:
					timeJury += timeInBetween;
					break;
				case Vacation: 	
					timeVacation += timeInBetween;
					break;
				case Holiday:
					timeHoliday += timeInBetween;
					break;
				case Bereavement: 	
					timeBereavement += timeInBetween;
					break;
			}
			i += 1;
		}
	}
}