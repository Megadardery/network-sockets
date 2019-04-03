/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data_packages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author MegaDardery
 */
public class RefreshWorker extends SwingWorker {

    public RefreshWorker() {
        GUI.myGUI.btnDownload.setEnabled(false);
        GUI.myGUI.btnRefresh.setEnabled(false);
    }

    @Override
    protected Void doInBackground() {
        GUI.myGUI.myPeer.refreshFileList();

        return null;
    }

    String coln[] = {"Filename", "IP Address"};

    @Override
    protected void done() {
        DefaultTableModel tableModelN = new DefaultTableModel(coln, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        ArrayList<FileInfo> disp = GUI.myGUI.myPeer.getAvailableFiles();
        for (int i = 0; i < disp.size(); ++i) {
            File fname = new File(disp.get(i).filename);
            Object[] obj = {fname.getName(), disp.get(i).owner.address.getHostAddress()};
            tableModelN.addRow(obj);
        }

        GUI.myGUI.tblDownload.setModel(tableModelN);
        GUI.myGUI.btnDownload.setEnabled(true);
        GUI.myGUI.btnRefresh.setEnabled(true);
    }

}
