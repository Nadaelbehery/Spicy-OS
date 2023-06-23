package osProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class interpreter {
	Memory m;
	Queue<Process> ready;
	Queue<Process> blocked;
	int processes = 0;
	Scheduler scheduler;
	Resources file;
	Resources input;
	Resources output;
	int processesdone;
	ArrayList<String> ProcessesPath;
	// boolean process2done;
	// boolean process3done;

	public interpreter() {
		m = new Memory();
		ready = new LinkedList<Process>();
		blocked = new LinkedList<Process>();
		scheduler = new Scheduler();
		new File("disk").mkdirs();
		processesdone = 0;
		ProcessesPath = new ArrayList<String>();
		file = new Resources();
		input = new Resources();
		output = new Resources();

	}

	public void programReader(String path) {
		processes++;
		FileReader program;
		try {
			program = new FileReader(path);
			BufferedReader br = new BufferedReader(program);
			String line = br.readLine();
			ArrayList<Word> instructions = new ArrayList<Word>();
			Word w;
			while (line != null) {
				// String []contents=line.split(" ");
				w = new Word("instruction", line);
				instructions.add(w);
				line = br.readLine();
			}
			br.close();
			Word v0 = new Word("v0", "");
			Word v1 = new Word("v1", "");
			Word v2 = new Word("v2", "");
			Word[] instruction = new Word[instructions.size()];
			Word[] variable = new Word[3];
			for (int i = 0; i < instructions.size(); i++) {
				instruction[i] = instructions.get(i);
			}
			variable[0] = v0;
			variable[1] = v1;
			variable[2] = v2;

			PCB pcb = new PCB(new Word("ID", path.charAt(8) + ""), new Word("state", "ready"), new Word("PC", ""),
					new Word("memLowerBoundary", "0"), new Word("memUpperBoundary", ""));
			Process p = new Process(instruction, pcb, variable);
			
			if (m.noOfProccesses < 2) {
				m.WriteIntoMem(p);
				ready.add(p);
			} else {// remove and save in hard disk
				Process toBeRemoved = null;
				if(!(blocked.isEmpty())) {
					toBeRemoved = blocked.remove();
					input.blocked.remove(toBeRemoved);
					output.blocked.remove(toBeRemoved);
					file.blocked.remove(toBeRemoved);
				}
				else {
					toBeRemoved = ready.remove();
				}
				String [] ret = m.removeFromMem(toBeRemoved);
				
//				
				if(!(ready.isEmpty())) {
					Process toBeUp = ready.peek();
					toBeUp.pcb.PC.data = (Integer.parseInt(toBeUp.pcb.PC.data) - Integer.parseInt(toBeUp.pcb.memLowerBoundary.data)) + "";
					toBeUp.pcb.memLowerBoundary.data = ret[0];
					toBeUp.pcb.memUpperBoundary.data = ret[1];
				}
				else if (!(blocked.isEmpty())){
					Process toBeUp = blocked.peek();
					toBeUp.pcb.PC.data = (Integer.parseInt(toBeUp.pcb.PC.data) - Integer.parseInt(toBeUp.pcb.memLowerBoundary.data)) + "";
					toBeUp.pcb.memLowerBoundary.data = ret[0];
					toBeUp.pcb.memUpperBoundary.data = ret[1];
				}
			

				m.WriteIntoMem(p);
				ready.add(p);
				toBeRemoved.pcb.state.data = "new";
				System.out.println("Process " + toBeRemoved.pcb.pID.data + " swapped into disk" + " ");
				ProcessesPath.add("disk/" + toBeRemoved.pcb.pID.data+ ".txt");
				
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream("disk/" + toBeRemoved.pcb.pID.data + ".txt"));
				oos.writeObject(toBeRemoved);
				oos.flush();
				oos.close();
			}
			//}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int execute(Process p, int instDone, Scanner scanner) {
		// Process p = scheduler.schedule(ready);
		int i = Integer.parseInt(p.pcb.PC.data);
		int newpc = i + 1;

		String inst = m.readFromMem(i).data;
		System.out.println("Instruction currently executing " + inst);
		String[] line = inst.split(" ");
		String instName = line[0];
		String data = "";

		switch (instName) {	
		
		case "print":
			if (output.mutex.id == Integer.parseInt(p.pcb.pID.data)) {
				String dataPrint = "";
				for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
						.parseInt(p.pcb.memUpperBoundary.data); j++) {
					if (m.readFromMem(j).name.equals(line[1])) {
						dataPrint = m.readFromMem(j).data;
					}
				}
				System.out.println(dataPrint);
			}
			p.pcb.PC.data = newpc + "";
			instDone++;
			break;
		case "assign":
			if (line[2].equals("readFile") && file.mutex.id == Integer.parseInt(p.pcb.pID.data)) {
				
				if(p.temp.equals("")) {
					String filename = "";
					for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
							.parseInt(p.pcb.memUpperBoundary.data); j++) {
						if (m.readFromMem(j).name.equals(line[3])) {
							filename = m.readFromMem(j).data;
						}
					}
					
				
					String sData = "";

					try {
						FileReader fr = new FileReader("disk/" + filename + ".txt");
						BufferedReader br = new BufferedReader(fr);

						while (br.ready()) {
							sData += br.readLine();
						}
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p.temp = sData;
				}
				else {
					for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
							.parseInt(p.pcb.memUpperBoundary.data); j++) {
						if (m.readFromMem(j).name.equals("v0") || m.readFromMem(j).name.equals("v1")
								|| m.readFromMem(j).name.equals("v2")) {
							m.readFromMem(j).name = line[1];
							m.readFromMem(j).data = p.temp;			
							break;
						}
					}
					p.temp = "";
					p.pcb.PC.data = newpc + "";
				}
				instDone++;
			} else {
				if (line[2].equals("input") && input.mutex.id == Integer.parseInt(p.pcb.pID.data)) {
					
					if(p.temp.equals("")) {
						System.out.println("Please enter a value");

						p.temp = scanner.nextLine();
					}
					else {
						for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
								.parseInt(p.pcb.memUpperBoundary.data); j++) {
							if (m.readFromMem(j).name.equals("v0") || m.readFromMem(j).name.equals("v1")
									|| m.readFromMem(j).name.equals("v2")) {
								m.readFromMem(j).name = line[1];
								m.readFromMem(j).data = p.temp;
								break;
							}
						}
						p.pcb.PC.data = newpc + "";
						p.temp = "";
					}
				}

				instDone++;
			}
			break;

		case "writeFile":
			if (file.mutex.id == Integer.parseInt(p.pcb.pID.data)) {
				String tempData = "";
				String filename = "";
				for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
						.parseInt(p.pcb.memUpperBoundary.data); j++) {
					if (m.readFromMem(j).name.equals(line[1])) {
						filename = m.readFromMem(j).data;
					}
					if (m.readFromMem(j).name.equals(line[2])) {
						tempData = m.readFromMem(j).data;
					}
				}

				try {
					
					File file = new File("disk/" + filename + ".txt");
					file.createNewFile();
					FileWriter fw = new FileWriter("disk/" + filename + ".txt");
					fw.write(tempData);
					fw.close();
//					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("disk/" + filename + ".txt"));
//					oos.writeObject(tempData);
//					oos.flush();
//					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				instDone++;
				p.pcb.PC.data = newpc + "";

			}
			break;
		case "printFromTo":
			if (output.mutex.id == Integer.parseInt(p.pcb.pID.data)) {

				int x = 0;
				int y = 0;
				for (int j = Integer.parseInt(p.pcb.memLowerBoundary.data); j < Integer
						.parseInt(p.pcb.memUpperBoundary.data); j++) {
				
					if (m.readFromMem(j).name.equals(line[1])) {
						x = Integer.parseInt(m.readFromMem(j).data);

					}
					if (m.readFromMem(j).name.equals(line[2])) {
						y = Integer.parseInt(m.readFromMem(j).data);
					}
				}
				for (int k = x; k <= y; k++) {
					System.out.println(k);
				}

			

			}
			p.pcb.PC.data = newpc + "";
			instDone++;
			break;
		case "semWait":
			if (line[1].equals("userInput")) {
				if (input.mutex.i == 0) {
					input.semWait(p);
					p.pcb.PC.data = newpc + "";
				} else {
					ready.remove(p);
					blocked.add(p);
					input.blocked.add(p);
					p.pcb.state.data = "blocked";
				}
			} else if (line[1].equals("userOutput")) {
				if (output.mutex.i == 0) {
					output.semWait(p);
					p.pcb.PC.data = newpc + "";
				} else {
					ready.remove(p);
					blocked.add(p);
					p.pcb.state.data = "blocked";
					
					output.blocked.add(p);

				}
			} else if (line[1].equals("file")) {
				if (file.mutex.i == 0) {
					file.semWait(p);
					p.pcb.PC.data = newpc + "";
				} else {
					ready.remove(p);
					blocked.add(p);
					p.pcb.state.data = "blocked";
					file.blocked.add(p);

				}
			}
			instDone++;
			break;
		case "semSignal":
			if (line[1].equals("userInput")) {
				if (input.mutex.id == Integer.parseInt(p.pcb.pID.data) && input.mutex.i == 1) {
					input.semSignal();
					if (input.blocked.size() > 0) {
						Process r = input.blocked.remove(0);
						blocked.remove(r);
						ready.add(r);
						r.pcb.state.data = "ready";
					}
					p.pcb.PC.data = newpc + "";
				}
			} else if (line[1].equals("userOutput")) {
				if (output.mutex.id == Integer.parseInt(p.pcb.pID.data) && output.mutex.i == 1) {
					output.semSignal();
					if (output.blocked.size() > 0) {
						Process r = output.blocked.remove(0);
						blocked.remove(r);
						ready.add(r);
						r.pcb.state.data = "ready";
					}
					p.pcb.PC.data = newpc + "";
				}
			} else if (line[1].equals("file")) {
				if (file.mutex.id == Integer.parseInt(p.pcb.pID.data) && file.mutex.i == 1) {
					file.semSignal();
					if (file.blocked.size() > 0) {
						Process r = file.blocked.remove(0);
						blocked.remove(r);
						ready.add(r);
						r.pcb.state.data = "ready";
					}
					p.pcb.PC.data = newpc + "";
				}
			}
			instDone++;
			break;
			
			default: System.out.println("Default");
			
		}
		int instBoundry = (Integer.parseInt(p.pcb.memUpperBoundary.data)) - 8;
		if (newpc > instBoundry) {// finished
			ready.remove(p);
			blocked.remove(p);

			String [] ret = m.removeFromMem(p);
			Process toBeUp = null;
			if(!(ready.isEmpty())) {
				toBeUp = ready.peek();
			}
			if(!(blocked.isEmpty())) {
				toBeUp = blocked.peek();
			}
			if(toBeUp != null) {
				toBeUp.pcb.PC.data = (Integer.parseInt(toBeUp.pcb.PC.data) - Integer.parseInt(toBeUp.pcb.memLowerBoundary.data)) + "";
				toBeUp.pcb.memLowerBoundary.data = ret[0];
				toBeUp.pcb.memUpperBoundary.data = ret[1];
			}

			p.pcb.state.data = "finished";
			//ready.remove(p);
			processesdone++;
			if (!ProcessesPath.isEmpty()) {
				String path = ProcessesPath.get(0);
				FileInputStream fileIn;
				Process pnew = null;
				try {
					fileIn = new FileInputStream(path);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					pnew = (Process) in.readObject();
					in.close();
					fileIn.close();
				} catch (IOException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Process " + pnew.pcb.pID.data + " swapped out of disk" + " ");
				m.WriteIntoMem(pnew);
				ProcessesPath.remove(0);
				String currentState = pnew.pcb.state.data;
//				if (currentState.equals("blocked")) {
//					blocked.add(pnew);
//				} else {
//					if (currentState.equals("ready")) {
//						ready.add(pnew);
//					}
//				}
				pnew.pcb.state.data = "ready";
				ready.add(pnew);

			}

		}
		return instDone;

	}

	public void mainM() {

		Scanner scanner = new Scanner(System.in);
		System.out.println("At what time does proccess1 arrive");
		int time1 = scanner.nextInt();
		System.out.println("At what time does proccess2 arrive");
		int time2 = scanner.nextInt();
		System.out.println("At what time does proccess3 arrive");
		int time3 = scanner.nextInt();
		System.out.println("Enter time slice");
		int timeSlice = scanner.nextInt();
		scanner.nextLine();
		int time = 0;
		boolean callSchedular = true;
		Process toExecute = null;
		int instDone = 0;
		while (processesdone < 3) {
			System.out.println("Cycle" + time);
			

			if (time == time1) {
				this.programReader("Program_1.txt");
			}
			if (time == time2) {
				this.programReader("Program_2.txt");

			}
			if (time == time3) {
				this.programReader("Program_3.txt");
			}
			System.out.print("Ready Queue: ");
			for(Process elem: ready) {
				System.out.print("Process: " + elem.pcb.pID.data + " ");
			}
			System.out.println();
			System.out.print("Blocked Queue: ");
			for(Process elem: blocked) {
				System.out.print("Process: " + elem.pcb.pID.data + " ");
			}
			System.out.println();
			if (callSchedular) {
				toExecute = scheduler.schedule(ready);
				if(toExecute == null) {
					System.out.println("toExecute id NULLLLL");
				}
				System.out.println("Process currently executing " + toExecute.pcb.pID.data);
				callSchedular = false;
				instDone = 0;
			}
			if (toExecute != null && (toExecute.pcb.state.data).equals("ready")) {
				System.out.println("this was instruction " + instDone);
				System.out.println("Process currently executing PC" + toExecute.pcb.PC.data);

				instDone = execute(toExecute, instDone, scanner);
			}
			if (instDone == timeSlice || !((toExecute.pcb.state.data).equals("ready"))) {

				callSchedular = true;
			}

			m.printMem();
			time++;
		}
		scanner.close();

	}

	public static void main(String[] args) {
		interpreter firstTest = new interpreter();
		firstTest.mainM();

	}

}
