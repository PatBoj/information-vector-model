package Main;

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
	
	Experiments(int id, double tau, String topologyType, double pEdit) {
		d = new Debug(0);
		this.id = id;
		
		N = 1000;
		k = 6;
		timeSteps = 1000000;
		dimOpinion = 100;
		this.pEdit = pEdit;
		pNewMessage = 0.1;
		realisations = 20;
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
		
		dyn.setSaveFile("results/16_02_2021/" + net.getTopologyType() + "_" + id + "_tau_" + d.c(threshold) + (pEdit == 0 ? "_non" : "_all") + ".txt");
		dyn.saveParameters("time", realisations, timeSteps);
		dyn.saveHeader();
		
		dyn.setInitialConditions(threshold);
		dyn.run(timeSteps);
		dyn.closeSaveFile();
	}
}
