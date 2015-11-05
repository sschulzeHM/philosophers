package vss.parallel.philosophers;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 14.10.15.
 * <p>
 * FEATURES:
 * - No fixed seats.
 * - Meal counter with sleep state after maximum number of meals.
 * - Separate eat, think and sleep times and max meals count.
 */
public class Philosopher extends Thread
{
    protected static double EATTIME = 1000;
    protected static double THINKTIME = 2500;
    protected static double SLEEPTIME = 5000;
    protected static int MAX_MEALS_BEFORE_SLEEP = 3;
    protected static int MAX_TRIES = 3;

    private final Table table;
    private final int id;
    private int mealcounter;

    public Philosopher(Table table, int id)
    {
        this.table = table;
        this.id = id;
        mealcounter = 0;
        start();
    }

    public void run()
    {
        while (!Thread.interrupted())
        {
            think();
            Usher usher = table.getUsher();
            Logger.getGlobal().log(Level.INFO, getOwnName() + " is served by usher " + usher.getId() + ".");
            Seat seat = usher.getAvailableSeat();
            Logger.getGlobal().log(Level.INFO, getOwnName() + " receives seat " + seat.getId() + ".");

            boolean success = seat.take(false);
            int requestCount = 0;
            while (!success && requestCount <= MAX_TRIES)
            {
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                }

                if (requestCount >= MAX_TRIES)
                {
                    success = seat.take(true);
                }
                else
                {
                    success = seat.take(false);
                }

                requestCount++;
            }

            if (success)
            {
                eat(id, seat.getId());
                seat.releaseForks();
            }

            seat.leave();
            if (requestCount >= MAX_TRIES)
            {
                Logger.getGlobal().log(Level.WARNING, getOwnName() + " could not eat at seat " + seat.getId() + ".");
            }
        }
    }

    private void eat(int id, int seat)
    {
        Logger.getGlobal().log(Level.INFO, getOwnName() + " eats at seat " + seat + ".");
        try
        {
            sleep((int) (Math.random() * EATTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, getOwnName() + " interrupted while eating at seat " + seat + ".");
        }
        Logger.getGlobal().log(Level.INFO, getOwnName() + " stops eating at seat " + seat + ".");

        mealcounter++;
        if (mealcounter >= MAX_MEALS_BEFORE_SLEEP)
        {
            Logger.getGlobal().log(Level.INFO, getOwnName() + " ate too much (" + MAX_MEALS_BEFORE_SLEEP + " times). Start sleeping.");
            try
            {
                sleep((int) (Math.random() * SLEEPTIME));
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().log(Level.WARNING, getOwnName() + " interrupted while sleeping.");
            }
            Logger.getGlobal().log(Level.INFO, getOwnName() + " awakes.");
            mealcounter = 0;
        }
    }

    private void think()
    {
        Logger.getGlobal().log(Level.INFO, getOwnName() + " thinks.");
        try
        {
            sleep((int) (Math.random() * THINKTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, getOwnName() + " interrupted while thinking.");
        }
    }

    protected String getOwnName()
    {
        return "Philosopher " + id;
    }
}
