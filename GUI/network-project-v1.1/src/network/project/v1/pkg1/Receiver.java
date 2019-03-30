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
    Path filepath;

    public void connect() throws IOException {
        connect = new Socket(IP, port);
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        filepath = Paths.get(directory, inFromServer.readLine());
    }

    public void receive(Reporter t) throws IOException {
        Socket connect = new Socket(IP, port);
        FileOutputStream fstream = new FileOutputStream(filepath.toString());
        DataInputStream inFromServer = new DataInputStream(connect.getInputStream());
        int len = inFromServer.readInt();

        int interval = 5120;

        byte[] data = new byte[interval];

        for (int off = 0; off < len; off += interval) {
            t.report(off * 100 / len);

            int sz = len - off;
            if (sz > interval) {
                sz = interval;
            }
            inFromServer.read(data, 0, sz);
            fstream.write(data, 0, sz);
        }

        fstream.close();
        connect.close();
    }

    public interface Reporter {

        String report(int param1);
    }
}
