package vss.distributed.philosophers;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 12.10.15.
 */
public class Table {
    private Usher[] ushers;
    private Seat[] seats;

    public Table(int seats, int ushers) {
        initSeats(seats);
        initUshers(seats, ushers);
    }

    private void initSeats(int seats) {
        this.seats = new Seat[seats];
        // create seats
        for (int i = 0; i < this.seats.length; i++) {
            this.seats[i] = new Seat(i);
        }
        // init created seats
        for (int i = 0; i < this.seats.length; i++) {
            this.seats[i].initialize(getLeftSeat(i), new Fork(true, getRightForkID(i)));
        }
    }

    private void initUshers(int seats, int ushers) {

        this.ushers = new Usher[ushers];
        for(int i = 0; i < ushers; i++){
           this.ushers[i] = new Usher(i);
        }
        // assign seats to manage
        assignSeats(seats);
    }

    private void assignSeats(int seats) {
        int seatsPerUsher = seats / this.ushers.length;
        int firstSeat = 0;
        int lastSeat = 0;

        for (int i = 0; i < this.ushers.length; i++) {
            lastSeat += seatsPerUsher - 1;
            // last usher gets remaining seats
            lastSeat = (lastSeat < this.seats.length && i == this.ushers.length - 1) ? this.seats.length - 1 : lastSeat;
            Seat[] managedSeats = Arrays.copyOfRange(this.seats, firstSeat, lastSeat + 1); //copy lastSeat exclusive therefore + 1
            this.ushers[i].setSeats(managedSeats);
            firstSeat = lastSeat + 1;
            lastSeat++;
        }
    }

    public Usher getUsher() {
        int someUsher = ((int) (Math.random() * 10)) % ushers.length;
        return ushers[someUsher];
    }


    private Seat getLeftSeat(int i) {
        if (i == 0) {
            return seats[seats.length - 1];
        }

        return seats[i - 1];
    }

    private int getRightForkID(int i) {
        return i;
    }

    public Seat getFirstSeat() {
        if (seats.length < 1) {
            return null;
        }

        return seats[0];
    }

    public Seat getLastSeat() {
        if (seats.length < 1) {
            return null;
        }

        return seats[seats.length - 1];
    }

    public void stop() {
        for (Usher usher : ushers) {
            usher.stopRunning();
        }

        boolean allSeatFree;
        do {
            allSeatFree = true;
            for (Seat seat : seats) {
                allSeatFree &= seat.isAvailable();
            }
        } while (!allSeatFree);
    }

    public void continueRunning() {
        for (Usher usher : ushers) {
            usher.continueRunning();
        }
    }

    public void insertSeats(boolean before, int countSeats, int seatID) {
        stop();

        Logger.getGlobal().log(Level.INFO, "Everything was stopped and all Seats are free");

        Seat[] moreSeats = new Seat[seats.length + countSeats];

        if (before) {
            seatID--;
        }
        //copy all seats until to the seat where will be insert after new seats.
        for (int i = 0; i <= seatID; i++) {
            moreSeats[i] = seats[i];
        }

        seatID++;

        for (int i = seatID; i < seatID + countSeats; i++) {
            moreSeats[i] = new Seat(i);
            moreSeats[i].initialize(moreSeats[Math.floorMod(i - 1,seats.length)], new Fork(true, i));
        }

        for (int i = seatID; i < seats.length; i++) {
            moreSeats[i + countSeats] = seats[i];
            moreSeats[i + countSeats].changeID((i + countSeats));
        }

        for(int i = 0; i < moreSeats.length; i++){
            moreSeats[i].changeLeftNeighbor(moreSeats[Math.floorMod(i-1,moreSeats.length)]);
        }

        seats = moreSeats;

        assignSeats(seats.length);

        continueRunning();

        Logger.getGlobal().log(Level.INFO, "Evering is running again");
    }
}
