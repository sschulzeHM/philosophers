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
public class Server
{
    private static int AVAILABLE_SEATS = 2;
    private static int AVAILABLE_USHERS = 1;
    private static int NUMBER_OF_PHILOSOPHERS = 1;
    private static int NUMBER_OF_HUNGRY_PHILOSOPHERS = 1;
    private static int MAX_MEALS = 3;

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
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry();

        // create remote objects
        IRemoteLogger remoteLogger = new RemoteLogger(Logger.getGlobal());
        Remote stubLogger = UnicastRemoteObject.exportObject(remoteLogger, port);
        registry.rebind("ServerRemoteLogger", stubLogger);

        IRegister registerAgent = new RegisterAgent(registry);
        Remote stubRegisterAgent = UnicastRemoteObject.exportObject(registerAgent, port);
        registry.rebind("RegisterAgent", stubRegisterAgent);

//        ISpecification clientSpec = new Specification(4, 1, 2, );
//        Remote stubSpec = UnicastRemoteObject.exportObject(clientSpec, port);
//        registry.rebind("Specification", stubSpec);

        IConnectionAgent connectionAgent = new ConnectionAgent(registry);
        Remote stubConnectionAgent = UnicastRemoteObject.exportObject(connectionAgent, port);
        registry.rebind("ConnectionAgent", stubConnectionAgent);

        remoteLogger.logInfo("Server online...");

        //IAgent clientAgent;
        while (true)
        {
            /*try
            {
                clientAgent = (IAgent) registry.lookup("ClientAgent");
                clientAgent.receiveInfo("Message: Server to Client");
            }
            catch (NotBoundException e)
            {
            }*/
        }

    }

    private static int getPortFromArgs(String[] args)
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
                port = Registry.REGISTRY_PORT;
            }
        }
        if (port == 0)
        {
            port = Registry.REGISTRY_PORT;
        }
        return port;
    }
}
