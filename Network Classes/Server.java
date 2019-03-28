package socketproject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Server {
	ServerSocket mainSocket;
	String fileName;
	int port;


	public Server(int p, String filename) throws IOException {
		port = p;
		fileName = filename;
		
		mainSocket = new ServerSocket(port);
		//mainSocket.setSoTimeout(0);
	}

	public String getIPAddress() throws UnknownHostException{
		return InetAddress.getLocalHost().getHostAddress();
	}
	public int getPort(){
		return port;
	}
	ArrayList<Socket> clients = new ArrayList<Socket>();

	class fileSender implements Runnable {
		Socket client;
		byte[] data;

		public fileSender(Socket s, byte[] d){
			data = d;
			client = s;
		}
		
		@Override
		public void run() {
			try {
				int len = data.length;

				DataOutputStream outToServer;
				outToServer = new DataOutputStream(client.getOutputStream());

				outToServer.writeInt(len);
				int interval = 5120;
				for(int off = 0; off < len; off+=interval){
					System.out.println(off);
					
					int sz = len - off;
					if (sz > interval)
						sz = interval;
					outToServer.write(data, off, sz);
				}
				//outToServer.write(data);
			}
			catch (SocketException e){
				e.printStackTrace();
				System.out.println("client ignored request");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class clientWaiter implements Runnable {
		ServerSocket mainSocket;

		public clientWaiter(ServerSocket m) {
			mainSocket = m;
		}

		@Override
		public void run() {

			while (true) {
				try {
					clients.add(mainSocket.accept());
					System.out.println("receieved a client");
				} catch (SocketException ex) {
					System.out.println("OK! DONE");
					break;
				} catch (IOException e) {
					
				}
			}
		}

	}

	public void waitForClients() {
		Thread t = new Thread(new clientWaiter(mainSocket));
		t.start();
	}

	public void stopWaiting() throws IOException {
		mainSocket.close();
	}

	public void sentToAllClients() throws IOException {
		byte[] data = Files.readAllBytes(Paths.get(fileName));
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for(Socket s : clients){
			Thread t = new Thread(new fileSender(s, data));
			t.start();
			threads.add(t);
		}
	}

}
