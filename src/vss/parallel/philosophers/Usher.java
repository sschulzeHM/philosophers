package vss.parallel.philosophers;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Usher
{
    public static final int SEAT_UNAVAILABLE = -1;

    private final int id;
    private final Seat[] seats;
    private final boolean[] forkUsed;
    private Seat nullSeat;

    public Usher(int id, Seat[] seats, boolean[] forkUsed)
    {
        this.id = id;
        this.seats = seats;
        this.forkUsed = forkUsed;
    }

    public synchronized Seat getAvailableSeat()
    {
        int availableSeat = SEAT_UNAVAILABLE;
        while (availableSeat == SEAT_UNAVAILABLE)
        {
            for (Seat seat : seats)
            {
                if (!forkUsed[seat.getLeftNeighbor()] && !forkUsed[seat.getRightNeighbor()])
                {
                    availableSeat = seat.getId();
                    return seat;
                }
            }
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        return getNullSeat();
    }


    public synchronized void takeSeat(Seat seat)
    {
        while (forkUsed[seat.getLeftNeighbor()] || forkUsed[seat.getRightNeighbor()])
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        forkUsed[seat.getLeftNeighbor()] = true;
        forkUsed[seat.getRightNeighbor()] = true;
    }

    public synchronized void leaveSeat(Seat seat)
    {
        forkUsed[seat.getLeftNeighbor()] = false;
        forkUsed[seat.getRightNeighbor()] = false;
        notifyAll();
    }

    public int getId()
    {
        return id;
    }

    private Seat getNullSeat()
    {
        if (this.nullSeat == null)
        {
            this.nullSeat = new Seat(SEAT_UNAVAILABLE, SEAT_UNAVAILABLE, SEAT_UNAVAILABLE);
        }
        return nullSeat;
    }
}
