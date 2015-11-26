package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 26.11.15.
 */
public interface IConnectionAgent extends Remote
{
    void connect(String id) throws RemoteException;
}
