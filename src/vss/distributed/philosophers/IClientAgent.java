package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientAgent extends Remote
{
    void setRemoteSeat(IRemoteSeat remote) throws RemoteException;

    void update() throws RemoteException;

    void insertSeats(boolean before, int countSeats, int seatID) throws RemoteException;

    boolean isAlive() throws RemoteException;

    boolean isInsertSeatsDone() throws RemoteException;
}
