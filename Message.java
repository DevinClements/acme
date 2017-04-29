import java.io.Serializable;

public class Message implements Serializable {
	String command;
	Object object;
	public Message(String command, Object object) {
		this.command = command;
		this.object = object;
	}
}