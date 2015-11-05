package vss.parallel.philosophers;

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

    public synchronized void restrictNeeds()
    {
        EATTIME = Philosopher.EATTIME;
        THINKTIME = Philosopher.THINKTIME;
        SLEEPTIME = Philosopher.SLEEPTIME;
        MAX_MEALS_BEFORE_SLEEP = Philosopher.MAX_MEALS_BEFORE_SLEEP;
        MAX_TRIES = Philosopher.MAX_TRIES;

        this.setPriority(Thread.NORM_PRIORITY);
    }

    public synchronized void allowNeeds()
    {
        EATTIME = Philosopher.EATTIME / 2;
        THINKTIME = Philosopher.THINKTIME / 2;
        SLEEPTIME = Philosopher.SLEEPTIME / 2;
        MAX_MEALS_BEFORE_SLEEP = Philosopher.MAX_MEALS_BEFORE_SLEEP * 2;
        MAX_TRIES = Philosopher.MAX_TRIES * 2;

        this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    protected String getOwnName()
    {
        return "Hungry " + super.getOwnName();
    }
}
