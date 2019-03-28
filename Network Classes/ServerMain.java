package socketproject;
import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args) throws IOException{
    	
    	
    	System.out.println("Do you want to recieve or send? (1- recieve, 2- send)");
    	Scanner sc = new Scanner(System.in);
    	int c = sc.nextInt();
    	sc.nextLine();
    	if (c == 1){
    		System.out.println("Enter Filename: ");
    		String filename = sc.nextLine();
    		System.out.println("Enter IP Addresss: ");
    		String ipadd = sc.nextLine();
    		System.out.println("Enter Port: ");
    		int portno = sc.nextInt();
    		
    		Reciever r = new Reciever(filename,ipadd,portno);
    		r.recieve();
    	}
    	else if (c == 2){
    		int port = 31211;
    		System.out.println("Enter Filename: ");
    		String filename = sc.nextLine();
    		
    		Server r = new Server(port,filename);
    		
    		System.out.print("IP address is: ");

			System.out.println(r.getIPAddress());
			
			System.out.print("Port is: ");
			System.out.println(r.getPort());
			r.waitForClients();
			System.out.println("Type 'stop' to stop listening");
			while(true){
				sc.nextLine();
							
				r.stopWaiting();
				r.sentToAllClients();
				
				break;
								
			}
			sc.nextLine();	
    	}
    	System.out.println("Program closed!");
    	sc.close();
    }
}
