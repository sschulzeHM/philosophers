package vss.parallel.philosophers;

import vss.utils.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 12.10.15.
 */
public class Philosophers
{
    private static final int AVAILABLE_SEATS = 4;
    private static final int AVAILABLE_USHERS = 2;
    private static final int NUMBER_OF_PHILOSOPHERS = 10;

    public static void main(String[] args)
    {
        // Setup logging
        Logger.getGlobal().setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getGlobal().addHandler(consoleHandler);
        //Logger.getGlobal().setLevel(Level.OFF);

        // Log configuration
        Logger.getGlobal().log(Level.INFO, "::CONFIGURATION::");
        Logger.getGlobal().log(Level.INFO, "::Time are randomized");
        Logger.getGlobal().log(Level.INFO, "::Max Think Time: " + (Philosopher.EATTIME / 1000) % 60 + "s");
        Logger.getGlobal().log(Level.INFO, "::Max Eat Time: " + (Philosopher.THINKTIME / 1000) % 60 + "s");
        Logger.getGlobal().log(Level.INFO, "::Max Eat Sleep: " + (Philosopher.SLEEPTIME / 1000) % 60 + "s");
        Logger.getGlobal().log(Level.INFO, "::Max Meals: " + Philosopher.MAX_MEALS_BEFORE_SLEEP);
        Logger.getGlobal().log(Level.INFO, "::Available Seats: " + AVAILABLE_SEATS);
        Logger.getGlobal().log(Level.INFO, "::Available Ushers: " + AVAILABLE_USHERS);
        Logger.getGlobal().log(Level.INFO, "::Number Of Philosophers: " + NUMBER_OF_PHILOSOPHERS);
        Logger.getGlobal().log(Level.INFO, "");

        Table table = new Table(AVAILABLE_SEATS, AVAILABLE_USHERS);
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++)
        {
            new Philosopher(table, i);
        }
    }
}
