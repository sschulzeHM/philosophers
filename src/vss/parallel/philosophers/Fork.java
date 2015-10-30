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

    public synchronized void setUnavailable()
    {
        this.available = false;
    }

    public synchronized void setAvailable()
    {
        this.available = true;
    }

    public int getId()
    {
        return id;
    }

}