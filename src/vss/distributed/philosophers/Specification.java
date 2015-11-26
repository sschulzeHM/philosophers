package vss.distributed.philosophers;

import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 26.11.15.
 */
public class Specification implements ISpecification
{
    private int numberOfPhilosophers;
    private int numberOfUshers;
    private int numberOfSeats;

    public Specification(int numberOfPhilosophers, int numberOfUshers, int numberOfSeats)
    {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfUshers = numberOfUshers;
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public int getNumberOfPhilosophers() throws RemoteException
    {
        return numberOfPhilosophers;
    }

    public void setNumberOfPhilosophers(int numberOfPhilosophers)
    {
        this.numberOfPhilosophers = numberOfPhilosophers;
    }

    @Override
    public int getNumberOfUshers() throws RemoteException
    {
        return numberOfUshers;
    }

    public void setNumberOfUshers(int numberOfUshers)
    {
        this.numberOfUshers = numberOfUshers;
    }

    @Override
    public int getNumberOfSeats() throws RemoteException
    {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats)
    {
        this.numberOfSeats = numberOfSeats;
    }
}
