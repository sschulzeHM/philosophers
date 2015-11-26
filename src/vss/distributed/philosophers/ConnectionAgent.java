package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 26.11.15.
 */
public class ConnectionAgent implements IConnectionAgent
{

    @Override
    public void connect(String id) throws RemoteException
    {
        Logger.getGlobal().log(Level.INFO, id + " connected.");
        // TODO update specification
    }
}
