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
        Logger.getGlobal().setLevel(Level.WARNING);

        String myIP = getHostFromArgs(args, 2);
        int myPort = getPortFromArgs(args, 3);

        String serverIP = getHostFromArgs(args, 0);
        int serverPort = getPortFromArgs(args, 1);
        Logger.getGlobal().log(Level.WARNING, "Client running on " + myIP + ":" + myPort + ". Connecting to " + serverIP + ":" + serverPort);

        //IMPORTANT:
        System.setProperty("java.rmi.server.hostname", myIP);

        IConnectionAgent connectionAgent;
        String id;

        while (true)
        {
            try
            {
                //Logger.getGlobal().log(Level.WARNING,"Before Lookup ConnectionAgent");
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
        ILocalSuperVisor supervisor;
        IRegisterAgent serverSupervisor;
        while (true)
        {
            try
            {
                // get specification
                spec = (ISpecification) Naming.lookup("//" + serverIP + ":" + serverPort + "/ClientSpec" + id);
                Logger.getGlobal().log(Level.WARNING, String.format("Client %s has to create Philosopher: %d,  Seats: %d, Ushers: %d, Hungry Philosophers: %d, Max Meal Difference: %d", spec.getClientID(), spec.getNumberOfPhilosophers(), spec.getNumberOfSeats(), spec.getNumberOfUshers(), spec.getNumberOfHungryPhilosophers(), spec.getMaxMealDiff()));
                // init table
                table = new Table(spec.getNumberOfSeats(), spec.getNumberOfUshers());

                // create philosophers
                philosophers = new Philosopher[spec.getNumberOfPhilosophers() + spec.getNumberOfHungryPhilosophers()];
                for (int i = 0; i < philosophers.length; i++)
                {
                    philosophers[i] = new Philosopher(table, i);
                }
                for (int i = spec.getNumberOfPhilosophers(); i < philosophers.length; i++)
                {
                    philosophers[i] = new HungryPhilosopher(table, i);
                }

                // create supervisor
                supervisor = new Supervisor(philosophers, spec.getMaxMealDiff());


                // create and register own agent
                clientAgent = new ClientAgent(table, connectionAgent, id);
                Remote stubAgent = UnicastRemoteObject.exportObject(clientAgent, 0);
               // Logger.getGlobal().log(Level.WARNING,"Before Lookup RegisterAgent");
                registerAgent = (IRegisterAgent) Naming.lookup("//" + serverIP + ":" + serverPort + "/RegisterAgent");
               // Logger.getGlobal().log(Level.WARNING,"Before Lookup RegisterAgent");
                registerAgent.register(stubAgent, String.format("ClientAgent%s", id));
               // Logger.getGlobal().log(Level.WARNING,"After register at RegisterAgent");

                // register supervisor at ServerSupervisor
                Remote stubSupervisor = UnicastRemoteObject.exportObject(supervisor, 0);
               // Logger.getGlobal().log(Level.WARNING,"Before Lookup ServerSupervisor");
                serverSupervisor = (IRegisterAgent) Naming.lookup("//" + serverIP + ":" + serverPort + "/ServerSupervisor");
               // Logger.getGlobal().log(Level.WARNING,"After Lookup ServerSupervisor");
                serverSupervisor.register(stubSupervisor, String.format("Supervisor%s", id));
               // Logger.getGlobal().log(Level.WARNING,"After register at ServerSupervisor");

                // start philosophers
                for (Philosopher phil : philosophers)
                {
                    phil.start();
                }

                // start supervisor
                Supervisor superman = (Supervisor) supervisor;
                superman.start();


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
            catch (RemoteException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Server not available. Trying again in " + WAIT_TIME / 1000 + " seconds.");
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
