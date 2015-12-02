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
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
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

        Scanner scanner = new Scanner(System.in);
        int inputSeatCount = 0;
        int inputClientID = 0;
        int inputAfterSeatID = 0;
        IClientAgent clientAgent;
        List<String> clients;
        int numOfClients = 0;

        while (!interrupted()) {
            numOfClients = ((ConnectionAgent) connectionAgent).getNumOfClients();

            if (numOfClients >= 1) {
                System.out.println("Wie viele Seats sollen eingfügt werden? Anzahl eingeben:  ");
                inputSeatCount = scanner.nextInt();
                while (inputSeatCount <= 0) {
                    System.out.println("Die Anzahl der Seats muss größer 0 sein. Erneut Anzahl eingeben:  ");
                    inputSeatCount = scanner.nextInt();
                }

                System.out.println(String.format("Auf welchem Client soll eingefügt werden? ClientID eingeben (Range: 1 - %d): ", (numOfClients)));
                inputClientID = scanner.nextInt();
                while (inputClientID > numOfClients || inputClientID <= 0) {
                    System.out.println(String.format("Eine ungültige ClientID wurde eingegeben. ClientID erneut eingeben (Range: 1 - %d): ", (numOfClients)));
                    inputClientID = scanner.nextInt();
                }

                System.out.println("Nach welchem Seat soll eingefügt werden? SeatID eingeben: ");
                inputAfterSeatID = scanner.nextInt();
                while (inputAfterSeatID < 0) {
                    System.out.println("Die SeatID muss größer oder gleich 0 sein. SeatID erneut eingeben: ");
                    inputAfterSeatID = scanner.nextInt();
                }

                clients = ((RegisterAgent) registerAgent).getConnectedClients();
                for (String client : clients) {
                    if (client.contains(Integer.toString(inputClientID))) {
                        try {
                            clientAgent = (IClientAgent) registry.lookup(client);
                        } catch (NotBoundException e) {
                            remoteLogger.logError(client + " was'n found in registry");
                            break;
                        }
                        try {
                            clientAgent.insertSeats(inputSeatCount, inputAfterSeatID);
                        } catch (ConnectException e) {
                            remoteLogger.logError(client + " is'n available");
                            break;
                        }
                    }
                }
            }
        }

    }
}
