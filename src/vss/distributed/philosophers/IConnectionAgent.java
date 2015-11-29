package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 26.11.15.
 */
public interface IConnectionAgent extends Remote
{
    int connect() throws RemoteException;

    String getNeighborAgentAddres(int id) throws RemoteException;
}
