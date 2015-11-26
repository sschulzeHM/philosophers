package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 18.11.15.
 */
public interface IRemoteLogger extends Remote
{
    void logInfo(String message) throws RemoteException;
    void logError(String message) throws RemoteException;
}
