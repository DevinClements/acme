import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;

public class Driver {
	public static void main(String[] args) {
		Department dept = new Department("123", DepartmentType.Production);
		
		// Set hours
		Calendar calStart = Calendar.getInstance();
		calStart.set(Calendar.DAY_OF_WEEK, 1);
		calStart.set(Calendar.HOUR_OF_DAY, 1);
		calStart.set(Calendar.MINUTE, 0);
		calStart.set(Calendar.SECOND, 0);
		calStart.set(Calendar.MILLISECOND, 0);

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.DAY_OF_WEEK, 2);
		calEnd.set(Calendar.HOUR_OF_DAY, 2);
		calEnd.set(Calendar.MINUTE, 30);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);

		Date dateStart = calStart.getTime();
		Date dateEnd = calEnd.getTime();
		
		dept.addEmployee("123");
		dept.punch("123", HourType.Regular, dateStart);
		dept.punch("123", HourType.Regular, dateEnd);
		
		// Check hours
		Calendar calStartCheck = Calendar.getInstance();
		calStartCheck.set(Calendar.DAY_OF_WEEK, 1);
		calStartCheck.set(Calendar.HOUR_OF_DAY, 1);
		calStartCheck.set(Calendar.MINUTE, 0);
		calStartCheck.set(Calendar.SECOND, 0);
		calStartCheck.set(Calendar.MILLISECOND, 0);

		Calendar calEndCheck = Calendar.getInstance();
		calEndCheck.set(Calendar.DAY_OF_WEEK, 4);
		calEndCheck.set(Calendar.HOUR_OF_DAY, 1);
		calEndCheck.set(Calendar.MINUTE, 0);
		calEndCheck.set(Calendar.SECOND, 0);
		calEndCheck.set(Calendar.MILLISECOND, 0);

		Date dateStartCheck = calStartCheck.getTime();
		Date dateEndCheck = calEndCheck.getTime();

		double hours = dept.getHours("123", dateStartCheck, dateEndCheck);
		System.out.println(hours);
	}
}