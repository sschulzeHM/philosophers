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
    private boolean successOwnFork;
    private boolean successOtherFork;

    public Seat(int id)
    {
        this.id = id;
        available = false;
        successOwnFork = false;
        successOtherFork = false;
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

    public boolean takeRightFork()
    {
        synchronized (rightFork)
        {
            if (!rightFork.isAvailable())
            {
                return false;
            }

            rightFork.setUnavailable();
            return true;
        }
    }

    public void releaseRightFork()
    {
        rightFork.setAvailable();
    }

    public int getId()
    {
        return id;
    }

    public boolean take(boolean releaseAnyway)
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
            successOtherFork = leftNeighbor.takeRightFork();
            if (successOtherFork)
            {
                Logger.getGlobal().log(Level.INFO, "Seat " + getId() + " takes fork from neighbor " + leftNeighbor.getId() + ".");
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

    public void releaseForks()
    {
        releaseRightFork();
        leftNeighbor.releaseRightFork();

        successOwnFork = false;
        successOtherFork = false;
    }

    public void leave()
    {
        available = true;
    }
}
