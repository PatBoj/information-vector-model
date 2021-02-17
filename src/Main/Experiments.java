package Main;

import Dynamics.Dynamics;
import Networks.RandomGraph;
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
	
	Experiments(int id) {
		d = new Debug(0);
		this.id = id;
		
		N = 1000;
		k = 6;
		timeSteps = 300000;
		dimOpinion = 100;
		pEdit = 0.05;
		pNewMessage = 0.1;
		realisations = 10;
	}

	public void run() {
		RandomGraph  net = new RandomGraph(N, (double)k/(N-1));
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		
		dyn.setSaveFile("results/16_02_2021/test" + id + ".txt");
		dyn.saveParameters("time", realisations, timeSteps);
		dyn.saveHeader();
			
		net = new RandomGraph(N, (double)k/(N-1));
		dyn.setNewNetwork(net);
		dyn.setInitialConditions(0.2);
		dyn.run(timeSteps);
		dyn.closeSaveFile();
	}
}
