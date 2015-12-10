package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fabian on 03.12.2015.
 */
public class InsertWaitThread extends Thread {

  //  private final IClientAgent clientAgent;
    private final Registry registry;
    private final String client;

    public InsertWaitThread(Registry registry, String client) {
      //  this.clientAgent = clientAgent;
        this.registry = registry;
        this.client = client;
    }


    public void run() {
        boolean done = false;
        IClientAgent clientAgent = null;
        try {
            clientAgent = (IClientAgent) registry.lookup(client);
        } catch (RemoteException e) {
            Logger.getGlobal().log(Level.INFO, client + " not available.");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        /*while (!done) {
            try {
                Logger.getGlobal().log(Level.INFO, "Ask Readystate at Client " + client + ".");
                done = clientAgent.isInsertSeatsDone();
            } catch (RemoteException e) {
                Logger.getGlobal().log(Level.WARNING, client + " not available.");
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.WARNING, "WaitForInsertThread sleep interrupted.");
            }
        }*/

        while(true) {
            try {
                Logger.getGlobal().log(Level.INFO, "Start update at " + client + ".");
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

