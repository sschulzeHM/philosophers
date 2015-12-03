package vss.distributed.philosophers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAgent implements IClientAgent, IRegisterObject
{
    private final IConnectionAgent connectionAgent;
    private final String clientID;
    private Table table;
    private LocalSeatAgent agent;

    public ClientAgent(Table table, IConnectionAgent connectionAgent, String clientID)
    {
        this.table = table;
        this.connectionAgent = connectionAgent;
        this.clientID = clientID;
    }

    @Override
    public void setRemoteSeat(IRemoteSeat remote) throws RemoteException
    {
        // stop local table
        table.stop();

        table.getFirstSeat().setLeftNeighbor(remote);
        Logger.getGlobal().log(Level.WARNING, String.format("Client %s setting remote seat %d", clientID, remote.getId()));
        table.continueRunning();
        agent = new LocalSeatAgent(table, remote);
        agent.start();

    }

    @Override
    public void update() throws RemoteException
    {
        // get neighbor agent
        String neighborAddress = connectionAgent.getNeighborAgentAddress(clientID);
        IClientAgent neighborAgent = null;
        try
        {
            neighborAgent = (IClientAgent) Naming.lookup(neighborAddress);
        }
        catch (NotBoundException e)
        {
            Logger.getGlobal().log(Level.WARNING, String.format("Neighbor %s not available", neighborAddress));
            return;
        }
        catch (MalformedURLException e)
        {
            Logger.getGlobal().log(Level.WARNING, String.format("Neighbor %s not available", neighborAddress));
            return;
        }

        // local to global
        // set remote seat
        Remote stubSeat = UnicastRemoteObject.exportObject(table.getLastSeat(), 0);
        neighborAgent.setRemoteSeat((IRemoteSeat) stubSeat);
    }

    @Override
    public void insertSeats(boolean before, int countSeats, int seatID) throws RemoteException
    {
        table.insertSeats(before, countSeats, seatID);
    }

    @Override
    public boolean isAlive() throws RemoteException
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "ClientAgent";
    }

    @Override
    public Remote getObject()
    {
        return this;
    }


}
