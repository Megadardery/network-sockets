package socketproject;
import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args){
    	
    	System.out.println("Do you want to recieve or send? (1- recieve, 2- send)");
    	Scanner sc = new Scanner(System.in);
    	int c = sc.nextInt();
    	sc.nextLine();
    	if (c == 1){
    		System.out.println("Enter filename: ");
    		String filename = sc.nextLine();
    		
    		int port = 12435;
    		try {
				Reciever r = new Reciever(port);
				System.out.print("IP address is: ");
				System.out.println(r.getIPAddress());
				
				System.out.print("Port is: ");
				System.out.println(r.getPort());
				
				r.RecieveFile(filename);
				System.out.println("finished");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    	else if (c == 2){
    		System.out.println("Enter filename: ");
    		String filename = sc.nextLine();
    		
    		System.out.println("Enter reciever IP: ");
    		String IP = sc.nextLine();
    		System.out.println("Enter port number");
    		int port = sc.nextInt();
    		try {
				Sender.SendFile(IP, port, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}