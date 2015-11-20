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
    private static final int AVAILABLE_SEATS = 2;
    private static final int AVAILABLE_USHERS = 1;
    private static final int NUMBER_OF_PHILOSOPHERS = 1;
    private static final int NUMBER_OF_HUNGRY_PHILOSOPHERS = 1;
    private static final int MAX_MEALS = 3;

    public static void main(String[] args) throws RemoteException
    {
        // configure Logger
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);

        // remote logger
        IRemoteLogger remoteLogger = new RemoteLogger(Logger.getGlobal());

        int port = getPortFromArgs(args);
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry();

        Remote stubLogger = UnicastRemoteObject.exportObject(remoteLogger, port);
        registry.rebind("ServerRemoteLogger", stubLogger);

        remoteLogger.logInfo("Server online...");
        while (true)
        {

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
