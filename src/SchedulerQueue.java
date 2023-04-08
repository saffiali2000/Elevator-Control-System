/**
 * Queue that processes scheduler requests in the background
 * @author Ashwin Stoparczyk
 *
 */
public class SchedulerQueue extends WorkQueue{
	private Scheduler scheduler;
	
	public SchedulerQueue(Scheduler scheduler) {
		this.scheduler = scheduler;		
	}

	@Override
	protected void processItem(CommandData workItem) {
		// TODO Auto-generated method stub
		if (workItem.getDest().equals("elevator")) { //Command is to move an elevator
			scheduler.sendCommandElevator(scheduler.determineClosestElevator(), scheduler.getCurrentCommand());
		} else { //Command is a confirmation from elevator to floor
			scheduler.sendUpdateFloor();
		}
	}

}
