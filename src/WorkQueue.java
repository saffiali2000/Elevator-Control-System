import java.util.LinkedList;
import java.util.List;

/**
 * WorkQueue abstract class
 * @author user Ashwin Stoparczyk
 *
 */
public abstract class WorkQueue {
	private List<CommandData> queue = new LinkedList<CommandData>();
	
	protected WorkQueue() {
		new WorkerThread().start();
	}
	
	/**
	 * Add item to queue
	 * @param workItem Item to be added
	 */
	public final void enqueue(CommandData workItem) {
		synchronized (queue) {
			queue.add(workItem);
			queue.notify();
		}
	}
	
	/**
	 * Processes an item
	 * @param workItem Item to be processed
	 */
	protected abstract void processItem(CommandData workItem);
	
	/**
	 * Private thread that works on items in queue
	 */
	private class WorkerThread extends Thread {
		
		/**
		@Override default run method
		*/
		public void run(){
			CommandData workItem = null;
			
			//Grab next item from queue and remove it
			while (true) {
					synchronized (queue) {
							try {
								while (queue.isEmpty())
									queue.wait();
							} catch (InterruptedException e) {
								return;
							}
							workItem = queue.remove(0);
					} 
					processItem(workItem);
			}
		}
	}
}
