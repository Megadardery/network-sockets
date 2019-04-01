package data_packages;
import java.io.Serializable;

public class FileInfo implements Serializable {
	private static final long serialVersionUID = 656857255758163993L;
	
	FileInfo(PeerInfo owner, String filename){
		this.owner = owner;
		this.filename = filename;
	}
	public PeerInfo owner;
	public String filename;
	
}
