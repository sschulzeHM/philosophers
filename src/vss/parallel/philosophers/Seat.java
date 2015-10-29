package vss.parallel.philosophers;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Seat
{
    private static final int REQUEST_MAX_COUNT = 3;
    private final int id;
    private Seat leftNeighbor;
    private Fork rightFork;
    private boolean available;

    public Seat(int id)
    {
        this.id = id;
        available = false;
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

    public synchronized boolean tryTakeFork()
    {
        if (!available)
        {
            return false;
        }

        rightFork.setUnavailable(1000);
        return true;
    }

    public synchronized boolean tryReleaseFork()
    {
        if (!available)
        {
            return false;
        }

        rightFork.setAvailable();
        return true;
    }

    private boolean takeRightFork()
    {
        if (!rightFork.isAvailable())
        {
            return false;
        }

        rightFork.setUnavailable(1000);
        return true;
    }

    private void releaseRightFork()
    {
        rightFork.setAvailable();
    }

    public int getId()
    {
        return id;
    }

    public boolean take()
    {
        boolean successOwnFork = false;
        boolean successOtherFork = false;
        int requestCount = 0;
        while (!successOwnFork || !successOtherFork)
        {
            if (!successOwnFork)
            {
                successOwnFork = takeRightFork();
                if (successOwnFork)
                {
                    Logger.getGlobal().log(Level.INFO, "Seat " + getId() + " takes fork " + rightFork.getId() + ".");
                }
            }
            if (!successOtherFork)
            {
                successOtherFork = leftNeighbor.tryTakeFork();
                if (successOtherFork)
                {
                    Logger.getGlobal().log(Level.INFO, "Seat " + getId() + " takes fork from neighbor " + leftNeighbor.getId() + ".");
                }
            }
            if (!successOwnFork || !successOtherFork)
            {
                requestCount++;
                if (requestCount > REQUEST_MAX_COUNT)
                {
                    Logger.getGlobal().log(Level.WARNING, "Seat " + getId() + " could not take both forks.");
                    return false;
                }
            }
        }
        return true;
    }

    public void leave()
    {
        available = true;
        leftNeighbor.tryReleaseFork();
        releaseRightFork();
    }
}
