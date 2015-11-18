package vss.distributed.philosophers;

/**
 * Created by stefanschulze on 05.11.15.
 */
public class HungryPhilosopher extends Philosopher
{
    public HungryPhilosopher(Table table, int id)
    {
        super(table, id);
        allowNeeds();
    }

    @Override
    public synchronized void restrictNeeds()
    {
        EATTIME *= 2;
        THINKTIME *= 2;
        SLEEPTIME *= 2;
        MAX_TRIES *= 0.5;

        this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public synchronized void allowNeeds()
    {
        EATTIME *= 0.5;
        THINKTIME *= 0.5;
        SLEEPTIME *= 0.5;
        MAX_TRIES *= 2;

        this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    protected String getOwnName()
    {
        return "Hungry " + super.getOwnName();
    }
}
