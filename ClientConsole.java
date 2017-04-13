import java.io.*;

/**
 * This class constructs the UI for a chat client.
 *
 * @author Dr. Dave Gibson
 * @version February 2017
 */
public class ClientConsole {
	// Class variables *************************************************
	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Instance variables **********************************************
	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	MyClient client;

	// Constructors ****************************************************
	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientConsole(String host, int port) {
	    try {
	    	client= new MyClient(host, port, this);
	    }
	    catch(IOException exception) {
	    	System.out.println("ERROR - Can't setup connection, terminating client.");
	    	System.exit(1);
	    }
	}

	// Instance methods ************************************************
	/**
	 * This method waits for input from the console.  Once it is
	 * received, it sends it to the client's message handler.
	 */
	public void accept() {
		try {
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
			String message;

			while (true) {
				message = fromConsole.readLine();
				client.handleMessageFromClientUI(message);
			}
		}
		catch (Exception ex) {
			System.out.println("ERROR - Unexpected error while reading from console.");
		}
	}

	/**
	 * This method displays a message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}

	// Class methods ***************************************************
	/**
	 * This method is responsible for the creation of the Client UI.
	 *
	 * @param args[0] The host to connect to.
	 */
  	public static void main(String[] args) {
  		String host = "";
  		try {
  			host = args[0];
  		}
  		catch(ArrayIndexOutOfBoundsException e) {
  			host = "localhost";
  		}
  		ClientConsole chat= new ClientConsole(host, DEFAULT_PORT);
  		chat.accept();  //Wait for console data
  	}
}
