package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 26.11.15.
 */
public class ConnectionAgent implements IConnectionAgent
{
    private final Registry registry;
    private int counterClientID;
    private HashMap<Integer,ISpecification> clientSpecs;
    public ConnectionAgent(Registry registry){
        this.registry = registry;
        this.counterClientID = 0;
        this.clientSpecs = new HashMap<>();
    }

    /**
     * If a client connected to server it gets his unique ID.
     * @return Client ID
     * @throws RemoteException
     */
    @Override
    public int connect() throws RemoteException
    {
        synchronized (this) {
            counterClientID++;
            clientSpecs.put(counterClientID,new Specification(4, 1, 2, counterClientID));
            Remote stubSpec = UnicastRemoteObject.exportObject(clientSpecs.get(counterClientID), 0);
            registry.rebind(String.format("Client%dSpec", counterClientID), stubSpec);
            Logger.getGlobal().log(Level.INFO, "Client " + counterClientID + " connected.");
            return counterClientID;
        }
    }
}
