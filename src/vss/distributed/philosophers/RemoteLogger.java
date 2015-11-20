package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class RemoteLogger implements IRemoteLogger
{
    private Logger logger;

    public RemoteLogger(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void logInfo(String message) throws RemoteException
    {
        logger.log(Level.INFO, message);
    }

    @Override
    public void logError(String message) throws RemoteException
    {
        logger.log(Level.WARNING, message);
    }
}
