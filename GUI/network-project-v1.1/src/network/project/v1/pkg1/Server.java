/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.project.v1.pkg1;

/**
 *
 * @author MegaDardery
 */
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public Server(String filename) throws IOException {
        this(filename, 0);
    }

    public Server(String filename, int p) throws IOException {
        fileName = filename;

        mainSocket = new ServerSocket(p);
    }

    public static String getIPAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public int getPort() {
        return mainSocket.getLocalPort();
    }
    ArrayList<Socket> clients = new ArrayList<>();

    class fileSender implements Runnable {

        Socket client;

        public fileSender(Socket s) {
            client = s;
        }

        @Override
        public void run() {
            try {
                DataOutputStream outToClient;
                outToClient = new DataOutputStream(client.getOutputStream());

                String msg = Paths.get(fileName).getFileName().toString();

                outToClient.writeUTF(msg);

                long len = new File(fileName).length();
                outToClient.writeLong(len);
               
                int interval = 10240;

                byte[] data = new byte[interval];

                FileInputStream fstream = new FileInputStream(fileName);
                
                for (long off = 0; off < len; off += interval) {
                    long sz = len - off;
                    if (sz > interval) {
                        sz = interval;
                    }
                    fstream.read(data, 0, (int)sz);
                    outToClient.write(data, 0, (int)sz);
                }
                fstream.close();
                System.out.println("File sent");
            } catch (SocketException e) {
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
                    Socket t = mainSocket.accept();
                    clients.add(t);

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

    public void sentToAllClients() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (Socket s : clients) {
            Thread t = new Thread(new fileSender(s));
            t.start();
            threads.add(t);
        }
    }

}
