package data_packages;

import java.util.ArrayList;

public class Main {

    static java.util.Scanner in = new java.util.Scanner(System.in);
    static java.io.PrintStream out = System.out;

    public static void maina(String[] args) {
        Peer client = new Peer();
        out.println("Enter P2P port number: ");
        int port = in.nextInt();
        in.nextLine();
        try {
            client.initialize(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.println("Enter directory name: ");
        String directory = in.nextLine();

        client.addFilesToLocalList(directory);

        ArrayList<FileInfo> x = client.getAvailableFiles();
        int i = 0;
        for (FileInfo fileInfo : x) {
            out.print(i++ + " : ");
            out.println(fileInfo.filename);
        }
        while (true) {
            out.println("Do you want a specific file?");
            int n = in.nextInt();
            if (n == -1) {
                break;
            }
            out.println("where do you want to install it? ");
            in.nextLine();
            String ss = in.nextLine();
            client.requestFile((int a) -> {
            }, n, ss);
        }
        client.close();
    }

}
