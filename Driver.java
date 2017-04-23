import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;

public class Driver {
	public static void main(String[] args) {
		Department dept = new Department("123", DepartmentType.Production);
		dept.addEmployee("123");
		
		// Set hours
		Date punchOne 	= getPunchCard(1,1,0);
		Date punchTwo 	= getPunchCard(1,6,0);
		Date punchThree = getPunchCard(1,13,0);
		Date punchFour 	= getPunchCard(1,18,0);
		Date punchFive 	= getPunchCard(2,1,0);
		Date punchSix 	= getPunchCard(2,6,30);
		
		dept.punch("123", HourType.Regular, punchOne);
		dept.punch("123", HourType.Regular, punchTwo);
		dept.punch("123", HourType.Regular, punchThree);
		dept.punch("123", HourType.Regular, punchFour);
		dept.punch("123", HourType.Regular, punchFive);
		dept.punch("123", HourType.Regular, punchSix);
		
		// Check hours
		Date dateToCheck = getPunchCard(2,0,0);
		Timesheet sheet = dept.getTimesheet("123", dateToCheck);
		System.out.println(sheet.getHoursWorked());
	}

	public static Date getPunchCard(int day, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
}