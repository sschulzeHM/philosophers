package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientAgent extends Remote
{
    void setRemoteSeat(IRemoteSeat remote) throws RemoteException;

    void update() throws RemoteException;
    void insertSeats(int countSeats, int afterSeat) throws RemoteException;

    boolean isAlive() throws RemoteException;
}
