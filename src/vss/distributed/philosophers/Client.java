package vss.distributed.philosophers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Client
{
    public static void main(String[] args) throws RemoteException, NotBoundException
    {
        int port = 0;
        if (args.length < 1)
        {
            port = Registry.REGISTRY_PORT;
        }
        else
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

        String ip = "localhost";
        if (args.length > 1)
        {
            ip = args[1];
        }

        System.out.println("Client is running on port " + port + ". Connecting to " + ip);

        IRemoteLogger remoteLogger;
        try
        {
            remoteLogger = (IRemoteLogger) Naming.lookup("//" + ip + ":" + port + "/ServerRemoteLogger");
            for (int i = 0; i < 4; i++)
            {
                remoteLogger.logInfo("Client calling.");
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
}
