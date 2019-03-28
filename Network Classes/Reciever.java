package socketproject;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reciever {
	String filename, IP;
	int port;
	public Reciever(String f, String ip, int p){
		port = p;
		filename = f;
		IP = ip;
	}
	public void recieve() throws IOException{
		Socket connect = new Socket(IP, port);
		DataInputStream inFromServer = new DataInputStream(connect.getInputStream());
		int len = inFromServer.readInt();
		byte[] arg = new byte[len];
		
		inFromServer.readFully(arg);
		
		Files.write(Paths.get(filename), arg);
		
		connect.close();
	}
}
