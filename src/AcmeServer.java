import java.io.IOException;
import java.util.HashMap;
import java.util.Date;

public class AcmeServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;
	final private String CLIENT_KEY_DEPARTMENT_CODE = "department_code";

	HashMap<String, Department> departmentStore = new HashMap<String, Department>();

	public AcmeServer(int port) {
		super(port);
	}

	public void handleDepartmentLogin(Message message, ConnectionToClient client) throws IOException {
		String code = (String) message.objects[0];
		System.out.println(code);
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
		client.sendToClient(new Message("!success", null));
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
		Date date = (Date) message.objects[1];
		Timesheet sheet = department.getTimesheet(employeeId, date);
		client.sendToClient(new Message("!timesheet", sheet));
		return;
	}
	
	public void handleEmployeeList(Message message, ConnectionToClient client) throws IOException {
		String departmentCode = (String) client.getInfo(CLIENT_KEY_DEPARTMENT_CODE);
		Department department = departmentStore.get(departmentCode);
		Employee[] emps = department.getAllEmployee();
		for(Employee e: emps) {
			System.out.println(e.name);
		}
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
		Employee emp = department.removeEmployee(id);
		System.out.printf("We removed %s\n", emp.name);
		client.sendToClient(new Message("!employee-remove", ""));
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