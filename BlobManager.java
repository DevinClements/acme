import java.io.Serializable;
import java.util.ArrayList;

public class BlobManager implements Serializable  {
	ArrayList<Blob> blobs = new ArrayList<>();

	public void addBlob(Blob b) {
		blobs.add(b);
	}

	public int getTotal() {
		int sum = 0;
		for(Blob b : blobs) {
			sum += b.getX();
		}
		return sum;
	}

	@Override
	public String toString() {
		StringBuilder msg = new StringBuilder("Blobs being mananged\n");
		for(Blob b : blobs) {
			msg.append(b + "\n");
		}
		return msg.toString();
	}
}
