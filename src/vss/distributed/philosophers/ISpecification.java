package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 26.11.15.
 */
public interface ISpecification extends Remote
{
    int getNumberOfPhilosophers() throws RemoteException;

    int getNumberOfUshers() throws RemoteException;

    int getNumberOfSeats() throws RemoteException;

    int getClientID() throws RemoteException;
}
