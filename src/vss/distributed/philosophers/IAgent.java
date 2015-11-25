package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAgent extends Remote{

	void receiveInfo(String message) throws RemoteException;
	
}
