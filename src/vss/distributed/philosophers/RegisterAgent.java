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
        clientsAgents.add(name);
        registry.rebind(name, registryObj);
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
                int connectedClients = clientsAgents.size();
                while (!end)
                {
                    if (clientsAgents.size() < 2)
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
                            {
                                Logger.getGlobal().log(Level.WARNING, "UpdateThread sleep interrupted.");
                            }
                        }
                    }

                    end = true;

                }
            }
        }.start();
    }
}
