package ProgramingTools;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Debug 
{
	// ~ DATA FIELDS ~
	private long startTime;
	private long stopTime;
	private double progress;
	private long startLoopTime;
	private ArrayList<Long> times;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Debug(int zero) {
		startTime = System.currentTimeMillis();
		stopTime = System.currentTimeMillis();
		progress = 0;
		startLoopTime = 0;
		times = new ArrayList<Long>();
	}
	
	// Default constructor
	public Debug() {this(0); System.out.println("Timer is on.\n");}
	
	// ~ METHODS ~
	// Starts timer
	public void startTimer() {
		startTime = System.currentTimeMillis(); System.out.println("Reset timer.");
	}
	
	// Stops timer
	public void stopTimer() {stopTime = System.currentTimeMillis();}
	
	// Converts milliseconds to time format: 00:00:00
	public String convertTime(long miliseconds) {
		long second = (miliseconds / 1000) % 60;
		long minute = (miliseconds / (1000 * 60)) % 60;
		long hour = (miliseconds / (1000 * 60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	public void startLoopTimer() {startLoopTime = System.currentTimeMillis();}
	
	// Shows progress of two loops
	public void progress(int i, int iMin, int iMax, int j, int jMin, int jMax) {
		times.add(System.currentTimeMillis() - startLoopTime);
		for(int k=0; k<times.size()-1; k++) {
			times.set(times.size()-1, times.get(times.size()-1) - times.get(k));
		}
		Collections.sort(times);
		
		progress = (double)((i-iMin)*(jMax-jMin)+(j-jMin+1))/((iMax-iMin)*(jMax-jMin));
		
		System.out.println("Progress: " + c(progress*100, 2) + "%, main loop " + i + "\\" + (iMax-1) + 
				", secondary loop " + (j+1) + "\\" + jMax + ", together " + ((i-iMin)*(jMax-jMin)+(j-jMin+1)) + 
				"\\" + ((iMax-iMin)*(jMax-jMin)) + ".");
		/*System.out.println("Time left: " + 
				convertTime((long)((1-progress) * (iMax-iMin) * getAverage(times))) + "\n");*/
		
		System.out.println("Time left: ");
		System.out.println("  - minimum: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getMinimum(times))));
		System.out.println("  - Q1: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getFirstQuartile(times))));
		System.out.println("  - median: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getMedian(times))));
		/*System.out.println("  - mean: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getAverage(times))));*/
		System.out.println("  - Q3: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getThirdQuartile(times))));
		System.out.println("  - maximum: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * (jMax-jMin) * getMaximum(times))));
		System.out.println("Current time:   " + convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	// Shows progress of one loop
	public void progress(int i, int iMin, int iMax) {
		times.add(System.currentTimeMillis() - startLoopTime);
		for(int k=0; k<times.size()-1; k++) {
			times.set(times.size()-1, times.get(times.size()-1) - times.get(k));
		}
		Collections.sort(times);
		
		progress = (double)(i-iMin+1)/(iMax-iMin);
		
		System.out.println("Progress: " + c(progress*100, 2) + "%, loops " + (i+1) + "\\" + iMax + ".");
		/*System.out.println("Time left: " + 
				convertTime((long)((1-progress) * (iMax-iMin) * getAverage(times))) + "\n");*/
		
		System.out.println("Time left: ");
		System.out.println("  - minimum: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getMinimum(times))));
		System.out.println("  - Q1: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getFirstQuartile(times))));
		System.out.println("  - median: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getMedian(times))));
		/*System.out.println("  - mean: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getAverage(times))));*/
		System.out.println("  - Q3: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getThirdQuartile(times))));
		System.out.println("  - maximum: \t" + 
				convertTime((long)((1-progress) * (iMax-iMin) * getMaximum(times))));
		System.out.println("Current time:   " + convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	public void progressSimple(int i, int iMin, int iMax) {
		progress = (double)(i-iMin+1)/(iMax-iMin);
		
		System.out.println("Progress: " + c(progress*100, 2) + "%, loops " + (i+1) + "\\" + iMax);
		System.out.println("Current time:   " + convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	public void progressSimple(int i, int iMin, int iMax, int j, int jMin, int jMax) {
		progress = (double)((i-iMin)*(jMax-jMin)+(j-jMin+1))/((iMax-iMin)*(jMax-jMin));
		
		System.out.println("Progress: " + c(progress*100, 2) + "%, main loop " + i + "\\" + (iMax-1) + 
				", secondary loop " + (j+1) + "\\" + jMax + ", together " + ((i-iMin)*(jMax-jMin)+(j-jMin+1)) + 
				"\\" + ((iMax-iMin)*(jMax-jMin)) + ".");
		System.out.println("Current time:   " + convertTime(System.currentTimeMillis()-startTime) + "\n");
	}
	
	// Converts double to string with custom precision
	public String c(double number, int precision) {
		String temp = "";
		for(int i=0; i<precision; i++) temp += "0";
		if(precision != 0) return new DecimalFormat("#0." + temp).format(number);
		else return new DecimalFormat("#0" + temp).format(number);
	}
	
	// Announces end of the simulation
	private void speek(String filename) {
		AePlayWave aw = new AePlayWave(filename);
        aw.start();
	}
	
	// Plays .mp3 files
	private void speek() {
		speek("tada.wav");
		speek("Voice1.wav");
	}
	
	public void test(String str) {
		System.out.println(str);
	}
	
	public void test() {test("Elo!");}
	
	public void ln() {System.out.println("");}
			
	public String c(double number) {return c(number, 2);}
	
	public void stopSimulationByTime(int hours) {
		stopTimer();
		if((stopTime - startTime)/1000/60/60 > hours) {
			printTime("Done:");
			System.exit(0);
		}
		else printTime("Was working for");
	}
	
	// Returns minimum value from the given list
	public Long getMinimum(ArrayList<Long> list) {
		Long min = list.get(0);
		for(int i=0; i<list.size(); i++)
			if(list.get(i) < min) min = list.get(i);
		return min;
	}
	
	public double getMinimumDouble(ArrayList<Double> list) {
		double min = list.get(0);
		for(int i=0; i<list.size(); i++)
			if(list.get(i) < min) min = list.get(i);
		return min;
	}
	
	public double getMinimumDoubleArray(double[] array) {
		double min = array[0];
		for(int i=0; i<array.length; i++)
			if(array[i] < min) min = array[i];
		return min;
	}
	
	// Return first quantile Q1 from the given list
	public Long getFirstQuartile(ArrayList<Long> list) {
		return list.get((int) Math.floor(list.size()/4.));		
	}
	
	// Return median from the given list
	public Long getMedian(ArrayList<Long> list) {
		return list.get((int) Math.floor(list.size()/2.));		
	}
	
	// Return average from the given list
	public Long getAverage(ArrayList<Long> list) {
		Long temp = (long) 0;
		for(int i=0; i<list.size(); i++)
			temp += list.get(i);
		return temp/list.size();
	}
	
	// Return third quantile Q3 from the given list
	public Long getThirdQuartile(ArrayList<Long> list) {
		return list.get((int) Math.floor(3*list.size()/4.));		
	}
	
	// Returns maximum value from the given list
	public Long getMaximum(ArrayList<Long> list) {
		Long max = list.get(0);
		for(int i=0; i<list.size(); i++)
			if(list.get(i) > max) max = list.get(i);
		return max;
	}
	
	public double getMaximumDouble(ArrayList<Double> list) {
		double max = list.get(0);
		for(int i=0; i<list.size(); i++)
			if(list.get(i) > max) max = list.get(i);
		return max;
	}
	
	public int getMinimumTableIndex(double[] array) {
		double min = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++)
			if(array[i] < min) {min = array[i]; index = i;}
		return index;
	}
	
	public int getMaximumTableIndex(double[] array) {
		double max = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++)
			if(array[i] > max) {max = array[i]; index = i;}
		return index;
	}
	
	// ~ GETTERS ~
	public void endTime(String str)  {
		stopTimer(); 
		System.out.println(str + " " + convertTime(stopTime-startTime)); 
		speek();
	}
	
	public void endTime()  {
		stopTimer(); 
		System.out.println(convertTime(stopTime-startTime)); 
		speek();
	}
	
	public void printTime(String str)  {
		stopTimer(); 
		System.out.println(str + " " + convertTime(stopTime-startTime));
	}
	
	public void printTime() {
		stopTimer(); 
		System.out.println(convertTime(stopTime-startTime));
	}
	
	public void n() {System.out.println("");}
	public void n(int k) {for(int i=0; i<k; i++) System.out.println("");}
	
	private void printArray(double[] array, String str) {
		for(int i=0; i<array.length; i++) 
			System.out.print(c(array[i], 2) + " ");
		System.out.print(str);
	}
	
	private void printArray(int[] array, String str) {
		for(int i=0; i<array.length; i++) 
			System.out.print(array[i] + " ");
		System.out.print(str);
	}
	
	public void printArray(double[] array) {printArray(array, "");}
	public void printArrayln(double[] array) {printArray(array, "\n");}
	public void printArray(int[] array) {printArray(array, "");}
	public void printArrayln(int[] array) {printArray(array, "\n");}
	
	public void printArrayInt(ArrayList<int[]> array) {
		for(int i=0; i<array.size(); i++)
			printArrayln(array.get(i));
	}
	
	public void printArrayDouble(ArrayList<double[]> array) {
		for(int i=0; i<array.size(); i++)
			printArrayln(array.get(i));
	}
	
	public void printListInt(ArrayList<Integer> array) {
		for(int element : array)
			System.out.print(element + " ");
		System.out.println("");
	}
	
	public void printListDouble(ArrayList<Double> array) {
		for(double element : array)
			System.out.print(c(element) + " ");
		System.out.println("");
	}
	
	public long getTimeMS() {return stopTime-startTime;}
	public double getTime() {return (double)(stopTime-startTime)/1000;}
}
