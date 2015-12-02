package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRegisterAgent extends Remote
{
    void register(Remote registerObj, String name) throws RemoteException;
}
