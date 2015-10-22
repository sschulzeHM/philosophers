package vss.parallel.philosophers;

/**
 * Created by stefanschulze on 22.10.15.
 */
public class Seat
{
    private final int id;
    private final int leftNeighbor;
    private final int rightNeighbor;

    public Seat(int id, int leftNeighbor, int rightNeighbor)
    {
        this.id = id;
        this.leftNeighbor = leftNeighbor;
        this.rightNeighbor = rightNeighbor;
    }

    public int getRightNeighbor()
    {
        return rightNeighbor;
    }

    public int getLeftNeighbor()
    {
        return leftNeighbor;
    }

    public int getId()
    {
        return id;
    }
}
