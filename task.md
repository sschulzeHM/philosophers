# dining philosophers
VSS dining philosopher task

story:
Philosophers think all day in a meditation area. 
Once a philosopher gets hungry he enters the eating area.
Then the philosopher asks for a seat.
The eating area assigns one of the eating area's ushers to the requesting philosopher.
The usher manages a specific part of the eating area with a defined number of seats.
The philosopher's request is hence served by the assigned usher until the philosopher is finished eating or the usher's capability is exceeded (tbd).
The usher tries to find an available seat for the philosopher.
The philospher takes the seat.
The philosopher tries to take one fork from his left and one from his right.
Once both forks are available, the philosopher starts eating.
The usher waits for the next request.
Once the philosopher finishes his meal he informs the usher that he leaves his seat or he simply leaves (tbd).
The philosopher returns to the mediation area.
Once a philosopher had a certain amount of meals, he takes a nap.

requirements:
- all processes have to be concurrent
- the number of philosophers and seats have to be variable
- a philosophers does not have a fixed seat
- a philosopher has to be implemented as a Thread
- a philosopher can be more hungry than others (higher thread priority and frequency)
- a philosopher who eats too frequently (tbd) has to be rejected from the eating area by a supervisor who balances the rations for all philosophers
- enable performance measurements
- enable logging
- develop run scenarios with defined values for eating time, thinking time, sleeping time and overall run time
- document run scenarios and include system configuration details like number of CPUs, operation system, type of machine (laptop, workstation)
