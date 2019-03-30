/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.project.v1.pkg1;

import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 *
 * @author MegaDardery
 */
public class ReceiverWorker extends SwingWorker<Void, Integer> {

    Receiver receiver;
    JProgressBar progress;
    public ReceiverWorker(Receiver r, JProgressBar prg) {
        receiver = r;
        progress = prg;
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish(0);
        receiver.receive((a) -> {
            publish(a);
            return null;
        });
        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        int i = chunks.get(chunks.size() - 1);
        progress.setValue(i);
    }

    @Override
    protected void done() {

    }
}
