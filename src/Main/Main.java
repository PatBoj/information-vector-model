package Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String args[]) throws InterruptedException {
		//Test t = new Test();
		//t.testDistance();
		int n = 2;
		
		double[] tau = new double[201];
		for(int i=0; i<tau.length; i++)
			tau[i] = -1 + i * 0.01;
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0, n));
		}
		/*
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0.05, n));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0, n));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0.05, n));
		}
		/*
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "SQ", 0, n));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "SQ", 0.05, n));
		}
		*/
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}
}