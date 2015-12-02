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

        int port = getPortFromArgs(args);
        String host = getHostFromArgs(args);
        Logger.getGlobal().log(Level.INFO, "Client is running on port " + port + ". Connecting to " + host);

        IConnectionAgent connectionAgent;
        int id;

        while (true)
        {
            try
            {
                connectionAgent = (IConnectionAgent) Naming.lookup("//" + host + ":" + port + "/ConnectionAgent");
                id = connectionAgent.connect();
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
        IRegister registerAgent;
        IClientAgent clientAgent;
        while (true)
        {
            try
            {
                // get specification
                spec = (ISpecification) Naming.lookup("//" + host + ":" + port + "/Client"+id+"Spec");
                Logger.getGlobal().log(Level.INFO, String.format("Client %d has to create Philosopher: %d,  Seats: %d, Ushers: %d",spec.getClientID(),spec.getNumberOfPhilosophers(),spec.getNumberOfSeats(),spec.getNumberOfUshers()));
                // init table
                table = new Table(spec.getNumberOfSeats(), spec.getNumberOfUshers());

                // create own agent
                clientAgent = new ClientAgent(table);
                Remote stubAgent = UnicastRemoteObject.exportObject(clientAgent, 0);
                registerAgent = (IRegister) Naming.lookup("//" + host + ":" + port + "/RegisterAgent");
                registerAgent.register(stubAgent, String.format("ClientAgent%d", id));

                sleep(5000);

                // get neighbor agent
                String neighborAddres = connectionAgent.getNeighborAgentAddres(id);
                IClientAgent neighborAgent = (IClientAgent) Naming.lookup(neighborAddres);

                // set remote seat
                Remote stubSeat = UnicastRemoteObject.exportObject(table.getLastSeat(), 0);
                neighborAgent.setRemoteSeat((IRemoteSeat) stubSeat);

                // create philosophers
                philosophers = new Philosopher[spec.getNumberOfPhilosophers()];
                for (int i = 0; i < philosophers.length; i++)
                {
                    philosophers[i] = new Philosopher(table, i);
                }

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
            catch (InterruptedException e)
            {

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
