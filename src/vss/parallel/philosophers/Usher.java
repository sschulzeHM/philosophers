package vss.parallel.philosophers;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Usher
{
    public static final int SEAT_UNAVAILABLE = -1;

    private final int id;
    private final Seat[] seats;
    private Seat nullSeat;

    public Usher(int id, Seat[] seats)
    {
        this.id = id;
        this.seats = seats;
    }

    public synchronized Seat getAvailableSeat(int id) throws InterruptedException
    {
        while (true)
        {
            for (Seat seat : seats)
            {
                if (seat.isAvailable())
                {
                    seat.setAvailable(false);
                    return seat;
                }
            }

            try {
            	Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " waiting for a seat.");
				wait();
				Logger.getGlobal().log(Level.INFO, "Philosopher " + id + " was awakened");
			} catch (InterruptedException e) {
				Logger.getGlobal().log(Level.INFO, "!!!!!!!!! a waiting philosoph is interrupted !!!!!!!!!!!");
				throw new InterruptedException();
			}
        }
        // never reached
    }

    public int getId()
    {
        return id;
    }
    
    //if a philosopher leaves the seat he has to wake up a waiting philosopher
    //it is impossible to leave a seat and get a seat parallel. For both you need the usher. PROBLEM??? 
    //--> Solution maybe: synchronize the seat, too
    public synchronized void leaveSeat(Seat seat){
    	seat.leave();
    	notify();
    }
}
