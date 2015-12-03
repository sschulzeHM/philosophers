package vss.distributed.philosophers;

import java.rmi.RemoteException;

/**
 * Created by stefanschulze on 26.11.15.
 */
public class Specification implements ISpecification
{
    private final String clientID;
    private final int numberOfPhilosophers;
    private final int numberOfUshers;
    private final int numberOfSeats;
    private final int numberOfHungryPhilosophers;
    private final int maxMeals;

    public Specification(int numberOfPhilosophers, int numberOfUshers, int numberOfSeats, int numberOfHungryPhilosophers, String clientID, int maxMeals)
    {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfUshers = numberOfUshers;
        this.numberOfSeats = numberOfSeats;
        this.numberOfHungryPhilosophers = numberOfHungryPhilosophers;
        this.clientID = clientID;
        this.maxMeals = maxMeals;
    }

    @Override
    public int getNumberOfPhilosophers() throws RemoteException
    {
        return numberOfPhilosophers;
    }

    @Override
    public int getNumberOfHungryPhilosophers() throws RemoteException
    {
        return numberOfHungryPhilosophers;
    }

    @Override
    public int getNumberOfUshers() throws RemoteException
    {
        return numberOfUshers;
    }

    @Override
    public int getNumberOfSeats() throws RemoteException
    {
        return numberOfSeats;
    }

    @Override
    public String getClientID() throws RemoteException { return clientID;}

    @Override
    public int getMaxMealDiff() throws RemoteException
    {
        return maxMeals;
    }
}
