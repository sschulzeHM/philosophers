package vss.distributed.philosophers;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterAgent implements IRegisterAgent
{

    private final Registry registry;
    private List<String> clientsAgents;

    public RegisterAgent(Registry registry)
    {
        this.registry = registry;
        this.clientsAgents = new ArrayList<>();
        startUpdateService();
    }

    public void register(Remote registryObj, String name) throws RemoteException
    {
        Logger.getGlobal().log(Level.INFO, String.format("Register: %s.", name));
        addClientAgent(name);
        registry.rebind(name, registryObj);
    }

    public void addClientAgent(String name)
    {
        synchronized (clientsAgents)
        {
            int i = clientsAgents.indexOf(name);
            if (i == -1)
            {
                clientsAgents.add(name);
            }
        }
    }

    public int getClientAgentsSize()
    {
        synchronized (clientsAgents)
        {
            return clientsAgents.size();
        }
    }

    public List<String> getClientAgents()
    {
        return clientsAgents;
    }

    void startUpdateService()
    {
        new Thread()
        {
            public void run()
            {
                boolean end = false;
                int connectedClients = getClientAgentsSize();
                while (true)
                {
                    if (getClientAgentsSize() < 2)
                    {
                        //Logger.getGlobal().log(Level.INFO, String.format("UpdateService: Less than two clients. %d connected clients of %d.", connectedClients, clientsAgents.size()));
                        try
                        {
                            sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, "UpdateThread sleep interrupted.");
                        }
                        continue;
                    }
                    else if (connectedClients == clientsAgents.size())
                    {
                        //Logger.getGlobal().log(Level.INFO, String.format("UpdateService: Nothing changed. %d connected clients of %d.", connectedClients, clientsAgents.size()));
                        try
                        {
                            sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, "UpdateThread sleep interrupted.");
                        }

                        continue;
                    }

                    synchronized (clientsAgents)
                    {
                        for (String address : clientsAgents)
                        {
                            try
                            {
                                Logger.getGlobal().log(Level.WARNING, String.format("UpdateService: Updating %s.", address));
                                IClientAgent agent = (IClientAgent) registry.lookup(address);
                                agent.update();
                            }
                            catch (RemoteException e)
                            {
                                Logger.getGlobal().log(Level.WARNING, String.format("UpdateService: Could not find %s.", address));
                            }
                            catch (NotBoundException e)
                            {
                                Logger.getGlobal().log(Level.WARNING, String.format("UpdateService: Not bound %s.", address));
                                try
                                {
                                    sleep(5000);
                                }
                                catch (InterruptedException e1)
                                {Logger.getGlobal().log(Level.WARNING, "UpdateThread sleep interrupted.");
                                }
                            }
                        }
                        connectedClients = getClientAgentsSize();
                    }
                }
            }
        }.start();
    }
}
