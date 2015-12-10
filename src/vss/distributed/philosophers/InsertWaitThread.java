package vss.distributed.philosophers;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Fabian on 03.12.2015.
 */
public class InsertWaitThread extends Thread
{
    private final Registry registry;
    private final String client;

    public InsertWaitThread(Registry registry, String client)
    {
        this.registry = registry;
        this.client = client;
    }

    public void run()
    {
        IClientAgent clientAgent = null;
        try
        {
            clientAgent = (IClientAgent) registry.lookup(client);
        }
        catch (RemoteException e)
        {
            Logger.getGlobal().log(Level.INFO, client + " not available.");
        }
        catch (NotBoundException e)
        {
            Logger.getGlobal().log(Level.INFO, client + " not bound.");
        }

        while(true) {
            try {
                Logger.getGlobal().log(Level.INFO, "Start update at " + client + ".");
                clientAgent.update();
                break;
            }
            catch (RemoteException e)
            {
                Logger.getGlobal().log(Level.WARNING, client + " not available for update.");
                try
                {
                    sleep(5000);
                }
                catch (InterruptedException i)
                {
                    Logger.getGlobal().log(Level.WARNING, "WaitForInsertThread sleep interrupted.");
                }
            }
        }
    }
}

