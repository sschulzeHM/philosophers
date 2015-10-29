package vss.parallel.philosophers;

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

    public synchronized Seat getAvailableSeat()
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
            try
            {
                // TODO back to table
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        // never reached
    }

    public int getId()
    {
        return id;
    }
}
