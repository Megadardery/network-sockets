package data_packages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Peer {

	//The port number used for internal communication
	private int PORT_NUMBER;

	//holds list of files that are in the network and not in current peer
	private volatile ArrayList<FileInfo> ExternalFileList = new ArrayList<>();
	
	//holds list of files shared by current peer
	private ArrayList<FileInfo> LocalFileList = new ArrayList<>();

	// Used for new peers to get list of files.
	private ServerSocket peerListener;

	// Used so that peers can reach this peer to receive files
	private ServerSocket peerSrc;

	private PeerInfo myPeer;
	public boolean initialize(int port) throws IOException, InterruptedException {
	
		PORT_NUMBER = port;
		try{
                    peerListener = new ServerSocket(PORT_NUMBER);
                }
                catch(Exception ex){
                    return false;
                }
                
                
		peerSrc = new ServerSocket(0);

		

		myPeer = new PeerInfo(InetAddress.getLocalHost(), peerSrc.getLocalPort());
		
		//launches the listener, which informs new peers of its own file list
		new Thread(this::_peerListener).start();
		
		new Thread(this::_requestAccepter).start();
		
		//Fills External File list
		refreshFileList();
                
                return true;
	}
	
	public ArrayList<FileInfo> getAvailableFiles(){
		return ExternalFileList;
	}
	public void addFilesToLocalList(String filenames) {
		String[] files = filenames.split("|");


		for (String file : files) {
			FileInfo info = new FileInfo(myPeer, file);
			LocalFileList.add(info);
		}
	}
	
	
	public void requestFile(Reporter t, int idx, String filepath){
		try {
			FileInfo fileInfo = ExternalFileList.get(idx);
			Socket connect = new Socket(fileInfo.owner.address,fileInfo.owner.port);
			FileOutputStream fstream = new FileOutputStream(filepath);
			
			DataOutputStream outToPeer = new DataOutputStream(connect.getOutputStream());
			outToPeer.writeUTF(fileInfo.filename);
			
			DataInputStream inFromPeer = new DataInputStream(connect.getInputStream());
			
	        long len = inFromPeer.readLong();

	        int interval = 10240;

	        byte[] data = new byte[interval];

	        for (long off = 0; off < len; off += interval) {
	            t.report((int)(off * 100 / len));

	            long sz = len - off;
	            if (sz > interval) {
	                sz = interval;
	            }
	            int read = inFromPeer.read(data, 0, (int)sz);
	            off -= sz - read;
	            fstream.write(data, 0, read);
	        }
	        t.report(100);
	        fstream.close();
	        
	        connect.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public interface Reporter {

        void report(int param1);
    }
	
	public void close(){
		try {
			peerSrc.close();
			peerListener.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void _peerListener() {
		try {
			Socket ss = peerListener.accept();
			new Thread(this::_peerListener).start();
			ObjectOutputStream obj = new ObjectOutputStream(ss.getOutputStream());
			obj.writeObject(LocalFileList);
		} catch(SocketException ex){
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void _requestAccepter() {
		try {
			Socket ss = peerSrc.accept();
			new Thread(this::_requestAccepter).start();

			DataInputStream inFromPeer = new DataInputStream(ss.getInputStream());
			DataOutputStream outToPeer = new DataOutputStream(ss.getOutputStream());
			
			String filename = inFromPeer.readUTF();

            long len = new File(filename).length();
            outToPeer.writeLong(len);
           
            int interval = 10240;

            byte[] data = new byte[interval];

            FileInputStream fstream = new FileInputStream(filename);
            
            for (long off = 0; off < len; off += interval) {
                long sz = len - off;
                if (sz > interval) {
                    sz = interval;
                }
                fstream.read(data, 0, (int)sz);
                outToPeer.write(data, 0, (int)sz);
            }
            fstream.close();
            ss.close();
		} catch(SocketException ex){
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void refreshFileList() throws InterruptedException {
		ExternalFileList.clear();
		final byte[] ip;
		try {
			ip = InetAddress.getLocalHost().getAddress();
		} catch (Exception e) {
			return;
		}
		ArrayList<Thread> threads = new ArrayList<>();
		for (int i = 2; i <= 254; i++) {
			final int j = i;
			
			@SuppressWarnings("unchecked")
			Thread e = new Thread(() -> {
				try {
					ip[3] = (byte) j;
					InetAddress address = InetAddress.getByAddress(ip);
					
					if (address.equals(InetAddress.getLocalHost()))
						return;
					
					ArrayList<FileInfo> tempFileList;
					
					Socket ss= new Socket();
					ss.setReuseAddress(true);
					
					ss.connect(new java.net.InetSocketAddress(address,PORT_NUMBER),3000);					
					ObjectInputStream obj = new ObjectInputStream(ss.getInputStream());
					tempFileList = (ArrayList<FileInfo>)obj.readObject();
					ss.close();
					
					for(FileInfo fileInfo : tempFileList){
						ExternalFileList.add(fileInfo);
					}
				} catch (ConnectException ex){
					
				} catch (SocketTimeoutException ex){
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			
			e.start();
			
			threads.add(e);

			
		}
		for (Thread thread : threads) {
			thread.join();
		}
	}
}
