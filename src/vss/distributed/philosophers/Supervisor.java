package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Supervisor extends Thread implements ILocalSuperVisor
{

    private static final long SLEEP_TIME = 3000;
    private final int MAX_MEALS;
    private Philosopher philosophers[];
    private AtomicInteger globalMin;

    public Supervisor(Philosopher[] philosophers, int maxMeals)
    {
        this.philosophers = philosophers;
        MAX_MEALS = maxMeals;
        globalMin = new AtomicInteger(Integer.MAX_VALUE);
    }

    public void run()
    {
        while (true)
        {
            int min = Integer.MAX_VALUE;
            int currentMeal = 0;

            // collect min meals from all philosophers
            for (Philosopher phil : philosophers)
            {
                currentMeal = phil.getMeals();
                if (currentMeal < min)
                {
                    min = currentMeal;
                }
            }

            if (globalMin.get() < min)
            {
                Logger.getGlobal().log(Level.WARNING, String.format("Local min %d exceeds global min %d! ", min, globalMin.get()));
                min = globalMin.get();
            }

            // set canEat on all philosophers
            Logger.getGlobal().log(Level.INFO, String.format("Current Min Meals: %d.", min));
            for (Philosopher phil : philosophers)
            {
                if (phil.getMeals() >= min + MAX_MEALS)
                {
                    phil.setCanEat(false);
                    //phil.restrictNeeds();
                }
                else
                {
                    phil.setCanEat(true);
                    //phil.allowNeeds();
                }
            }

            // sleep
            try
            {
                sleep(SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setGlobalMin(int min) throws RemoteException
    {
        //Logger.getGlobal().log(Level.INFO, String.format("New global min from ServerSupervisor: %d.", min));
        this.globalMin.set(min);
    }

    @Override
    public int getLocalMin() throws RemoteException
    {
        int min = Integer.MAX_VALUE;
        int currentMeal = 0;
        for (Philosopher phil : philosophers)
        {
            currentMeal = phil.getMeals();
            if (currentMeal < min)
            {
                min = currentMeal;
            }
        }
        return min;
    }
}