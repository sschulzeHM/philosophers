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
    private static final int NUMBER_OF_PHILOSOPHERS = 5;
    private static final int NUMBER_OF_HUNGRY_PHILOSOPHERS = 3;
    
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
        Logger.getGlobal().log(Level.INFO, "::Available Seats: " + AVAILABLE_SEATS);
        Logger.getGlobal().log(Level.INFO, "::Available Ushers: " + AVAILABLE_USHERS);
        Logger.getGlobal().log(Level.INFO, "::Number Of Philosophers: " + NUMBER_OF_PHILOSOPHERS);
        Logger.getGlobal().log(Level.INFO, "::Number Of Hungry Philosophers: " + NUMBER_OF_HUNGRY_PHILOSOPHERS);
        Logger.getGlobal().log(Level.INFO, "");

        Table table = new Table(AVAILABLE_SEATS, AVAILABLE_USHERS);
        
        
        new Supervisor(table,NUMBER_OF_PHILOSOPHERS,NUMBER_OF_HUNGRY_PHILOSOPHERS).start();
    }
}
