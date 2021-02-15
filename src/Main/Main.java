package Main;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ProgramingTools.Debug;

public class Main {
	public static void main(String args[]) throws InterruptedException {
		//Experiments ex = new Experiments();
		//ex.popularity();
		
		Debug d = new Debug();
		Random rnd = new Random();
		rnd.setSeed(1);
		
		int n = 10;
		int[] times = new int[n];
		for(int i=0; i<n; i++)
			times[i] = rnd.nextInt(4) + 1;
		
		for(int i=0; i<n; i++) {
			d.startLoopTimer();
			TimeUnit.SECONDS.sleep(times[i]);
			d.progress(i, 0, n);
		}
	}
}