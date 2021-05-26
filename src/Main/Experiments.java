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
		String folder = "results/tests/";
		Save s = new Save(folder + net.getTopologyType() + "_" + id + "_tau_" + Tools.convertToString(threshold) + (pEdit == 0 ? "_non" : "_all") + ".txt");
		
		dyn.setInitialOpinions(initialTime);
		dyn.saveParameters(s, "with_competition", realisations, timeSteps);
		
		dyn.run(s, timeSteps);
		s.closeWriter();
		
		Time.count();
		Time.globalProgress();
	}
	
	public static void runExperiment() throws InterruptedException {
		int N = 5;
		double dt = 0.2;
		int n = (int)(2/dt+1);
		
		Time.setMaxIterations(4 * n * N);
		
		double[] tau = new double[n];
		for(int i=0; i<n; i++)
			tau[i] = -1 + i * dt;
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		for(int i=0; i<n; i++) {
			for(int j=0 ; j<N; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0, N, 0));
		}
		
		for(int i=0; i<n; i++) {
			for(int j=0 ; j<N; j++)
				executor.execute(new Experiments(j+1, tau[i], "ER", 0.05, N, 0));
		}
		
		for(int i=0; i<n; i++) {
			for(int j=0 ; j<N; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0, N, 0));
		}
		
		for(int i=0; i<n; i++) {
			for(int j=0 ; j<N; j++)
				executor.execute(new Experiments(j+1, tau[i], "BA", 0.05, N, 0));
		}
		
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		
		Time.speek();
	}
}
