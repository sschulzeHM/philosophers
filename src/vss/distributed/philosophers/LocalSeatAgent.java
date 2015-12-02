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
    private final Table table;
    private IRemoteSeat remoteSeat;

    public LocalSeatAgent(Table table, IRemoteSeat remoteSeat)
    {
        this.table = table;
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
                while (table.stop())
                {
                }

                try
                {
                    table.getLastSeat().releaseRightFork();
                    Logger.getGlobal().log(Level.INFO, "Reconnecting local seat " + table.getFirstSeat().getId() + " and seat " + table.getLastSeat().getId());
                }
                catch (RemoteException e1)
                {
                    Logger.getGlobal().log(Level.INFO, "Remote exception during reconnection.");
                }

                table.getFirstSeat().setLeftNeighbor(table.getLastSeat());
                table.continueRunning();
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
