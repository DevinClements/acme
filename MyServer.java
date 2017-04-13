import java.io.*;

/**
*  This class extends the abstract superclass to illustrate some basic
*  functionality for the server.
*
*	@author Dr Dave Gibson
*	@version February 2017
**/
public class MyServer extends AbstractServer {

	// Class variables *************************************************
	/**
   	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public MyServer(int port) {
		super(port);
	}

	// Instance methods ************************************************

	/**
     * This method handles any messages received from the client.
	 *
     * @param msg The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient (Object msg, ConnectionToClient client) {
		// Check to see if msg is a String
		if(msg instanceof String) {
			String command = (String)msg;

			if(command.equals("#getBMan")) {
  	  			System.out.println("Request for BlobManager received");
				// Get BlobManager from client's HashMap on server and send to client
				BlobManager bMan = (BlobManager)client.getInfo("bman");
				try {
  	  	  			client.sendToClient(bMan);
  	  	  			System.out.println("Sending BlobManager to client");
  	  			}
				catch(IOException e) {
					System.out.println("ERROR - Couldn't send BlobManager to client");
				}
  	  		}
			else if(command.equals("#close")) {
				try {
					client.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
  	  		else {
  	  			// For illustration purposes, if msg is not a command, then simply send it back
  	  			// to all clients. Illustrates a chat feature.
  	  	  		this.sendToAllClients(msg);
  	  		}
  		}
		// Check to see if msg is a BlobManager
		else if(msg instanceof BlobManager) {
  			System.out.println("Request to save BlobManager sent by client");
  			BlobManager bMan = (BlobManager)msg;
  			// Store BlobManager in client's HashMap.
  			client.setInfo("bman", bMan);
  			System.out.println("BlobManager saved in client's HashMap");
  		}
		// Check to see if msg is a Blob
		else if(msg instanceof Blob) {
  			Blob blob = (Blob)msg;
  			System.out.println("Server received a Blob" + blob);
			try {
	  	  			client.sendToClient("Got the Blob you sent");
	  			}
			catch(IOException e) {
				System.out.println("ERROR - Couldn't send message to client");
			}
  		}
		// Else, message is not recognized!
		else {
			System.out.println("Unrecognized message received: " + msg + " from " + client);
		}
  	}

	/**
	 *  Hook method to inform client of successful connection
	 *  @throws IOException
	 */
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

	/**
	 *  Hook method to illustrate server's knowledge of client disconnecting,
	 *  turns out, client is already null at this point.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		String msg = "Client: " + client.getInetAddress() + " has disconnected";
		System.out.println(msg);
	}

	/**
	 *  Hook method to illustrate server's knowledge of client quitting or
	 *  client abnormally terminating, e.g Ctrl+c from command prompt.
	 *  turns out, client is already null at this point.
	 */
	@Override
 	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		String msg = "Client Exception";
		System.out.println(msg);
	}

	/**
	 * Hook method to inform server console of successful start.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * Hook method to inform server console that server is no longer listening
	 * for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the server.
     *
     * @param args[0] The port number to listen on.  Defaults to 5555 if no argument is entered.
     */
	public static void main(String[] args) {
  		int port = 0; // Port to listen on

  		try {
  			port = Integer.parseInt(args[0]); // Get port from command line
  		}
  		catch(Throwable t) {
  			port = DEFAULT_PORT; // Set port to 5555
  		}

  		MyServer sv = new MyServer(port);

  		try {
  			sv.listen(); // Start listening for connections
  		}
  		catch (Exception ex) {
  			System.out.println("ERROR - Could not listen for clients!");
  		}
  	}
}
