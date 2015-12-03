package vss.distributed.philosophers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Usher {
    public static final int SEAT_UNAVAILABLE = -1;

    private final int id;

    private Seat[] seats;
    private Seat nullSeat;
    private boolean running;

    public Usher(int id) {
        this.id = id;
        this.running = true;
    }

    public synchronized Seat getAvailableSeat(String philName) {
        while (true) {
            if (running) {
                for (Seat seat : seats) {
                    if (seat.isAvailable()) {
                        seat.setAvailable(false);
                        return seat;
                    }
                }
            }
                try {
                    Logger.getGlobal().log(Level.INFO, philName + " waiting for a seat.");
                    wait();
                    Logger.getGlobal().log(Level.INFO, philName + " was awakened");
                } catch (InterruptedException e) {
                    Logger.getGlobal().log(Level.INFO, "!!!!!!!!! A waiting philosoph is interrupted !!!!!!!!!!!");

                }
        }
    }

    public int getId() {
        return id;
    }

    // when a philosopher leaves the seat, he has to wake up a waiting philosopher
    // it is impossible to leave a seat and get a seat parallel. For both you need the usher. PROBLEM???
    //--> Solution maybe: synchronize the seat, too
    public synchronized void leaveSeat(Seat seat) {
        try {
            Logger.getGlobal().log(Level.INFO, "Usher " + getId() + " leave Seat " + seat.getId());
        } catch (RemoteException e) {
            Logger.getGlobal().log(Level.INFO, "Usher receices Remote Exception.");
        }
        seat.leave();

        if(running) {
            notify();
        }
    }

    public synchronized void stopRunning() {
        running = false;
    }

    public synchronized void continueRunning(){
        running = true;
        notifyAll();
    }

    public void setSeats(Seat[] seats) {
        this.seats = seats;
    }
}
