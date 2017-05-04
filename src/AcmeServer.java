import java.io.IOException;
import java.util.HashMap;
import java.util.Date;

public class AcmeServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	final private String CLIENT_KEY_DEPARTMENT_CODE = "department_code";

	HashMap<String, Department> departmentStore = new HashMap<String, Department>();

	public AcmeServer(int port) {
		super(port);
		
		// Sample department
		Department driverDept = new Department("000", DepartmentType.Production);
		
		String[] employeeCodes = new String[]{"001","002", "003", "004", "005"};
		String[] employeeNames = new String[]{"Vincent Moore","Devon Clements", "Micah Downs", "James English", "John Anthony"};
		
		for(int i = 0; i < employeeCodes.length; i++) {
			String code = employeeCodes[i];
			String name = employeeNames[i];
			driverDept.addEmployee(code, name);
			driverDept.punch(code, HourType.Vacation, 1, 8, 0);
			driverDept.punch(code, HourType.Regular, 2, 12, 0);
			driverDept.punch(code, HourType.Regular, 3, 10, 0);
			driverDept.punch(code, HourType.Regular, 4, 10, 0);
			driverDept.punch(code, HourType.Regular, 5, 10, 0);
			driverDept.punch(code, HourType.Callback, 6, 2, 0);
		}
		
		departmentStore.put("000", driverDept);
	}

	public void handleDepartmentLogin(Message message, ConnectionToClient client) throws IOException {
		String code = (String) message.objects[0];
		if(departmentStore.containsKey(code)){
			client.setInfo(CLIENT_KEY_DEPARTMENT_CODE, code);
			client.sendToClient(new Message("!department-login", "Successfully logged into department."));
			return;
		}
		client.sendToClient(new Message("!error", "Could not log into deparment."));
		return;
	}

	public void handleCreateDepartment(Message message, ConnectionToClient client) throws IOException {
		String code = (String) message.objects[0];
		DepartmentType type = (DepartmentType) message.objects[1];
		if(!departmentStore.containsKey(code)) {
			departmentStore.put(code, new Department(code, type));
			client.sendToClient(new Message("!department-create", "Successfully created department."));
			return;
		}
		client.sendToClient(new Message("!error", "Department already exists."));
		return;
	}

	public void handlePunch(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		if(!departmentStore.containsKey(departmentCode)) {
			client.sendToClient(new Message("!error", null));
			return;
		}
		Department department = departmentStore.get(departmentCode);
		String employeeId = (String) message.objects[0];
		HourType hourType = (HourType) message.objects[1];
		department.punch(employeeId, hourType);
		client.sendToClient(new Message("!punch", ""));
		return;
	}

	public void handleTimesheetForDate(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		if(!departmentStore.containsKey(departmentCode)) {
			client.sendToClient(new Message("!error", null));
			return;
		}
		Department department = departmentStore.get(departmentCode);
		String employeeId = (String) message.objects[0];
		Date[] dates = (Date[]) message.objects[1];
		Timesheet sheet = department.getTimesheet(employeeId, dates);
		client.sendToClient(new Message("!timesheet", sheet));
		return;
	}
	
	public void handleTimesheetForRange(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		if(!departmentStore.containsKey(departmentCode)) {
			client.sendToClient(new Message("!error", null));
			return;
		}
		Department department = departmentStore.get(departmentCode);
		String employeeId = (String) message.objects[0];
		Date from = (Date) message.objects[1];
		Date to = (Date) message.objects[2];
		Timesheet sheet = department.getTimesheet(employeeId, from, to);
		client.sendToClient(new Message("!timesheet-range", sheet));
		return;
	}
	
	public void handleEmployeeList(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		Department department = departmentStore.get(departmentCode);
		Employee[] emps = department.getAllEmployee();
		client.sendToClient(new Message("!employee-list", emps));
	}
	
	public void handleEmployeeAdd(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		Department department = departmentStore.get(departmentCode);
		String id = (String) message.objects[0];
		String name = (String) message.objects[1];
		department.addEmployee(id, name);
		client.sendToClient(new Message("!employee-add", ""));
	}
	
	public void handleEmployeeRemove(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		Department department = departmentStore.get(departmentCode);
		String id = (String) message.objects[0];
		department.removeEmployee(id);
		client.sendToClient(new Message("!employee-remove", ""));
	}
	
	public void handleTicketReplace(Message message, ConnectionToClient client) throws IOException {
		System.out.println("we are here");
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		Department department = departmentStore.get(departmentCode);
		String id = (String) message.objects[0];
		Ticket[] toRemove = (Ticket[]) message.objects[1];
		Ticket[] toAdd = (Ticket[]) message.objects[2];
		if(toRemove != null) {
			department.removeTicket(id, toRemove[0]);
			department.removeTicket(id, toRemove[1]);
		}
		if(toAdd != null) {
			department.addTicket(id, toAdd[0]);
			department.addTicket(id, toAdd[1]);
		}
		client.sendToClient(new Message("!ticket-replace", ""));
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if(msg instanceof Message) {
			Message message = (Message) msg;
			try {		
				switch(message.command) {
					case "!department-login":
						handleDepartmentLogin(message, client);
						break;
					case "!department-create":
						handleCreateDepartment(message, client);
						break;
					case "!employee-list":
						handleEmployeeList(message, client);
						break;
					case "!employee-add":
						handleEmployeeAdd(message, client);
						break;
					case "!employee-remove":
						handleEmployeeRemove(message, client);
						break;
					case "!punch":
						handlePunch(message, client);
						break;
					case "!timesheet":
						handleTimesheetForDate(message, client);
						break;
					case "!timesheet-range":
						handleTimesheetForRange(message, client);
						break;
					case "!ticket-replace":
						handleTicketReplace(message, client);
						break;
					default:
						this.sendToAllClients(msg);
						break;
				}
			} catch(IOException e) {
				System.out.println(e);
			}
  		}
  	}

  	protected void clientConnected(ConnectionToClient client) {
		String msg = "Successful connection for client: " + client.getInetAddress();
		System.out.println(msg);
		try {
			client.sendToClient(msg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//this.sendToAllClients(msg);
	}

	synchronized protected void clientDisconnected(ConnectionToClient client) {
		String msg = "Client: " + client.getInetAddress() + " has disconnected";
		System.out.println(msg);
	}

	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		System.out.println(exception);
		String msg = "Client Exception";
		System.out.println(msg);
	}

	protected void serverStarted() {
		System.out.println("Acme Server listening for connections on port " + getPort());
	}

	protected void serverStopped() {
		System.out.println("Acme Server has stopped listening for connections.");
	}

	public static void main(String[] args) {
  		int port = 0; // Port to listen on

  		try {
  			port = Integer.parseInt(args[0]); // Get port from command line
  		}
  		catch(Throwable t) {
  			port = DEFAULT_PORT; // Set port to 5555
  		}

  		AcmeServer sv = new AcmeServer(port);

  		try {
  			sv.listen(); // Start listening for connections
  		}
  		catch (Exception ex) {
  			System.out.println("ERROR - Could not listen for clients!");
  		}
  	}
}