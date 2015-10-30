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
    public static final double EATTIME = 1000;
    public static final double THINKTIME = 2500;
    public static final double SLEEPTIME = 5000;
    public static final int MAX_MEALS_BEFORE_SLEEP = 3;
    private static final int MAX_TRIES = 3;

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
            Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " is served by usher " + usher.getId() + ".");
            Seat seat = usher.getAvailableSeat();
            Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " receives seat " + seat.getId() + ".");

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
                Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " could not eat at seat " + seat.getId() + ".");
            }
        }
    }

    private void eat(int id, int seat)
    {
        Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " eats at seat " + seat + ".");
        try
        {
            sleep((int) (Math.random() * EATTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " interrupted while eating at seat " + seat + ".");
        }
        Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " stops eating at seat " + seat + ".");

        mealcounter++;
        if (mealcounter >= MAX_MEALS_BEFORE_SLEEP)
        {
            Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " ate too much (" + MAX_MEALS_BEFORE_SLEEP + " times). Start sleeping.");
            try
            {
                sleep((int) (Math.random() * SLEEPTIME));
            }
            catch (InterruptedException e)
            {
                Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " interrupted while sleeping.");
            }
            Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " awakes.");
            mealcounter = 0;
        }
    }

    private void think()
    {
        Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " thinks.");
        try
        {
            sleep((int) (Math.random() * THINKTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " interrupted while thinking.");
        }
    }
}
