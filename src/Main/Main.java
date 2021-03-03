package Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

import ProgramingTools.Debug;
import ProgramingTools.Mailer;

public class Main {
	public static void main(String args[]) throws InterruptedException {

		int n = 2;
		double[] tau = new double[11];
		for(int i=0; i<tau.length; i++)
			tau[i] = -1 + i * 0.2;
		
		int n = 24;
		
		Debug d = new Debug();
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(int i=0; i<n; i++) {
			executor.execute(new Experiments(i+1));
		}
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		d.endTime("Wielow¹tkowo:");
		
		d.startTimer();
		
		Test t = new Test();
		t.testThreadSpeed();
		
		d.endTime("Klasycznie:");
	}
}