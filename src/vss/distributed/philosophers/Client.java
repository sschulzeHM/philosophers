package vss.distributed.philosophers;

import vss.utils.LogFormatter;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Client extends Thread
{
    private static final int WAIT_TIME = 3000;

    public static void main(String[] args) throws RemoteException
    {
        // Setup logging
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);
        //Logger.getGlobal().setLevel(Level.OFF);

        int port = getPortFromArgs(args);
        String ip = getHostFromArgs(args);
        System.out.println("Client is running on port " + port + ". Connecting to " + ip);

        IRemoteLogger remoteLogger;

        while (true)
        {
            try
            {
                remoteLogger = (IRemoteLogger) Naming.lookup("//" + ip + ":" + port + "/ServerRemoteLogger");
                remoteLogger.logInfo("Client calling.");
                break;
            }
            catch (MalformedURLException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server does not respond. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            catch (NotBoundException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server does not respond. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            catch (ConnectException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server does not respond. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            try
            {
                sleep(WAIT_TIME);
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Sleep interrupted.");
            }
        }

        ISpecification spec;
        Table table;
        Philosopher philosophers[];
        while (true)
        {
            try
            {
                spec = (ISpecification) Naming.lookup("//" + ip + ":" + port + "/Specification");
                table = new Table(spec.getNumberOfSeats(), spec.getNumberOfUshers());
                philosophers = new Philosopher[spec.getNumberOfPhilosophers()];
                for (int i = 0; i < philosophers.length; i++)
                {
                    philosophers[i] = new Philosopher(table, i);
                }
                for (Philosopher phil : philosophers)
                {
                    phil.start();
                }
                break;
            }
            catch (MalformedURLException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server spec not available. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            catch (NotBoundException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server spec not available. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            catch (ConnectException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server spec not available. Trying again in " + WAIT_TIME / 1000 + " seconds.");
            }
            try
            {
                sleep(WAIT_TIME);
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Sleep interrupted.");
            }
        }
    }

    private static String getHostFromArgs(String[] args)
    {
        String ip = "localhost";
        if (args.length > 1)
        {
            ip = args[1];
        }
        return ip;
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
