package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRegister extends Remote
{

    void register(Remote registerObj, String name) throws RemoteException;
}
