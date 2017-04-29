import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String command;
	Object[] objects;

	public Message(String command, Object[] objects) {
		this.command = command;
		this.objects = objects;
	}

	public Message(String command, Object object) {
		this(command, new Object[]{object});
	}
}