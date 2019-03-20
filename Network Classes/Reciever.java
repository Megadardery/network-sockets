package socketproject;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reciever {
	ServerSocket reciever;
	int port;
	Reciever(int p) throws IOException{
		port = p;
		reciever = new ServerSocket(port);
	}
	String getIPAddress() throws UnknownHostException{
		return InetAddress.getLocalHost().getHostAddress();
	}
	int getPort(){
		return port;
	}
	void RecieveFile(String filename) throws IOException{
		Socket client = reciever.accept();
		
        DataInputStream inFromClient = new DataInputStream(client.getInputStream());

        int size = inFromClient.readInt();
        byte[] arg = new byte[size];
        
        inFromClient.readFully(arg);
        
        Files.write(Paths.get(filename),arg);
        
        client.close();
	}
	protected void finalize() throws IOException{
		reciever.close();
	}
}
