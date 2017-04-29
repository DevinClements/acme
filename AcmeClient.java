import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AcmeClient extends AbstractClient {

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
					System.out.printf("Got a timesheet.\n");
					break;
				default:
					System.out.printf("Invalid command.\n");
					break;
			}
		}
	}

	public void sendMessageToServer(String command) throws IOException {
		Message message = new Message(command, null);
		switch(message.command) {
			case "!punch":
				this.sendToServer(message);
				break;
			case "!timesheet":
				this.sendToServer(message);
				break;
			default:
				System.out.printf("Invalid command.\n");
				break;
		}
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 5555;
		System.out.printf("Client running.\n");
		System.out.printf("Client connecting to server on port:\t%d\n", port);

		try {
			AcmeClient client = new AcmeClient(host, port);
			client.openConnection();
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message;
			while (true) {
				message = fromConsole.readLine();
				client.sendMessageToServer(message);
			}
		} catch(IOException e) {
			System.out.println(e);
			System.out.printf("ERROR - Can't setup connection, terminating client.\n");
	    	System.exit(1);
		}
	}
}