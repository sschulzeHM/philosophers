package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Local agent on client side. Check if connection to remote client is alive.
 * Change from remote execution mode to local execution mode if connection was disconnected.
 */
public class LocalSeatAgent extends Thread
{
    private static final long SLEEP_TIME = 3000;
    private Seat firstSeat;
    private Seat lastSeat;
    private IRemoteSeat remoteSeat;

    public LocalSeatAgent(Seat firstSeat, Seat lastSeat, IRemoteSeat remoteSeat)
    {
        this.firstSeat = firstSeat;
        this.lastSeat = lastSeat;
        this.remoteSeat = remoteSeat;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                // check if remote seat is alive
                remoteSeat.getId();
            }
            catch (RemoteException e)
            {
                try
                {
                    Logger.getGlobal().log(Level.INFO, "Reconnecting local seat " + firstSeat.getId() + " and seat " + lastSeat.getId());
                }
                catch (RemoteException e1)
                {
                }
                firstSeat.setLeftNeighbor(lastSeat);
                break;
            }
            try
            {
                sleep(SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
            }
        }

    }
}
