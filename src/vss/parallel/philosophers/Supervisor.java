package vss.parallel.philosophers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Supervisor extends Thread {

	private HashMap <Philosopher, Integer> philosophers ;
	private final Table table;
	private final int NUMBER_OF_PHILOSOPHERS;
    private final int NUMBER_OF_HUNGRY_PHILOSOPHERS;

	public Supervisor(Table table, int numOfPhil, int numOfHungPhil) {
		this.table = table;
		NUMBER_OF_PHILOSOPHERS = numOfPhil;
		NUMBER_OF_HUNGRY_PHILOSOPHERS = numOfHungPhil;
		initializePhilosophers();
	}



	public void run(){
		while(true){
			for(Philosopher philosopher : philosophers ){

				if(philosopher.getMeals() >= philosopher.getMaxMeals())
				{
					Logger.getGlobal().log(Level.INFO, "Philosopher " + philosopher.getId() + " ate too much (" + MAX_MEALS_BEFORE_LEAVE_SEAT + " times). He has to leave the Table.");
					philosopher.interrupt();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void initializePhilosophers(){
		
		 philosophers = new LinkedHashMap<>();
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++)
        {
            philosophers.put(new Philosopher(table, i),0);
        }
        
        for (int i = 0; i < NUMBER_OF_HUNGRY_PHILOSOPHERS; i++)
        {
            philosophers.put(new Philosopher(table, i),0);
        }
	}
}
