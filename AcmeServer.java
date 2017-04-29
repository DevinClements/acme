import java.io.IOException;

public class AcmeServer extends AbstractServer {

	final public static int DEFAULT_PORT = 5555;

	public AcmeServer(int port) {
		super(port);
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// Check to see if msg is a String
		if(msg instanceof Message) {
			Message message = (Message) msg;

			switch(message.command) {
				case "!punch":
					System.out.println("Punching clock.");
					Message punchMessage = new Message("!punch", "swagger");
					try {
						client.sendToClient(punchMessage);
					} catch(IOException e) {
						System.out.println(e);
					}
					break;
				case "!timesheet":
					System.out.println("Sending timesheet for employee.");
					Message timeMessage = new Message("!timesheet", "swagger");
					try {
						client.sendToClient(timeMessage);
					} catch(IOException e) {
						System.out.println(e);
					}
					break;
				default:
					this.sendToAllClients(msg);
					break;
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