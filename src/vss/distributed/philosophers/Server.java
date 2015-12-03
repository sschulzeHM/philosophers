package vss.distributed.philosophers;

import vss.utils.LogFormatter;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 18.11.15.
 */
public class Server extends HostApplication {

    public static void main(String[] args) throws RemoteException {
        // configure Logger
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);

        // parse arguments
        String ip = getHostFromArgs(args, 0);
        int port = getPortFromArgs(args, 1);
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry();

        // create remote objects
        IRemoteLogger remoteLogger = new RemoteLogger(Logger.getGlobal());
        Remote stubLogger = UnicastRemoteObject.exportObject(remoteLogger, port);
        registry.rebind("ServerRemoteLogger", stubLogger);

        IRegisterAgent registerAgent = new RegisterAgent(registry);
        Remote stubRegisterAgent = UnicastRemoteObject.exportObject(registerAgent, port);
        registry.rebind("RegisterAgent", stubRegisterAgent);

        IConnectionAgent connectionAgent = new ConnectionAgent(registry, ip, port);
        Remote stubConnectionAgent = UnicastRemoteObject.exportObject(connectionAgent, port);
        registry.rebind("ConnectionAgent", stubConnectionAgent);

        IRegisterAgent serverSupervisor = new ServerSupervisor(registry, ip, port);
        Remote stubServerSupervisor = UnicastRemoteObject.exportObject(serverSupervisor, port);
        registry.rebind("ServerSupervisor", stubServerSupervisor);

        remoteLogger.logInfo("Server online...");

        Scanner scanner = new Scanner(System.in);
        int inputSeatCount = 0;
        int inputClientNumber = 0;
        int inputAfterSeatID = 0;
        IClientAgent clientAgent;
        List<String> clients;
        int numOfClients = 0;

        // accept input for insertion at clients
        while (!interrupted()) {
            try
            {
                numOfClients = ((ConnectionAgent) connectionAgent).getNumOfClients();

                if (numOfClients >= 1)
                {
                    System.out.println("Wie viele Seats sollen eingfuegt werden? Anzahl eingeben:  ");
                    inputSeatCount = scanner.nextInt();
                    while (inputSeatCount <= 0)
                    {
                        System.out.println("Die Anzahl der Seats muss groesser 0 sein. Erneut Anzahl eingeben:  ");
                        inputSeatCount = scanner.nextInt();
                    }

                    System.out.println(String.format("Auf welchem Client soll eingefügt werden? Client# eingeben (Range: 0 - %d): ", (numOfClients)));
                    inputClientNumber = scanner.nextInt();
                    while (inputClientNumber > numOfClients || inputClientNumber < 0)
                    {
                        Logger.getGlobal().log(Level.INFO, String.format("Eine ungueltige Client# wurde eingegeben. Client# erneut eingeben (Range: 0 - %d): ", (numOfClients)));
                        inputClientNumber = scanner.nextInt();
                    }

                    System.out.println("Nach welchem Seat soll eingefuegt werden? SeatID eingeben: ");
                    inputAfterSeatID = scanner.nextInt();
                    while (inputAfterSeatID < 0)
                    {
                        System.out.println("Die SeatID muss groesser oder gleich 0 sein. SeatID erneut eingeben: ");
                        inputAfterSeatID = scanner.nextInt();
                    }

                    clients = ((RegisterAgent) registerAgent).getClientAgents();
                    for (String client : clients)
                    {
                        String clientID = ((ConnectionAgent) connectionAgent).getClient(inputClientNumber);
                        if (client.contains(clientID))
                        {
                            try
                            {
                                clientAgent = (IClientAgent) registry.lookup(client);
                            }
                            catch (NotBoundException e)
                            {
                                remoteLogger.logError(client + " was not found in registry!");
                                break;
                            }
                            try
                            {
                                clientAgent.insertSeats(inputSeatCount, inputAfterSeatID);
                            }
                            catch (ConnectException e)
                            {
                                remoteLogger.logError(client + " is not available!");
                                break;
                            }
                        }
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                break;
            }

        }

    }
}
