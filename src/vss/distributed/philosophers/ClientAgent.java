package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class ClientAgent implements IClientAgent, IRegisterObject
{
    private Table table;
    private LocalSeatAgent agent;

    public ClientAgent(Table table)
    {
        this.table = table;
    }

    @Override
    public void setRemoteSeat(IRemoteSeat remote) throws RemoteException
    {
        table.getFirstSeat().setLeftNeighbor(remote);
        agent = new LocalSeatAgent(table.getFirstSeat(), table.getLastSeat(), remote);
        agent.start();
    }

    //Do we need that anymore???
    @Override
    public void receiveInfo(String message) throws RemoteException
    {
        System.out.println(message);
    }

    @Override
    public void insertSeats(int countSeats, int afterSeat) throws RemoteException {
        table.insertSeats(countSeats,afterSeat);
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
