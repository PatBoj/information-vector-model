package Main;

import java.util.ArrayList;

import Dynamics.Dynamics;
import Networks.LatticeNetwork;
import Networks.RandomGraph;
import Networks.ScaleFreeNetwork;
import ProgramingTools.Debug;

public class Experiments 
{
	Debug d;
	
	public Experiments() {
		d = new Debug();
	}
	
	public void test() {
		int N = 200;
		int k = 6;
		int n = 100;
		int dimOpinion = 100;
		double pEdit = 0.05;
		double pNewMessage = 0.1;
		int avg = 2;
		
		ScaleFreeNetwork net = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage);
		dyn.setSaveFile("test/raw_data.txt");
		dyn.saveHeader();
		
		for(int i=0; i<avg; i++) {
			net = new ScaleFreeNetwork(N, k/2);
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(net, 0.2);
			dyn.run(n, pEdit, i, avg, "elo");
		}
		dyn.closeSaveFile();
	}
	
	public void popularity() {
		int N = 200;
		int k = 6;
		int n = 300000;
		int dimOpinion = 100;
		double pEdit = .0;
		double pNewMessage = .1;
		int avg = 10;
		
		double[] th = {-0.75, -0.50, -0.25, 0, 0.25, 0.50, 0.75};
		
		ScaleFreeNetwork net = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage);
		dyn.setSaveFile("results\\12_01_2021\\none_th.txt");		
		dyn.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				net = new ScaleFreeNetwork(N, k/2);
				dyn.setNewNetwork(net);
				dyn.setInitialConditions(net, th[j]);
				dyn.run(n, pEdit, i+1, avg, "non");
			}
		}
		dyn.closeSaveFile();
		
		ScaleFreeNetwork net2 = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn2 = new Dynamics(net2, dimOpinion, pNewMessage);
		dyn2.setSaveFile("Results\\12_01_2021\\all_th.txt");
		dyn2.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				net2 = new ScaleFreeNetwork(N, k/2);
				dyn2.setNewNetwork(net2);
				dyn2.setInitialConditions(net2, th[j]);
				dyn2.run(n, 0.05, i+1, avg, "all");
			}
		}
		dyn.closeSaveFile();
		
		d.endTime();
	}
}
