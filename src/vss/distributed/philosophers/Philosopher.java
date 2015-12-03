package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 14.10.15.
 * <p>
 */
public class Philosopher extends Thread
{
    private final Table table;
    private final int id;
    public double EATTIME = 2000;
    public double THINKTIME = 4000;
    public double SLEEPTIME = 6000;
    public int MAX_TRIES = 3;
    private int mealcounter;
    private boolean canEat;

    private Seat seat;
    private Usher usher;

    public Philosopher(Table table, int id)
    {
        this.table = table;
        this.canEat = true;
        this.id = id;
        this.mealcounter = 0;
    }

    public void run()
    {
        while (true)
        {
            while (!Thread.interrupted())
            {
                think();
                if (!canEat)
                {
                    Logger.getGlobal().log(Level.INFO, getOwnName() + " can't eat. Meals " + mealcounter);
                    break;
                }
                usher = table.getUsher();
                Logger.getGlobal().log(Level.INFO, getOwnName() + " is served by usher " + usher.getId() + ".");

                seat = usher.getAvailableSeat(getOwnName());

                try
                {
                    Logger.getGlobal().log(Level.INFO, getOwnName() + " receives seat " + seat.getId() + ".");

                    boolean success = seat.take(false, getOwnName());
                    int requestCount = 0;

                    while (!success && requestCount <= MAX_TRIES)
                    {
                        try
                        {
                            sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                            throw new InterruptedException();
                        }

                        if (requestCount >= MAX_TRIES)
                        {
                            success = seat.take(true, getOwnName());
                        }
                        else
                        {
                            success = seat.take(false, getOwnName());
                        }
                        requestCount++;
                    }

                    if (success)
                    {
                        eat(id, seat.getId());
                        seat.releaseForks();
                    }

                    usher.leaveSeat(seat);
                    if (requestCount >= MAX_TRIES)
                    {
                        Logger.getGlobal().log(Level.WARNING, getOwnName() + " could not eat at seat " + seat.getId() + ".");
                    }
                }
                catch (InterruptedException e)
                {
                    try
                    {
                        seat.releaseOwnedForks();
                    }
                    catch (RemoteException e1)
                    {
                        usher.leaveSeat(seat);
                        break;
                    }

                    usher.leaveSeat(seat);
                    break;
                }
                catch (RemoteException e)
                {
                    try
                    {
                        seat.resetOtherSuccess();
                        Logger.getGlobal().log(Level.WARNING, getOwnName() + " could not eat at seat " + seat.getId() + ". Remote seat not available.");
                        seat.releaseOwnedForks();
                    }
                    catch (RemoteException e1)
                    {
                        usher.leaveSeat(seat);
                        break;
                    }

                    usher.leaveSeat(seat);
                    break;
                }
            }
        }

    }

    private void eat(int philosopherID, int seatID)
    {
        Logger.getGlobal().log(Level.WARNING, getOwnName() + " eats at seat " + seatID + "." + " Meal: " + (mealcounter + 1));
        try
        {
            sleep((int) (EATTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, getOwnName() + " interrupted while eating at seat " + seatID + ".");
            try
            {
                seat.releaseForks();
            }
            catch (RemoteException e1)
            {
                usher.leaveSeat(seat);
            }
            usher.leaveSeat(seat);

        }
        Logger.getGlobal().log(Level.INFO, getOwnName() + " stops eating at seat " + seatID + ".");

        mealcounter++;

    }

    private void think()
    {
        Logger.getGlobal().log(Level.INFO, getOwnName() + " thinks.");
        try
        {
            sleep((int) (THINKTIME));
        }
        catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, getOwnName() + " interrupted while thinking.");
        }
    }

    public int getMeals()
    {
        return mealcounter;
    }

    protected String getOwnName()
    {
        return String.format("Philosopher %d", id);
    }

    public void setCanEat(boolean canEat)
    {
        this.canEat = canEat;
    }

    public synchronized void restrictNeeds()
    {
        setPriority(Thread.MIN_PRIORITY);
    }

    public synchronized void allowNeeds()
    {
        setPriority(Thread.NORM_PRIORITY);
    }
}
