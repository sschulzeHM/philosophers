package vss.distributed.philosophers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Client
{
    public static void main(String[] args) throws RemoteException, NotBoundException
    {
        int port = Registry.REGISTRY_PORT;
        
        if (args.length >= 1)
        {
        	try
            {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Given Port is not a number. Stopping server.");
                return;
            }
        }
         
       
        String host = "127.0.0.1";
        if (args.length >= 2)
        {
            host = args[1];
        }

        System.out.println("Client is running on port " + port + ". Connecting to " + host);
        
        IAgent clientAgent = new ClientAgent();
        Remote stub = UnicastRemoteObject.exportObject(clientAgent, 0);
        IRemoteLogger remoteLogger;
        IRegister registerAgent;
        
        try {
			registerAgent = (IRegister) Naming.lookup("//" + host + ":" + port + "/RegisterAgent");
			registerAgent.register(stub,"ClientAgent");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
        while(true )
        try {
			remoteLogger = (IRemoteLogger) Naming.lookup("//" + host + ":" + port + "/ServerRemoteLogger");
			//Alternative:registry.lookup("ServerRemoteLogger");
			remoteLogger.logInfo("Client calling.");
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        
        
        
    }
    
}
