package osProject;

import java.io.File;
import java.util.ArrayList;

public class Memory {
	Word [] mem;
	int count;
	int noOfProccesses;
	
	public Memory() {
		mem=new Word[40];
		for(int i=0 ; i<mem.length ; i++) {
			mem[i]=new Word("null","null");
		}
		count=0;
		noOfProccesses=0;
	}
//	public int availableIndex() {
//		int x=0;
//		for(int i=0;i<mem.length;i++) {
//			if(mem[i]==null)
//				x++;
//		}
//		return x;
//	}
	public void WriteIntoMem(Process p) {
		p.pcb.memLowerBoundary=new Word("memLowerBoundary",count+"");
		p.pcb.PC=new Word("PC",count+"");
		int var=0;
	 for(int i=0 ; i<p.instructions.length ; i++) {
		 mem[count]=p.instructions[i];
		 count++;
	 }
	 for(int i=0 ; i<p.variables.length ; i++) {
		 mem[count]=p.variables[i];
		 count++;
	 } 
	
	 mem[count]=p.pcb.pID;
	 count++;
	 mem[count]=p.pcb.state;
	 count++;
	 mem[count]=p.pcb.PC;
	 count++;
	 mem[count]=p.pcb.memLowerBoundary;
	 count++;
     p.pcb.memUpperBoundary=new Word("memUpperBoundary",count+"");
	 mem[count]=p.pcb.memUpperBoundary;
	 count++;
	 noOfProccesses++;
	}
	public String[] removeFromMem(Process p) {
	   for(int i =Integer.parseInt(p.pcb.memLowerBoundary.data) ; i<=Integer.parseInt(p.pcb.memUpperBoundary.data) ; i++) {
		   mem[i]=new Word("null", "null");
	   }
//	   count=Integer.parseInt(p.pcb.memLowerBoundary.data);
	   noOfProccesses--;
	   String [] ret = shiftMem();
	   count = Integer.parseInt(ret[1]) +1;
	   for(int i = Integer.parseInt(ret[1]) +1; i<mem.length; i++) {
		   mem[i] = new Word("null", "null");
	   }
	   return ret;
	}
	
	public String [] removeFirst() {
		count = 0;
		String pID = "";
		for(int i = 0; i<mem.length; i++) {
			Word w = mem[i];
			if(w.name.equals("ID")) {
				pID = w.data;
			}
			if(w.name.equals("memUpperBoundary")) {
				mem[i] = new Word("null", "null");
				break;
			}
			mem[i] = new Word("null", "null");
		}
		   noOfProccesses--;

		String [] ret = new String [3];
		ret[0] = pID;
		String [] ret1 = shiftMem();
		ret[1] = ret1[0];
		ret[2] = ret1[1];
		return ret;
	}
	
	public String[] shiftMem() {
		int shift = 0;
		for(int i = 0; i<mem.length; i++) {
			if(!(mem[i].name.equals("null"))) {
				shift = i;
				break;
			}
		}
		int j = 0;
		for(int i = shift; i<mem.length; i++) {
			if(!(mem[i].name.equals("null"))) {
				mem[j] = mem[i];
				j++;
			}
			else {
				count = j;
				break;
			}
		}

		String [] ret = {"0", count-1 + ""};
		return ret;
		
	}
	public Word readFromMem(int i) {
		return mem[i];
	}
	public void printMem() {
		for (int i=0; i<mem.length ; i++) {
			System.out.print("Index"+i+" "+mem[i].name+" "+mem[i].data+"--");
			
		}
		System.out.println("");
	}
	
	
}
