package ProgramingTools;

public class Time {
	// Times of the chunk of code running
	Long[] times;
	
	// Number of code running
	int[] codeRun;
	
	// Name of this chunk of code
	String[] names;
	
	// Number of codes to compare
	int N;
	
	// Current temp time
	Long currentTime;
	
	// Debug class to convert time
	Debug d;
	
	// Constructor #1
	public Time(int N) {
		if(N <= 0) throw new Error("Number of tested chunked of code should be greather than zero.");
		this.N = N;
		
		times = new Long[N];
		codeRun = new int[N];
		names = new String[N];
		
		for(int i=0; i<N; i++) {
			times[i] = (long) 0;
			codeRun[i] = 0;
		}
		
		d = new Debug(0);
	}
	
	// Default constructor
	public Time() {this(1);}
	
	public void setNames(String[] names) {
		if(names.length != N)
			throw new Error("Length of the names vector should match");
		this.names = names.clone();
	}
	
	public void startTimer() {currentTime = System.currentTimeMillis();}
	
	public void pauseTimer(int i) {
		times[i] += System.currentTimeMillis() - currentTime;
		codeRun[i] += 1;
	}
	
	public void printTimeResults() {
		System.out.println("Name\tTime\tRealisations");
		for(int i=0; i<N; i++)
			System.out.println(names[i] + "\t" + d.convertTime(times[i]) + "\t" + codeRun[i]);
		System.out.println("");
	}
}
