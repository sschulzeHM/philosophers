package vss.distributed.philosophers;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RegisterAgent implements IRegister
{

    private final Registry registry;
    private List<String> connectedCliens;

    public RegisterAgent(Registry registry)
    {
        this.registry = registry;
        this.connectedCliens = new ArrayList<>();
    }


    public void register(Remote registryObj, String name) throws RemoteException
    {
        connectedCliens.add(name);
        registry.rebind(name, registryObj);
    }

    public List<String> getConnectedClients(){
        return connectedCliens;
    }
}
