/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_packages;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author MegaDardery
 */
public class DownloadWorker extends SwingWorker<Boolean, Integer> {

    int idx;
    String filename;

    public DownloadWorker(int idx, String filename) {
        GUI.myGUI.btnDownload.setEnabled(false);
        GUI.myGUI.btnRefresh.setEnabled(false);

        this.idx = idx;
        this.filename = filename;
    }

    @Override
    protected Boolean doInBackground() {
        return GUI.myGUI.myPeer.requestFile(this::publish, idx, filename);
    }

    @Override
    protected void done() {
        try {
            if (get().equals(false)) {
                JOptionPane.showConfirmDialog(GUI.myGUI,
                        "This file is not being shared anymore by the peer, or the peer is offline. "
                        + "The list needs to be refreshed.",
                        "Unable to request file",
                        JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
                GUI.myGUI.myPeer.refreshFileList();
            }
            GUI.myGUI.btnDownload.setEnabled(true);
            GUI.myGUI.btnRefresh.setEnabled(true);
        } catch (InterruptedException | ExecutionException ex) {
            //Logger.getLogger(DownloadWorker.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @Override
    protected void process(List<Integer> args) {
        int v = args.get(args.size() - 1);
        GUI.myGUI.prgProgress.setValue(v);
    }
}
