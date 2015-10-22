package vss.parallel.philosophers;

import java.util.Arrays;

/**
 * Created by stefanschulze on 12.10.15.
 */
public class Table
{
    private boolean[] forkUsed;
    private Usher[] ushers;
    private Seat[] seats;

    public Table(int seats, int ushers)
    {
        initForks(seats);
        initSeats(seats);
        initUshers(seats, ushers);
    }

    private void initForks(int seats)
    {
        forkUsed = new boolean[seats];
        for (int i = 0; i < forkUsed.length; i++)
        {
            forkUsed[i] = false;
        }
    }

    private void initSeats(int seats)
    {
        this.seats = new Seat[seats];
        // create seats
        for (int i = 0; i < this.seats.length; i++)
        {
            this.seats[i] = new Seat(i, left(i), right(i));
        }
    }

    private void initUshers(int seats, int ushers)
    {
        int seatsPerUsher = seats / ushers;
        int firstSeat = 0;
        int lastSeat = 0;
        this.ushers = new Usher[ushers];

        // assign seats to manage
        for (int i = 0; i < this.ushers.length; i++)
        {
            lastSeat += seatsPerUsher - 1;
            // last usher gets remaining seats
            lastSeat = (lastSeat < this.seats.length && i == this.ushers.length - 1) ? this.seats.length - 1 : lastSeat;
            Seat[] managedSeats = Arrays.copyOfRange(this.seats, firstSeat, lastSeat);
            this.ushers[i] = new Usher(i, managedSeats, forkUsed);
            firstSeat = lastSeat + 1;
            lastSeat++;
        }
    }

    public Usher getUsher()
    {
        int someUsher = (int) (Math.random() * (ushers.length));
        if (someUsher == ushers.length)
        {
            someUsher--;
        }
        return ushers[someUsher];
    }


    private int left(int i)
    {
        if (i == 0)
        {
            return forkUsed.length - 1;
        }

        return i;
    }

    private int right(int i)
    {
        if (i + 1 < forkUsed.length)
        {
            return i + 1;
        }
        else
        {
            return 0;
        }
    }
}
