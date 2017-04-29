import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Date;

public class AcmeClient extends AbstractClient {

	public static boolean shouldContinue = true;

	public AcmeClient(String host, int port) throws IOException {
		super(host, port);
	}

	public void handleMessageFromServer(Object msg) {
		// See if server sent a BlobManager
		if(msg instanceof Message) {
			Message message = (Message) msg;
			switch(message.command) {
				case "!punch":
					System.out.printf("We punched the timeclock.\n");
					break;
				case "!timesheet":
					Timesheet sheet = (Timesheet) message.objects[0];
					System.out.println(sheet.getHoursWorked());
					break;
				case "!success":
					System.out.printf("Success: %s\n", (String) message.objects[0]);
					break;
				case "!error":
					System.out.printf("Error: %s\n", (String) message.objects[0]);
					break;
				default:
					System.out.printf("Invalid command.\n");
					break;
			}
			System.out.println("lets continue.");
			shouldContinue = true;
		}
	}

	public void sendMessageToServer(String command) throws IOException {
		Scanner scan = new Scanner(System.in);
		switch(command) {
			case "!department-login":
				System.out.printf("Enter department code:\t");
				String departmentCode = scan.next();
				System.out.printf("\n");
				this.sendToServer(new Message(command, departmentCode));
				break;
			case "!department-create":
				System.out.printf("Enter department code:\t");
				String dCode = scan.next();
				System.out.printf("\n");
				System.out.printf("Enter department type:");
				System.out.printf("\n\n");
				System.out.printf("[p] Production\n");
				System.out.printf("[i] Indirect Production\n");
				System.out.printf("\n");
				DepartmentType type = null;
				while(type == null) {
					System.out.printf("Choice:\t");
					String choice = scan.next();
					if(choice.equals("p")) {
						type = DepartmentType.Production;
					}
					if(choice.equals("i")) {
						type = DepartmentType.IndirectProduction;
					}
				}
				Object[] objectsToSend = new Object[]{dCode, type};
				this.sendToServer(new Message(command, objectsToSend));
				System.out.printf("\n\n");
				break;
			case "!punch":
				this.sendToServer(new Message(command, new Date()));
				break;
			case "!timesheet":
				this.sendToServer(new Message(command, new Date()));
				break;
			default:
				System.out.printf("Invalid command.\n");
				break;
		}
		scan.close();
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 5555;
		System.out.printf("\n");
		System.out.printf("Client running.\n");
		System.out.printf("Client connecting to server on port:\t%d\n\n", port);

		try {
			AcmeClient client = new AcmeClient(host, port);
			client.openConnection();
			System.out.printf("Client connected.");
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				if(shouldContinue) {
					String[] commands = new String[]{"!department-login", "!department-create", "!punch", "!timesheet"};
					System.out.printf("\n\n");
					System.out.printf("\n*********\nMain Menu\n*********\n");
					System.out.printf("Here are your commands.");
					System.out.printf("\n\n");
					for(int i = 0; i < commands.length; i++) {
						System.out.printf("[%d] %s\n", i, commands[i]);
					}
					System.out.printf("\n");
					System.out.printf("Enter Choice:\t");
					int choice = Integer.parseInt(fromConsole.readLine());
					System.out.printf("\n\n");
					client.sendMessageToServer(commands[choice]);
					shouldContinue = false;
				}
			}
		} catch(IOException e) {
			System.out.println(e);
			System.out.printf("ERROR - Can't setup connection, terminating client.\n");
	    	System.exit(1);
		}
	}
}