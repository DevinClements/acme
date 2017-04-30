import java.io.IOException;
import java.util.Date;

public class AcmeClient extends AbstractClient {

	final private String COMMAND_DEPARTMENT_LOGIN = "!department-login";
	final private String COMMAND_DEPARTMENT_CREATE = "!department-create";
	final private String COMMAND_EMPLOYEE_PUNCH = "!punch";
	final private String COMMAND_EMPLOYEE_TIMESHEET = "!timesheet";
	
	private Receiver receiver;

	public AcmeClient(String host, int port, Receiver receiver) throws IOException {
		super(host, port);
		this.receiver = receiver;
	}
	
	public void connect() throws IOException {
		this.openConnection();
		return;
	}
	
	public void disconnect() throws IOException {
		this.closeConnection();
		return;
	}
	
	public void login(String departmentCode) throws IOException {
		this.sendToServer(new Message(COMMAND_DEPARTMENT_LOGIN, departmentCode));
		return;
	}
	
	public void createDepartment(String code, DepartmentType type) throws IOException {
		Object[] objectsToSend = new Object[]{code, type};
		this.sendToServer(new Message(COMMAND_DEPARTMENT_CREATE, objectsToSend));
		return;
	}
	
	public void punch(String id, HourType type) throws IOException {
		Object[] objectsToSend = new Object[]{id, type, new Date()};
		this.sendToServer(new Message(COMMAND_EMPLOYEE_PUNCH, objectsToSend));
		return;
	}
	
	public void getTimesheet(String id, Date date) throws IOException {
		Object[] objectsToSend = new Object[]{id, new Date()};
		this.sendToServer(new Message(COMMAND_EMPLOYEE_TIMESHEET, objectsToSend));
		return;
	}
	
	public void handleMessageFromServer(Object msg) {
		if(msg instanceof Message) {
			Message message = (Message) msg;
			this.receiver.receive(message);
		}
	}
}