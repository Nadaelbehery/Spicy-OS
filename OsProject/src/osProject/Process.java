package osProject;

import java.io.Serializable;

public class Process implements Serializable{
     Word [] instructions ;
     PCB pcb;
     Word [] variables;
     int instCount;
     int instExecuted;
     String temp;
     
     
     public Word[] getInstructions() {
		return instructions;
	}


	public void setInstructions(Word[] instructions) {
		this.instructions = instructions;
	}


	public PCB getPcb() {
		return pcb;
	}


	public void setPcb(PCB pcb) {
		this.pcb = pcb;
	}


	public Word[] getVariables() {
		return variables;
	}


	public void setVariables(Word[] variables) {
		this.variables = variables;
	}


	public int getInstCount() {
		return instCount;
	}


	public void setInstCount(int instCount) {
		this.instCount = instCount;
	}


	public Process(Word[] instructions,PCB pcb,Word []variables) {
    	 this.instructions=instructions;
    	 this.pcb=pcb;
    	 this.variables=variables;
    	 instCount = 0;
    	 instExecuted = 0;
    	 temp = "";
     }
  
}
