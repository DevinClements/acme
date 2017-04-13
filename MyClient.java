import java.io.IOException;

/**
 * This class creates a client and is used by ClientConsole
 *
 * @author Dr. Dave Gibson
 * @version February 2017
 */
public class MyClient extends AbstractClient {
	// Instance variables **********************************************
	/**
	 * Reference to the client UI
	 */
	ClientConsole clientUI;

	// Constructors ****************************************************
	/**
	 * Constructs an instance of the client.
	 *
	 * @param host The server to connect to.
	 * @param port The port number to connect on.
	 * @param clientUI The interface type variable.
	 */
	public MyClient(String host, int port, ClientConsole clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		clientUI.display("Type '#connect' to connect to server");
		//openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		// See if server sent a BlobManager
		if(msg instanceof BlobManager) {
			BlobManager blobs = (BlobManager)msg;
			clientUI.display("BlobManager received from server says total is:" + blobs.getTotal());
			clientUI.display("Complete BlobManager:\n" + blobs + "\n");
		}
		// See if server sent a Blob
		else if(msg instanceof Blob) {
			Blob blob = (Blob)msg;
			clientUI.display("Blob received from server:" + blob + "\n");
		}
		// Else, display whatever the server sent.
		else {
			clientUI.display("Message from server:" + msg.toString() + "\n");
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 * @throws InterruptedException
	 */
	public void handleMessageFromClientUI(String message) throws InterruptedException {
		// See if client wants to connect
		if(message.equals("#connect")) {
		      try {
		        this.openConnection();
				clientUI.display("Connection open.");
		      }
		      catch(IOException e) {
		    	  clientUI.display("ERROR - Cannot establish connection with server" );
		    	  return;
		      }
		}
		// Close the connect, but don't quit.
		else if(message.equals("#close")) {
			try {
		        //closeConnection();
		        sendToServer(message);
				clientUI.display("Connection closed.");
			}
			catch(IOException e) {
		        clientUI.display("ERROR - Cannot close connection.");
			}
		}
		// See if client is connected to server.
		else if(message.equals("#isconnected")) {
			clientUI.display("Connection to server=" + this.isConnected());
		}
		// See if client UI sent a series of blobs
		else if(message.startsWith("#blobs")) {
			// Extract integers as a string that occur after command
			String data = message.substring(7);
			String[] strNums = data.split(" ");
			// Create a BlobManager, create Blobs, add them, and then send BlobManager to server.
			BlobManager blobs = new BlobManager();
			for(String strNum : strNums) {
				blobs.addBlob(new Blob(Integer.parseInt(strNum)));
			}
			//transportObject = new Blob(Integer.parseInt(data));
			try {
				sendToServer(blobs);
				clientUI.display("BlobManager sent to server");
			}
			catch(IOException e) {
				clientUI.display("ERROR - Could not send BlobManager to server");
			}
		}
		// See if client UI sent a single blob
		else if(message.startsWith("#blob")) {
			String data = message.substring(6);
			Blob b = new Blob(Integer.parseInt(data));
			try {
				sendToServer(b);
				clientUI.display("Blob sent to server");
			}
			catch(IOException e) {
				clientUI.display("ERROR - Could not send Blob to server.");
			}
		}
		// See if client wants to retrieve BlobManager store in client's HashMap on server.
		else if(message.startsWith("#getBMan")) {
			try {
				sendToServer(message);
				clientUI.display("Request sent to server for BlobManager.");
			}
			catch(IOException e) {
				clientUI.display("Error - Could not send message to server");
			}
		}
		// See if client wants to quit program.
		else if(message.equals("#quit")) {
			try {
		        closeConnection();
				clientUI.display("Client shutting down...");
				Thread.sleep(3000);
				System.exit(0);
			}
			catch(IOException e) {
				System.exit(1);
			}
		}
	}
}