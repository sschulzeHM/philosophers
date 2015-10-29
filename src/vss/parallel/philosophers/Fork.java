package vss.parallel.philosophers;

/**
 * Created by stefanschulze on 29.10.15.
 */
public class Fork
{
    private final int id;
    private boolean available;

    public Fork(boolean available, int id)
    {
        this.available = available;
        this.id = id;
    }

    public synchronized boolean isAvailable()
    {
        return available;
    }

    public synchronized void setUnavailable(int timeout)
    {
        int maxTries = 3;
        int tries = 0;
        while (!isAvailable() && tries <= maxTries)
        {
            try
            {
                wait(timeout);
            }
            catch (InterruptedException e)
            {
            }
        }
        if (tries <= maxTries)
        {
            return;
        }

        this.available = false;
    }

    public synchronized void setAvailable()
    {
        this.available = true;
        notifyAll();
    }

    public int getId()
    {
        return id;
    }

}