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

        this.ushers = new Usher[ushers];

        // assign seats to manage
        assignSeats(seats,ushers);
    }

    private void assignSeats(int seats, int ushers){
        int seatsPerUsher = seats / ushers;
        int firstSeat = 0;
        int lastSeat = 0;

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

    public void insertSeats(int countSeats, int afterSeat){
        for(Usher usher : ushers){
            usher.stopRunning();
        }

        boolean allSeatFree = false;
        while(!allSeatFree)
            for(Seat seat : seats){
                allSeatFree |= seat.isAvailable();
            }

        System.out.println("All was stopped and all Seats are free");

        Seat[] moreSeats = new Seat[seats.length + countSeats];

        //copy all seats until to the seat where will be insert after new seats.
        for(int i = 0; i <= afterSeat; i++){
            moreSeats[i] = seats[i];
        }

        for(int i = (afterSeat+1); i < (afterSeat+1)+countSeats; i++){
            moreSeats[i] = new Seat(i);
            moreSeats[i].setId(i);
        }

        for(int i = afterSeat+1,j=0; i < seats.length; i++,j++){
            moreSeats[(afterSeat+1+j)+countSeats] = seats[i];
            moreSeats[(afterSeat+1)+countSeats+j].setId((afterSeat+1)+countSeats+j);
        }



        seats = moreSeats;

        assignSeats(seats.length, ushers.length);

        for(Usher usher: ushers){
            usher.continueRunning();
        }

        System.out.println("All is running again");
    }
}
