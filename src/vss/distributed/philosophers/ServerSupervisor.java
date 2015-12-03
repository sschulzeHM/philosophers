package vss.distributed.philosophers;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 03.12.15.
 */
public class ServerSupervisor implements IRegisterAgent
{

    private final Registry registry;
    private final String ip;
    private final int port;
    private List<String> supervisors;

    public ServerSupervisor(Registry registry, String ip, int port)
    {
        this.registry = registry;
        this.ip = ip;
        this.port = port;
        this.supervisors = new ArrayList<>();
    }

    @Override
    public void register(Remote registerObj, String name) throws RemoteException
    {
        Logger.getGlobal().log(Level.INFO, String.format("Register Supervisor: %s.", name));
        supervisors.add(name);
        registry.rebind(name, registerObj);
    }

    public void startSupervising()
    {
        new Thread()
        {
            public void run()
            {
                while (true)
                {

                    int globalMin = Integer.MAX_VALUE;
                    int localMin = 0;
                    ILocalSuperVisor supervisor;

                    // calculate current global min from local min
                    for (String supID : supervisors)
                    {
                        try
                        {
                            supervisor = (ILocalSuperVisor) registry.lookup(supID);
                            localMin = supervisor.getLocalMin();
                            if (localMin < globalMin)
                            {
                                globalMin = localMin;
                            }
                        }
                        catch (RemoteException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, String.format("ServerSupervisor: %s could not be found.", supID));
                        }
                        catch (NotBoundException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, String.format("ServerSupervisor: %s is not bound.", supID));
                        }
                    }

                    // update global min in registered supervisors
                    for (String supID : supervisors)
                    {
                        try
                        {
                            supervisor = (ILocalSuperVisor) registry.lookup(supID);
                            supervisor.setGlobalMin(globalMin);
                        }
                        catch (RemoteException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, String.format("ServerSupervisor: %s could not be found.", supID));
                        }
                        catch (NotBoundException e)
                        {
                            Logger.getGlobal().log(Level.WARNING, String.format("ServerSupervisor: %s is not bound.", supID));
                        }
                    }

                    try
                    {
                        sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        Logger.getGlobal().log(Level.WARNING, "UpdateThread sleep interrupted.");
                    }
                }
            }
        }.start();
    }
}
