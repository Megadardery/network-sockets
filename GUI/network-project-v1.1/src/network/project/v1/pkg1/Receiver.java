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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Receiver {

    String directory, IP;
    int port;

    public Receiver(String f, String ip, int p) {
        port = p;
        directory = f;
        IP = ip;
    }
    Socket connect;

    public void connect() throws IOException {
        connect = new Socket(IP, port);
    }

    public void receive(Reporter t) throws IOException {
        
        DataInputStream inFromServer = new DataInputStream(connect.getInputStream());

        String msg = inFromServer.readUTF();
        
        String filepath = Paths.get(directory, msg).toString();
        FileOutputStream fstream = new FileOutputStream(filepath);
        
        long len = inFromServer.readLong();

        int interval = 10240;

        byte[] data = new byte[interval];

        for (long off = 0; off < len; off += interval) {
            t.report((int)(off * 100 / len));

            long sz = len - off;
            if (sz > interval) {
                sz = interval;
            }
            int read = inFromServer.read(data, 0, (int)sz);
            off -= sz - read;
            fstream.write(data, 0, read);
        }
        t.report(100);
        fstream.close();
        
        connect.close();
    }

    public interface Reporter {

        String report(int param1);
    }
}
