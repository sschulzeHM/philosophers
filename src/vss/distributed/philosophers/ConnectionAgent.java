package vss.distributed.philosophers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
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

    private ArrayList<String> clients;

    public ConnectionAgent(Registry registry, String host, int port)
    {
        this.registry = registry;
        this.host = host;
        this.port = port;
        this.clients = new ArrayList<>();
    }

    /**
     * If a client connected to server it gets his unique ID.
     * @return Client ID
     * @throws RemoteException
     */
    @Override
    public synchronized String connect(String host, int port) throws RemoteException
    {
        String id = String.format("%s%d", host.replace(".", ""), port);

        int i = clients.indexOf(id);
        if (i == -1)
        {
            clients.add(id);
        }

        ISpecification spec = new Specification(NUMBER_OF_PHILOSOPHERS, AVAILABLE_USHERS, AVAILABLE_SEATS++, NUMBER_OF_HUNGRY_PHILOSOPHERS, id, MAX_MEALS);
        Remote stubSpec = UnicastRemoteObject.exportObject(spec, 0);

        registry.rebind(String.format("ClientSpec%s", id), stubSpec);
        Logger.getGlobal().log(Level.INFO, "Client " + id + " connected.");
        return id;
    }

    @Override
    public String getNeighborAgentAddress(String id) throws RemoteException
    {
        updateClientList();
        String neighborID = clients.get((clients.indexOf(id) + 1) % clients.size());
        String address = "//" + host + ":" + port + String.format("/ClientAgent%s", neighborID);
        return address;
    }

    public int getNumOfClients() {
        return clients.size();
    }

    public String getClient(int index)
    {
        if (index >= clients.size())
        {
            return "";
        }
        return clients.get(index);
    }

    private void updateClientList()
    {
        IClientAgent neighborAgent;
        String clientID;
        String address;
        for (Iterator<String> iterator = clients.iterator(); iterator.hasNext(); )
        {
            clientID = iterator.next();
            address = "//" + host + ":" + port + String.format("/ClientAgent%s", clientID);
            try
            {
                Logger.getGlobal().log(Level.INFO, "Trying " + clientID + " at " + address);
                neighborAgent = (IClientAgent) Naming.lookup(address);
            }
            catch (NotBoundException e)
            {
                Logger.getGlobal().log(Level.INFO, "Client " + clientID + " disconnected. Available clients: " + getNumOfClients());
                iterator.remove();
                continue;
            }
            catch (MalformedURLException e)
            {
                Logger.getGlobal().log(Level.INFO, "Client " + clientID + " disconnected. Available clients: " + getNumOfClients());
                iterator.remove();
                continue;
            }
            catch (RemoteException e)
            {
                Logger.getGlobal().log(Level.INFO, "Client " + clientID + " disconnected. Available clients: " + getNumOfClients());
                iterator.remove();
                continue;
            }
        }
    }

}


