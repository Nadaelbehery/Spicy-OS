package osProject;

import java.util.Queue;

public class Scheduler {

	public Scheduler() {
		
	}
	
	public Process schedule(Queue<Process> q) {
		if(!(q.isEmpty())) {
			Process p =q.remove();
			q.add(p);
			return q.peek();
		}
		return null;

		/*
		 * if(p.getInstCount()<2) return p; else { p.setInstCount(0); q.remove();
		 * q.add(p); return q.peek(); }
		 */		
	}
}
