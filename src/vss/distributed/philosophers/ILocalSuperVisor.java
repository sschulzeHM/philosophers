package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 02.12.15.
 */
public interface ILocalSuperVisor extends Remote
{
    void setGlobalMin(int min) throws RemoteException;

    int getLocalMin() throws RemoteException;
}
