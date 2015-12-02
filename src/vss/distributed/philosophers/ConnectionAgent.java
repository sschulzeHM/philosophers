package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 26.11.15.
 */
public class ConnectionAgent implements IConnectionAgent
{
    private static int AVAILABLE_SEATS = 3;
    private static int AVAILABLE_USHERS = 2;
    private static int NUMBER_OF_PHILOSOPHERS = 3;
    private static int NUMBER_OF_HUNGRY_PHILOSOPHERS = 1;
    private static int MAX_MEALS = 3;

    private final Registry registry;
    private final String host;
    private final int port;
    private int counterClientID;

    private HashMap<Integer,ISpecification> clientSpecs;

    public ConnectionAgent(Registry registry, String host, int port)
    {
        this.registry = registry;
        this.host = host;
        this.port = port;
        this.counterClientID = 0;
        this.clientSpecs = new HashMap<>();
    }

    /**
     * If a client connected to server it gets his unique ID.
     * @return Client ID
     * @throws RemoteException
     */
    @Override
    public synchronized int connect() throws RemoteException
    {
        counterClientID++;
        clientSpecs.put(counterClientID, new Specification(NUMBER_OF_PHILOSOPHERS, AVAILABLE_USHERS, AVAILABLE_SEATS + (counterClientID - 1), counterClientID));
        Remote stubSpec = UnicastRemoteObject.exportObject(clientSpecs.get(counterClientID), 0);
        registry.rebind(String.format("Client%dSpec", counterClientID), stubSpec);
        Logger.getGlobal().log(Level.INFO, "Client " + counterClientID + " connected.");
        return counterClientID;
    }

    @Override
    public String getNeighborAgentAddres(int id) throws RemoteException
    {
        int neighborID = 1;

        // requesting client is 1 and there are more than one client
        if (id == 1)
        {
            neighborID = clientSpecs.size();
        }

        // default; many clients, not the first is requesting
        else
        {
            neighborID = id - 1;
        }

        return String.format("//%s:%d/ClientAgent%d", host, port, neighborID);
    }

    public int getNumOfClients() {
        return clientSpecs.size();
    }

}
