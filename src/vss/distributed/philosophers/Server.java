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

        System.setProperty("java.rmi.server.hostname", ip);

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

        ((ServerSupervisor) serverSupervisor).startSupervising();

        remoteLogger.logInfo("Server online...");

        Scanner scanner = new Scanner(System.in);
        int inputSeatCount = 0;
        int inputClientNumber = 0;
        int inputSeatID = 0;
        IClientAgent clientAgent;
        List<String> clients;
        int numOfClients = 0;
        boolean before;
        String clientID;
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
                        Logger.getGlobal().log(Level.WARNING, "Die Anzahl der Seats muss groesser 0 sein.");
                        System.out.println("Anzahl erneut eingeben:  ");
                        inputSeatCount = scanner.nextInt();
                    }

                    System.out.println(String.format("Auf welchem Client soll eingefügt werden? ClientID eingeben (Range: 0 - %d): ", (numOfClients - 1)));
                    inputClientNumber = scanner.nextInt();
                    while (inputClientNumber >= numOfClients || inputClientNumber < 0)
                    {
                        Logger.getGlobal().log(Level.WARNING, "Eine ungueltige ClientID wurde eingegeben.");
                        System.out.println(String.format("ClientID erneut eingeben (Range: 0 - %d): ", (numOfClients - 1)));
                        inputClientNumber = scanner.nextInt();
                    }

                    System.out.println("Soll vor oder nach einem Seat eingefuegt werden? Eingabe (v = davor, h = dahinter): ");
                    String c = scanner.next();
                    boolean correctEntry = false;
                    if (c.equalsIgnoreCase("v") || c.equalsIgnoreCase("h"))
                    {
                        correctEntry = true;
                    }
                    while (!correctEntry)
                    {
                        Logger.getGlobal().log(Level.WARNING, "Eine ungueltiges Zeichen wurde eingegeben.");
                        System.out.println("Zeichen erneut eingeben (v = davor, h = dahinter): ");
                        c = scanner.next();
                        if (c.equalsIgnoreCase("v") || c.equalsIgnoreCase("h"))
                        {
                            correctEntry = true;
                        }
                    }

                    if (c.equalsIgnoreCase("v"))
                    {
                        System.out.println("SeatID eingeben vor der eingefuegt werden soll. Eingabe: ");
                        before = true;
                    }
                    else
                    {
                        System.out.println("SeatID eingeben nach der eingefuegt werden soll. Eingabe: ");
                        before = false;
                    }

                    inputSeatID = scanner.nextInt();

                    while (inputSeatID < 0)
                    {
                        Logger.getGlobal().log(Level.WARNING, "Die SeatID muss groesser oder gleich 0 sein.");
                        System.out.println("SeatID erneut eingeben: ");
                        inputSeatID = scanner.nextInt();
                    }

                    clients = ((RegisterAgent) registerAgent).getClientAgents();

                    for (String client : clients)
                    {
                        clientID = ((ConnectionAgent) connectionAgent).getClient(inputClientNumber);
                        if (clientID != null && client.contains(clientID))
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
                                clientAgent.insertSeats(before, inputSeatCount, inputSeatID);
                                new InsertWaitThread(clientAgent, client).start();
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
