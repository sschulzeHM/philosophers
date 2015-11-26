package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRegister extends Remote{
	
	void register(Remote registerObj, String name) throws RemoteException;

}
