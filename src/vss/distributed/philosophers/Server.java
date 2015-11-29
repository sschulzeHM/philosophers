package vss.distributed.philosophers;

import vss.utils.LogFormatter;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Server extends HostApplication
{

    public static void main(String[] args) throws RemoteException
    {
        // configure Logger
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);

        // parse arguments
        int port = getPortFromArgs(args);
        String host = getHostFromArgs(args);
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry();

        // create remote objects
        IRemoteLogger remoteLogger = new RemoteLogger(Logger.getGlobal());
        Remote stubLogger = UnicastRemoteObject.exportObject(remoteLogger, port);
        registry.rebind("ServerRemoteLogger", stubLogger);

        IRegister registerAgent = new RegisterAgent(registry);
        Remote stubRegisterAgent = UnicastRemoteObject.exportObject(registerAgent, port);
        registry.rebind("RegisterAgent", stubRegisterAgent);

        IConnectionAgent connectionAgent = new ConnectionAgent(registry, host, port);
        Remote stubConnectionAgent = UnicastRemoteObject.exportObject(connectionAgent, port);
        registry.rebind("ConnectionAgent", stubConnectionAgent);

        remoteLogger.logInfo("Server online...");

        //IClientAgent clientAgent;
        while (true)
        {
            /*try
            {
                clientAgent = (IClientAgent) registry.lookup("ClientAgent");
                clientAgent.receiveInfo("Message: Server to Client");
            }
            catch (NotBoundException e)
            {
            }*/
        }

    }
}
