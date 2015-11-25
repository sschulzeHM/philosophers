package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class ClientAgent implements IAgent, IRegisterObject {
	
	@Override
	public void receiveInfo(String message) throws RemoteException {
		
		System.out.println(message);
	}

	@Override
	public String getName(){
		return "ClientAgent";
	}

	@Override
	public Remote getObject(){
		return this;
	}

}
