package vss.distributed.philosophers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class ClientAgent implements IClientAgent, IRegisterObject
{
    private Seat firstSeat;
    private Seat lastSeat;
    private LocalSeatAgent agent;

    public ClientAgent(Seat firstSeat, Seat lastSeat)
    {
        this.firstSeat = firstSeat;
        this.lastSeat = lastSeat;
    }

    @Override
    public void setRemoteSeat(IRemoteSeat remote) throws RemoteException
    {
        firstSeat.setLeftNeighbor(remote);
        // TODO localservice check periodically for remote exception and reset leftNeighbor to local
        agent = new LocalSeatAgent(firstSeat, lastSeat, remote);
        agent.start();
    }

    @Override
    public void receiveInfo(String message) throws RemoteException
    {

        System.out.println(message);
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
