package vss.distributed.philosophers;

import java.util.Arrays;

/**
 * Created by stefanschulze on 12.10.15.
 */
public class Table
{
    private Usher[] ushers;
    private Seat[] seats;

    public Table(int seats, int ushers)
    {
        initSeats(seats);
        initUshers(seats, ushers);
    }

    private void initSeats(int seats)
    {
        this.seats = new Seat[seats];
        // create seats
        for (int i = 0; i < this.seats.length; i++)
        {
            this.seats[i] = new Seat(i);
        }
        // init created seats
        for (int i = 0; i < this.seats.length; i++)
        {
            this.seats[i].initialize(getLeftSeat(i), new Fork(true, getRightForkID(i)));
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
            Seat[] managedSeats = Arrays.copyOfRange(this.seats, firstSeat, lastSeat + 1); //copy lastSeat exclusive therefore + 1
            this.ushers[i] = new Usher(i, managedSeats);
            firstSeat = lastSeat + 1;
            lastSeat++;
        }
    }

    public Usher getUsher()
    {
        int someUsher = ((int) (Math.random() * 10)) % ushers.length;
        return ushers[someUsher];
    }


    private Seat getLeftSeat(int i)
    {
        if (i == 0)
        {
            // TODO RMI SEAT
            return seats[seats.length - 1];
        }

        return seats[i - 1];
    }

    private int getRightForkID(int i)
    {
        return i;
    }

    public Seat getFirstSeat()
    {
        if (seats.length < 1)
        {
            return null;
        }

        return seats[0];
    }

    public Seat getLastSeat()
    {
        if (seats.length < 1)
        {
            return null;
        }

        return seats[seats.length - 1];
    }
}
