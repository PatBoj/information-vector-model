package Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Dynamics.Dynamics;
import Networks.Network;
import Networks.RandomGraph;
import Networks.ScaleFreeNetwork;
import ProgramingTools.Time;
import ProgramingTools.Tools;

public class Experiments implements Runnable {
	private int id;
	
	private int N;
	private int k;
	private int timeSteps;
	private int dimOpinion;
	private double pEdit;
	private double pNewMessage;
	private int realisations;
	private double threshold;
	private int initialTime;
	
	private String topologyType;
	private Network net;
	
	Experiments(int id, double tau, String topologyType, double pEdit, int realisations, int initialTime) {
		this.id = id;
		
		N = 1000;
		k = 6;
		timeSteps = 1000;
		dimOpinion = 100;
		this.pEdit = pEdit;
		pNewMessage = 0.1;
		this.realisations = realisations;
		threshold = tau;
		this.initialTime = initialTime;
		this.topologyType = topologyType;
		net = new Network();
	}

	public void run() {		
		if(topologyType.equals("ER"))
			net = new RandomGraph(N, (double)k/(N-1));
		else if(topologyType.equals("BA"))
			net = new ScaleFreeNetwork(N, k/2);
		
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit, threshold);
		String folder = "results/24_05_2021/";
		Save s = new Save(folder + net.getTopologyType() + "_" + id + "_tau_" + Tools.convertToString(threshold) + (pEdit == 0 ? "_non" : "_all") + ".txt");
		
		dyn.setInitialOpinions(initialTime);
		dyn.saveParameters(s, "with_competition", realisations, timeSteps);
		
		dyn.run(s, timeSteps);
		s.closeWriter();
	}
	
	public static void runExperiment() throws InterruptedException {
		int n = 2;
		double[] tau = new double[11];
		for(int i=0; i<tau.length; i++)
			tau[i] = -1 + i * 0.2;
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0, n, 0));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0.05, n, 0));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0, n, 0));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0.05, n, 0));
		}
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		Time.speek();
	}
}
