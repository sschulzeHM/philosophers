package vss.parallel.philosophers;

/**
 * Created by stefanschulze on 12.10.15.
 */
public class Table
{
    public static final int FORK_UNAVAILABLE = -1;

    private boolean[] forkUsed;

    private int left(int i)
    {
        return i;
    }

    private int right(int i)
    {
        if (i + 1 < forkUsed.length)
        {
            return i + 1;
        }
        else
        {
            return 0;
        }
    }

    public Table(int seats)
    {
        forkUsed = new boolean[seats];
        for (int i = 0; i < forkUsed.length; i++)
        {
            forkUsed[i] = false;
        }
    }

    public synchronized int getAvailableSeat()
    {
        int availableSeat = FORK_UNAVAILABLE;
        while (availableSeat == FORK_UNAVAILABLE) {
            for (int i = 0; i < forkUsed.length; i++)
            {
                if (!forkUsed[left(i)] && !forkUsed[right(i)])
                {
                    return i;
                }
            }
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }


        return FORK_UNAVAILABLE;
    }

    public synchronized void takeSeat(int number)
    {
        while (forkUsed[left(number)] || forkUsed[right(number)])
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        forkUsed[left(number)] = true;
        forkUsed[right(number)] = true;
    }

    public synchronized void leaveSeat(int number)
    {
        forkUsed[left(number)] = false;
        forkUsed[right(number)] = false;
        notifyAll();
    }
}
