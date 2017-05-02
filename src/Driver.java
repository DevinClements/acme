import java.util.Calendar;
import java.io.IOException;
import java.util.Date;

public class Driver implements Receiver {
	static Department dept = new Department("123", DepartmentType.Production);
	
	public Driver() {
		
	}
	
	public static void main(String[] args) {
		String codeVincent = "123";
		String codeArvell = "321";
		String codeMalena = "231";
		
		try {
			Driver driver = new Driver();
			
			AcmeClient client = new AcmeClient("localhost", 5555, driver);
			client.connect();
			
			client.createDepartment("123", DepartmentType.Production);
			
			client.addEmployee(codeVincent, "Vincent Moore");
			client.addEmployee(codeArvell, "Arvell Webb");
			client.addEmployee(codeMalena, "Malena Bravo");
			
			
		} catch(IOException e) {
			System.out.println(e);
		}

	}

	public static Ticket punch(String id, HourType type, int day, int hour, int minute) {
		Date date = getDate(day, hour, minute);
		Ticket ticket = dept.punch(id, type, date);
		return ticket;
	}

	public static Date getDate(int day, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	@Override
	public void receive(Message message) {}
}