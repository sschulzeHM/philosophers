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
	private static final int MAX_TRIES = 3;
	public static int MAX_MEALS_BEFORE_GO;

	private final Table table;
	private final int id;
	private int mealcounter;

	private Seat seat;
	private Usher usher;

	public Philosopher(Table table, int id)
	{
		this.table = table;
		this.id = id;
		mealcounter = 0;
	}

	public void run()
	{
		while(true){


			while (!Thread.interrupted())
			{
				think();
				usher = table.getUsher();
				Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " is served by usher " + usher.getId() + ".");
				try {
					seat = usher.getAvailableSeat(id);
				} catch (InterruptedException e1) {
					Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " is interrupted.");
					break;
				}
				Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " receives seat " + seat.getId() + ".");

				
				try{
					
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
							throw new InterruptedException();
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

					if(success)
					{
						eat(id, seat.getId());
						seat.releaseForks();
					}

					usher.leaveSeat(seat);
					if (requestCount >= MAX_TRIES)
					{
						Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " could not eat at seat " + seat.getId() + ".");
					}
				}
				catch(InterruptedException e){
					seat.releaseOwnedForks();
					usher.leaveSeat(seat);
					break;
				}
			}
		}

	}

	private void eat(int philosopherID, int seatID)
	{
		Logger.getGlobal().log(Level.INFO, "Philosopher " + philosopherID + " eats at seat " + seatID + "." + " Meal: "+ mealcounter);
		try
		{
			sleep((int) (Math.random() * EATTIME));
		}
		catch (InterruptedException e)
		{
			Logger.getGlobal().log(Level.WARNING, "Philosopher " + philosopherID + " interrupted while eating at seat " + seatID + ".");
			mealcounter = 0;
			seat.releaseForks();
			usher.leaveSeat(seat);

		}
		Logger.getGlobal().log(Level.INFO, "Philosopher " + philosopherID + " stops eating at seat " + seatID + ".");

		mealcounter++;

	}

	private void think()
	{
		Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " thinks.");
		try
		{
			sleep((int) ( THINKTIME));
		}
		catch (InterruptedException e)
		{
			Logger.getGlobal().log(Level.WARNING, "Philosopher " + id + " interrupted while thinking.");
		}
	}

	public int getMeals() {
		return mealcounter;
	}
}
