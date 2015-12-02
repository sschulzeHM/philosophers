package vss.distributed.philosophers;

import vss.utils.LogFormatter;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Client extends HostApplication
{
    private static final int WAIT_TIME = 3000;
    private int ID;

    public static void main(String[] args) throws RemoteException
    {
        // Setup logging
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);
        //Logger.getGlobal().setLevel(Level.OFF);

        String myIP = getHostFromArgs(args, 2);
        int myPort = getPortFromArgs(args, 3);

        String serverIP = getHostFromArgs(args, 0);
        int serverPort = getPortFromArgs(args, 1);
        Logger.getGlobal().log(Level.INFO, "Client running. Connecting to " + serverIP + ":" + serverPort);

        IConnectionAgent connectionAgent;
        String id;

        while (true)
        {
            try
            {
                connectionAgent = (IConnectionAgent) Naming.lookup("//" + serverIP + ":" + serverPort + "/ConnectionAgent");
                id = connectionAgent.connect(myIP, myPort);
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
        IRegisterAgent registerAgent;
        IClientAgent clientAgent;
        while (true)
        {
            try
            {
                // get specification
                spec = (ISpecification) Naming.lookup("//" + serverIP + ":" + serverPort + "/ClientSpec" + id);
                Logger.getGlobal().log(Level.INFO, String.format("Client %s has to create Philosopher: %d,  Seats: %d, Ushers: %d", spec.getClientID(), spec.getNumberOfPhilosophers(), spec.getNumberOfSeats(), spec.getNumberOfUshers()));
                // init table
                table = new Table(spec.getNumberOfSeats(), spec.getNumberOfUshers());

                // create own agent
                clientAgent = new ClientAgent(table, connectionAgent, id);
                Remote stubAgent = UnicastRemoteObject.exportObject(clientAgent, 0);
                registerAgent = (IRegisterAgent) Naming.lookup("//" + serverIP + ":" + serverPort + "/RegisterAgent");
                registerAgent.register(stubAgent, String.format("ClientAgent%s", id));

                IClientAgent myAgent = (IClientAgent) Naming.lookup("//" + serverIP + ":" + serverPort + String.format("/ClientAgent%s", id));

                // create philosophers
                philosophers = new Philosopher[spec.getNumberOfPhilosophers()];
                for (int i = 0; i < philosophers.length; i++)
                {
                    philosophers[i] = new Philosopher(table, i);
                }
                // TODO create hungry philosophers

                // start philosophers
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
}
