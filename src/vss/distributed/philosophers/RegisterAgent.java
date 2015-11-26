package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class RegisterAgent implements IRegister
{

    private final Registry registry;

    public RegisterAgent(Registry registry)
    {
        this.registry = registry;
    }


    public void register(Remote registryObj, String name) throws RemoteException
    {
        registry.rebind(name, registryObj);
    }

}
