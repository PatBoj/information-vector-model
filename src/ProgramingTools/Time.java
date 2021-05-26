package ProgramingTools;

import java.util.ArrayList;
import java.util.Collections;

public class Time {
	// ~ DATA FIELDS ~
	// TIME COMARISON
	// Contains information about total time that i-th chunk of code was performing 
	Long[] codeTimes;
	
	// Number of iterations of this chunk of code
	int[] codeRuns;
	
	// Custom name given to this chunk of code
	String[] codeNames;
	
	// Current time
	Long currentTime;
	
	// PROGRESS BAR
	// Time when this class was created
	private long startTime;
	
	// Progress of the loop
	private double progress;
	
	// Time when loop started 
	private long startLoopTime;
	
	// Time when the last loop was
	private long previousLoopTime;
	
	// List of all realization of the loop
	private ArrayList<Long> times;
	
	// Just a sorted listed of times
	private ArrayList<Long> sortedTimes;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Time(String[] names) {
		int N = names.length;
		
		codeTimes = new Long[N];
		codeRuns = new int[N];
		codeNames = new String[N];
		
		for(int i=0; i<N; i++) {
			codeTimes[i] = (long) 0;
			codeRuns[i] = 0;
			codeNames[i] = names[i];
		}
		
		startTime = System.currentTimeMillis();
		times = new ArrayList<Long>();
		sortedTimes = new ArrayList<Long>();
	}
	
	//Default constructor
	public Time() {
		startTime = System.currentTimeMillis();
		times = new ArrayList<Long>();
		sortedTimes = new ArrayList<Long>();
	}
	
	// ~ METHODS ~
	// TIME COMPARISON
	// Starts the timer
	public void startTimer() {currentTime = System.currentTimeMillis();}
	
	// Save time and realization of i-th chunk of code
	public void saveTime(int i) {
		codeTimes[i] += System.currentTimeMillis() - currentTime;
		codeRuns[i] += 1;
	}
	
	// Save time and realization of chunk of code by given name
	public void saveTime(String str) {
		int index = -1;
		for(int i=0; i<codeNames.length; i++)
			if(str.equals(codeNames[i])) {
				index = i;
				break;
			}
		
		if(index != -1)
			saveTime(index);
		else
			throw new Error("Wrong name for the chunk of code.");
	}
	
	// Prints the result of the chunk of code comparison
	public void printTimeComparison() {
		System.out.println("Name\tTime\tRealisations\tPer iteration /ms");
		for(int i=0; i<codeNames.length; i++)
			System.out.println(codeNames[i] + "\t" + Tools.convertTime(codeTimes[i]) + "\t" + codeRuns[i] + (codeTimes[i]/codeRuns[i]));
		System.out.println("");
	}
	
	// PROGRESS BAR
	public void startLoopTimer() {startLoopTime = System.currentTimeMillis(); previousLoopTime = startLoopTime;}
	
