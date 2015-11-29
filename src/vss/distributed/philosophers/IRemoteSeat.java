package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 29.11.15.
 */
public interface IRemoteSeat extends Remote
{
    boolean takeRightFork() throws RemoteException;

    int getId() throws RemoteException;

    void releaseForks() throws RemoteException;

    void releaseRightFork() throws RemoteException;
}
