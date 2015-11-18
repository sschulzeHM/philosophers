package vss.distributed.philosophers;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Supervisor extends Thread
{

    private static final long SLEEP_TIME = 3000;
    private final int MAX_MEALS;
    private Philosopher philosophers[];

    public Supervisor(Philosopher[] philosophers, int maxMeals)
    {
        this.philosophers = philosophers;
        MAX_MEALS = maxMeals;
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

            // set canEat on all philosophers
            Logger.getGlobal().log(Level.INFO, "Current Min Meals: " + min + ".");
            for (Philosopher phil : philosophers)
            {
                if (phil.getMeals() >= min + MAX_MEALS)
                {
                    phil.setCanEat(false);
                    phil.restrictNeeds();
                }
                else
                {
                    phil.setCanEat(true);
                    phil.allowNeeds();
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
}