package osProject;

import java.util.ArrayList;

public class Resources {
	
	Mutex mutex;
	ArrayList<Process> blocked;

	public Resources() {
		mutex = new Mutex();
		blocked = new ArrayList<Process>();
	}
	
	public void semWait(Process p) {
		this.mutex.i = 1;
		this.mutex.id = Integer.parseInt(p.pcb.pID.data); 
	}
	
	public void semSignal() {
		this.mutex.i = 0;
		this.mutex.id = -1;
	}
}
