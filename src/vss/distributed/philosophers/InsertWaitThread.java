package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fabian on 03.12.2015.
 */
public class InsertWaitThread extends Thread {

    private final IClientAgent clientAgent;
    private final String client;

    public InsertWaitThread(IClientAgent clientAgent, String client) {
        this.clientAgent = clientAgent;
        this.client = client;
    }


    public void run() {
        boolean done = false;
        while (!done) {
            try {
                done = clientAgent.isInsertSeatsDone();
            } catch (RemoteException e) {
                Logger.getGlobal().log(Level.WARNING, client + " not available.");
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.WARNING, "WaitForInsertThread sleep interrupted.");
            }
        }

        while(true) {
            try {
                clientAgent.update();
                break;
            } catch (RemoteException e) {
                Logger.getGlobal().log(Level.WARNING, client + " not available for update.");
                try {
                    sleep(5000);
                } catch (InterruptedException i) {
                    Logger.getGlobal().log(Level.WARNING, "WaitForInsertThread sleep interrupted.");
                }
            }
        }
    }
}

