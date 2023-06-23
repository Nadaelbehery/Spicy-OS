package osProject;

import java.io.Serializable;

public class PCB  implements Serializable{
     Word pID;
     Word state;
     Word PC;
     Word memUpperBoundary;
     Word memLowerBoundary;
     
     
	public PCB(Word pID, Word state, Word pC, Word memLowerBoundary, Word memUpperBoundary) {
		this.pID = pID;
		this.state = state;
		PC = pC;
		this.memUpperBoundary = memUpperBoundary;
		this.memLowerBoundary = memLowerBoundary;
	}
	   
    public void PcIncrementer() {
   	 String x=PC.data;
   	 int z=Integer.parseInt(x);
   	 z++;
   	 PC.data=z+"";
    }
}
