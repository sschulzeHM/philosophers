package vss.parallel.philosophers;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by stefanschulze on 05.11.15.
 */
public class Manager
{
    private HashMap<Philosopher, Integer> rations;

    public Manager()
    {
        rations = new LinkedHashMap<>();
    }

    public synchronized int getCurrentRationCount(Philosopher philosopher)
    {
        return rations.get(philosopher);
    }

    public synchronized void resetRationCount(Philosopher philosopher)
    {
        rations.put(philosopher, 0);
    }

    public synchronized void increaseRationCount(Philosopher philosopher)
    {
        int count;
        if (rations.get(philosopher) == null)
        {
            count = 0;
        }
        else
        {
            count = rations.get(philosopher);
        }

        rations.put(philosopher, count++);
    }

}
