package vss.parallel.philosophers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Supervisor extends Thread
{

    private final Table table;
    private final int NUMBER_OF_PHILOSOPHERS;
    private final int NUMBER_OF_HUNGRY_PHILOSOPHERS;
    private HashMap<Philosopher, Integer> philosophers;

    public Supervisor(Table table, int numOfPhil, int numOfHungPhil)
    {
        this.table = table;
        NUMBER_OF_PHILOSOPHERS = numOfPhil;
        NUMBER_OF_HUNGRY_PHILOSOPHERS = numOfHungPhil;
        initializePhilosophers();
        startPhilosophers();
    }

    private void initializePhilosophers()
    {
        philosophers = new LinkedHashMap<>();
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++)
        {
            philosophers.put(new Philosopher(table, i), 0);
        }

        for (int i = 0; i < NUMBER_OF_HUNGRY_PHILOSOPHERS; i++)
        {
            philosophers.put(new HungryPhilosopher(table, NUMBER_OF_PHILOSOPHERS + i), 0);
        }
    }

    private void startPhilosophers()
    {
        Set<Philosopher> phils = philosophers.keySet();
        for (Philosopher philosopher : phils)
        {
            philosopher.start();
        }
    }

    public void run()
    {
        Set<Philosopher> phils = philosophers.keySet();
        int currentMinMeals = 0;
        int currentMeals = 0;
        while (true)
        {
            for (Philosopher philosopher : phils)
            {
                currentMeals = philosopher.getMeals();
                if (currentMeals < currentMinMeals)
                {
                    currentMinMeals = currentMeals;
                }
                if (currentMinMeals >= philosopher.MAX_MEALS_BEFORE_SLEEP)
                {
                    Logger.getGlobal().log(Level.INFO, philosopher.getOwnName() + " ate too much (" + philosopher.MAX_MEALS_BEFORE_SLEEP + " times). He has to leave the Table.");
                    philosopher.interrupt();
                }
            }
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
    }
}
