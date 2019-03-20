package socketproject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sender {
	
	public static void SendFile(String IP, int port, String filename) throws IOException{
		byte[] data = Files.readAllBytes(Paths.get(filename));
        int len = data.length;
        
        Socket connectSocket = new Socket(IP, port);
        
        DataOutputStream outToServer
                = new DataOutputStream(connectSocket.getOutputStream());

        outToServer.writeInt(len);
        outToServer.write(data);

        connectSocket.close();
	}
	
}
