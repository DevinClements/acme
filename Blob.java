import java.io.Serializable;

public class Blob implements Serializable {
	private int x;

	public Blob(int x) {
		super();
		this.x = x;
	}

	public int getX() {
		return x;
	}

	@Override
	public String toString() {
		return "Blob, x=" + x;
	}

}
