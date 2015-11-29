package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Seat implements IRemoteSeat
{
    private static final int REQUEST_MAX_COUNT = 3;
    private final int id;
    private IRemoteSeat leftNeighbor;
    private Fork rightFork;
    private boolean available;
    private boolean successOwnFork;
    private boolean successOtherFork;
    public Seat(int id)
    {
        this.id = id;
        available = false;
        successOwnFork = false;
        successOtherFork = false;
    }

    public void setLeftNeighbor(IRemoteSeat leftNeighbor)
    {
        this.leftNeighbor = leftNeighbor;
    }

    public void initialize(Seat leftNeighbor, Fork rightFork)
    {
        this.leftNeighbor = leftNeighbor;
        this.rightFork = rightFork;
        available = true;
    }

    // not synchronized because only one usher object is managing this instance
    public boolean isAvailable()
    {
        return available;
    }

    // not synchronized because only one usher object is managing this instance
    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    //syncronized because 2 seats can call this method
    public boolean takeRightFork() throws RemoteException
    {
        return rightFork.take();
    }

    public void releaseRightFork() throws RemoteException
    {
        rightFork.setAvailable();
    }

    public int getId() throws RemoteException
    {
        return id;
    }

    public boolean take(boolean releaseAnyway, String philName) throws RemoteException
    {
        if (!successOwnFork)
        {
            successOwnFork = takeRightFork();
            if (successOwnFork)
            {
                Logger.getGlobal().log(Level.INFO, philName + " at Seat " + getId() + " takes fork " + rightFork.getId() + ".");
            }
        }
        if (!successOtherFork)
        {
            successOtherFork = leftNeighbor.takeRightFork();
            if (successOtherFork)
            {
                Logger.getGlobal().log(Level.INFO, philName + " at Seat " + getId() + " takes fork from neighbor Seat " + leftNeighbor.getId() + ".");
            }
        }

        if ((!successOwnFork || !successOtherFork) && releaseAnyway)
        {
            if (successOwnFork)
            {
                releaseRightFork();
                successOwnFork = false;
            }
            if (successOtherFork)
            {
                leftNeighbor.releaseRightFork();
                successOtherFork = false;
            }
        }

        return successOtherFork && successOwnFork;
    }

    public void releaseForks() throws RemoteException
    {
        releaseRightFork();
        successOwnFork = false;

        leftNeighbor.releaseRightFork();
        successOtherFork = false;
    }

    public void leave()
    {
        Logger.getGlobal().log(Level.INFO, "Seat " + id + " left");
        available = true;
    }

    public void releaseOwnedForks() throws RemoteException
    {
        if (successOwnFork)
        {
            releaseRightFork();
            successOwnFork = false;
        }

        if (successOtherFork)
        {
            leftNeighbor.releaseRightFork();
            successOtherFork = false;
        }
    }

    public void resetOtherSuccess()
    {
        successOtherFork = false;
    }
}