	// Shows progress of two loops
	public void progress(int i, int iMin, int iMax, int j, int jMin, int jMax) {		
		times.add(System.currentTimeMillis() - previousLoopTime);
		previousLoopTime = System.currentTimeMillis();
		sortedTimes.add(Tools.getLastLong(times));
		Collections.sort(sortedTimes);
		
		progress = (double)((i-iMin)*(jMax-jMin)+(j-jMin+1))/((iMax-iMin)*(jMax-jMin));
		
		printProgressBar(progress);
		System.out.println("Main loop " + i + "\\" + (iMax-1) + 
			", secondary loop " + (j+1) + "\\" + jMax + ", together " + ((i-iMin)*(jMax-jMin)+(j-jMin+1)) + 
			"\\" + ((iMax-iMin)*(jMax-jMin)));
		
		System.out.println("Estimated time left: " + 
			Tools.convertTime((long)((1-progress) * (iMax-iMin) * (jMax - jMin) * Tools.getAverageLong(times))));
		System.out.println("This iteration took: " + Tools.convertTime(times.get(times.size()-1)));
		
		System.out.println("  - minimum: \t" + 
			Tools.convertTime(Tools.getMinimumLong(sortedTimes)));
		System.out.println("  - Q1: \t" + 
			Tools.convertTime(Tools.getFirstQuartile(sortedTimes)));
		System.out.println("  - median: \t" + 
			Tools.convertTime(Tools.getMedian(sortedTimes)));
		/*System.out.println("  - mean: \t" + 
			convertTime((long)((1-progress) * (iMax-iMin) * getAverage(sortedTimes))));*/
		System.out.println("  - Q3: \t" + 
			Tools.convertTime(Tools.getThirdQuartile(sortedTimes)));
		System.out.println("  - maximum: \t" + 
			Tools.convertTime(Tools.getMaximumLong(sortedTimes)));
		
		System.out.println("Current time:   " + Tools.convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	// Shows progress of one loop
	public void progress(int i, int iMin, int iMax) {
		times.add(System.currentTimeMillis() - previousLoopTime);
		previousLoopTime = System.currentTimeMillis();
		sortedTimes.add(Tools.getLastLong(times));
		Collections.sort(sortedTimes);
		
		progress = (double)(i-iMin+1)/(iMax-iMin);
		
		printProgressBar(progress);
		System.out.println("Loops " + (i+1) + "\\" + iMax);
		System.out.println("Estimated time left: " + 
			Tools.convertTime((long)((1-progress) * (iMax-iMin) * Tools.getAverageLong(times))));
		System.out.println("This iteration took: " + Tools.convertTime(times.get(times.size()-1)));
		
		System.out.println("  - minimum: \t" + 
			Tools.convertTime(Tools.getMinimumLong(sortedTimes)));
		System.out.println("  - Q1: \t" + 
			Tools.convertTime(Tools.getFirstQuartile(sortedTimes)));
		System.out.println("  - median: \t" + 
			Tools.convertTime(Tools.getMedian(sortedTimes)));
		/*System.out.println("  - mean: \t" + 
			convertTime((long)((1-progress) * (iMax-iMin) * getAverage(sortedTimes))));*/
		System.out.println("  - Q3: \t" + 
			Tools.convertTime(Tools.getThirdQuartile(sortedTimes)));
		System.out.println("  - maximum: \t" + 
			Tools.convertTime(Tools.getMaximumLong(sortedTimes)));
		
		System.out.println("Current time:   " + Tools.convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	// Prints simple progress bar like this [###     ]
	private void printProgressBar(double progress) {
		int n = 50;
		String[] bar = new String[n];
		int done = (int)Tools.convertToDouble(progress/2 * 100, 0);
		
		for(int i=0; i<done; i++)
			bar[i] = "#";
		for(int i=done; i<n; i++)
			bar[i] = " ";
		
		System.out.print("[");
		
		for(int i=0; i<19; i++)
			System.out.print(bar[i]);
		
		if(Tools.convertToDouble(progress) < 0.1)
			System.out.print(bar[19] + "| " + Tools.convertToString(progress*100, 2) + "%" + " |" + bar[29]);
		else if(Tools.convertToDouble(progress)  < 1)
			System.out.print(bar[19] + "| " + Tools.convertToString(progress*100, 2) + "%" + " |");
		else if(Tools.convertToDouble(progress)  == 1)
			System.out.print("| " + Tools.convertToString(progress*100, 2) + "%" + " |");
		
		for(int i=30; i<n; i++)
			System.out.print(bar[i]);
		
		System.out.println("]");
	}
	
	// Stop simulation before given hours of working
	public void stopSimulationByTime(int hours) {
		Long duration = System.currentTimeMillis() - startTime;
		if((duration)/1000/60/60 > hours) {
			System.out.println("INTERRUPTED: " + Tools.convertTime(duration));
			System.exit(0);
		}
		else System.out.println("DONE: " + Tools.convertTime(duration));
	}
	
	// After finish print duration of simulation and play mp3 file
	public void speekDuration(String before, String after)  {
		System.out.println(before + Tools.convertTime(System.currentTimeMillis() - startTime) + after); 
		speek();
	}
	
	public void speekDuration(String before) {speekDuration(before, "");}
	public void speekDuration() {speekDuration("");}
	
	public void printDuration(String before, String after) {
		System.out.println(before + Tools.convertTime(System.currentTimeMillis() - startTime) + after);
	}
	
	public void printDuration(String before) {printDuration(before, "");}
	public void printDuration() {printDuration("");}
	
	// Announces end of the simulation
	private static void speek(String filename) {
		AePlayWave aw = new AePlayWave(filename);
        aw.start();
	}
	
	// Plays files
	public static void speek() {
		speek("tada.wav");
		speek("Voice1.wav");
	}
}
