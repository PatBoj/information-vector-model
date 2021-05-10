package Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Dynamics.Dynamics;
import Networks.LatticeNetwork;
import Networks.Network;
import Networks.RandomGraph;
import Networks.ScaleFreeNetwork;
import ProgramingTools.Debug;

public class Experiments implements Runnable {
	
	private Debug d;
	private int id;
	
	private int N;
	private int k;
	private int timeSteps;
	private int dimOpinion;
	private double pEdit;
	private double pNewMessage;
	private int realisations;
	private double threshold;
	
	private String topologyType;
	private Network net;
	
	Experiments(int id, double tau, String topologyType, double pEdit, int realisations) {
		d = new Debug(0);
		this.id = id;
		
		N = 1000;
		k = 6;
		timeSteps = 1000;
		dimOpinion = 100;
		this.pEdit = pEdit;
		pNewMessage = 0.1;
		this.realisations = realisations;
		threshold = tau;
		this.topologyType = topologyType;
		net = new Network();
	}

	public void run() {		
		if(topologyType.equals("ER"))
			net = new RandomGraph(N, (double)k/(N-1));
		else if(topologyType.equals("BA"))
			net = new ScaleFreeNetwork(N, k/2);
		else if(topologyType.equals("SQ")) {
			N = 1024;
			net = new LatticeNetwork(N);
		}
		
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		
		dyn.setSaveFile("results/20_04_2021/" + net.getTopologyType() + "_" + id + "_tau_" + d.c(threshold) + (pEdit == 0 ? "_non" : "_all") + ".txt");
		
		dyn.setInitialConditions(threshold);
		dyn.saveParameters("time", realisations, timeSteps);
		dyn.saveHeader();
		
		dyn.run(timeSteps);
		dyn.closeSaveFile();
	}
	
	public void runExperiment() throws InterruptedException {
		int n = 2;
		double[] tau = new double[11];
		for(int i=0; i<tau.length; i++)
			tau[i] = -1 + i * 0.2;
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0, realisations));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0.05, realisations));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0, realisations));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0.05, realisations));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "SQ", 0, realisations));
		}
		
		for(int i=0; i<tau.length; i++) {
			for(int j=0 ; j<n; j++)
				executor.execute(new Experiments(j+1, tau[i], "SQ", 0.05, realisations));
		}
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}
}
