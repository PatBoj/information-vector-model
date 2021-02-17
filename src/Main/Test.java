package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Dynamics.Dynamics;
import Dynamics.Message;
import Networks.FullNetwork;
import Networks.LatticeNetwork;
import Networks.Network;
import Networks.RandomGraph;
import Networks.ScaleFreeNetwork;
import ProgramingTools.Debug;

public class Test 
{
	private Debug d;
	
	int N;
	int k;
	int timeSteps;
	int dimOpinion;
	double pEdit;
	double pNewMessage;
	int realisations;
	
	public Test() {
		d = new Debug();
		
		N = 1000;
		k = 6;
		timeSteps = 300000;
		dimOpinion = 100;
		pEdit = 0.05;
		pNewMessage = 0.1;
		realisations = 10;
	}
	
	public void testThreadSpeed() {
		realisations = 24;
		
		RandomGraph  net = new RandomGraph(N, (double)k/(N-1));
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		
		for(int i=0; i<realisations; i++) {
			dyn.setSaveFile("results/16_02_2021/test" + (i+1) + ".txt");
			dyn.saveParameters("time", realisations, timeSteps);
			dyn.saveHeader();
			
			d.startLoopTimer();
			net = new RandomGraph(N, (double)k/(N-1));
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(0.2);
			dyn.run(timeSteps);
			d.progress(i, 0, realisations);
			dyn.closeSaveFile();
		}
	}
	
	public void testHeaders() {
		RandomGraph  net = new RandomGraph(N, (double)k/(N-1));
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		
		timeSteps = 10000;
		
		for(int i=0; i<realisations; i++) {
			dyn.setSaveFile("results/16_02_2021/test" + i + ".txt");
			dyn.saveParameters("time", realisations, timeSteps);
			dyn.saveHeader();
			
			d.startLoopTimer();
			net = new RandomGraph(N, (double)k/(N-1));
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(0.2);
			dyn.run(timeSteps);
			d.progress(i, 0, realisations);
			dyn.closeSaveFile();
		}
	}
	
	public void isingTest() {
		int N = 10000;
		int E = 200;
		int n = 1000;
		int dimOpinion = 100;
		double pEdit = 0.05;
		double pNewMessage = 0.1;
		int avg = 10;
		
		RandomGraph net = new RandomGraph(N, E);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		dyn.setInitialConditions(0.2, "ising");
		/*dyn.setSaveFile("test/raw_data.txt");
		dyn.saveHeader();
		
		d.startLoopTimer();
		for(int i=0; i<avg; i++) {
			net = new ScaleFreeNetwork(N, k/2);
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(net, 0.2);
			dyn.run(n, pEdit, i+1, avg, "elo");
			d.progress(i, 0, avg);
		}
		dyn.closeSaveFile();*/
	}
	
	public void speedTest() {
		int N = 10000;
		int k = 6;
		int n = 1000000;
		int dimOpinion = 100;
		double pEdit = 0.05;
		double pNewMessage = 0.1;
		int avg = 10;
		
		ScaleFreeNetwork net = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		dyn.setSaveFile("test/raw_data.txt");
		dyn.saveHeader();
		
		d.startLoopTimer();
		for(int i=0; i<avg; i++) {
			net = new ScaleFreeNetwork(N, k/2);
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(net, 0.2);
			dyn.run(n);
			d.progress(i, 0, avg);
		}
		dyn.closeSaveFile();
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
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		dyn.setSaveFile("test/raw_data.txt");
		dyn.saveHeader();
		
		for(int i=0; i<avg; i++) {
			net = new ScaleFreeNetwork(N, k/2);
			dyn.setNewNetwork(net);
			dyn.setInitialConditions(net, 0.2);
			dyn.run(n);
		}
		dyn.closeSaveFile();
	}
	
	public void popularity() {
		int N = 1000;
		int k = 6;
		int n = 300;
		int dimOpinion = 100;
		double pEdit = .0;
		double pNewMessage = .1;
		int avg = 10;
		
		double[] th = {-0.75, -0.50, -0.25, 0, 0.25, 0.50, 0.75};
		
		ScaleFreeNetwork net = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		dyn.setSaveFile("results\\08_02_2021\\none_th_BA.txt");		
		dyn.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				d.startLoopTimer();
				net = new ScaleFreeNetwork(N, k/2);
				dyn.setNewNetwork(net);
				dyn.setInitialConditions(net, th[j]);
				dyn.run(n);
				d.progress(j, 0, th.length, i, 0, avg);
			}
		}
		dyn.closeSaveFile();
		
		ScaleFreeNetwork net2 = new ScaleFreeNetwork(N, k/2);
		Dynamics dyn2 = new Dynamics(net2, dimOpinion, pNewMessage, pEdit);
		dyn2.setSaveFile("Results\\08_02_2021\\all_th_BA.txt");
		dyn2.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				d.startLoopTimer();
				net2 = new ScaleFreeNetwork(N, k/2);
				dyn2.setNewNetwork(net2);
				dyn2.setInitialConditions(net2, th[j]);
				dyn2.run(n);
				d.progress(j, 0, th.length, i, 0, avg);
			}
		}
		dyn.closeSaveFile();
		
		d.endTime();
	}
	
	public void popularityER() {
		int N = 1000;
		int k = 6;
		int n = 300000;
		int dimOpinion = 100;
		double pEdit = .0;
		double pNewMessage = .1;
		int avg = 10;
		
		double[] th = {-0.75, -0.50, -0.25, 0, 0.25, 0.50, 0.75};
		
		RandomGraph net = new RandomGraph(N, k);
		Dynamics dyn = new Dynamics(net, dimOpinion, pNewMessage, pEdit);
		dyn.setSaveFile("results\\08_02_2021\\none_th_ER.txt");		
		dyn.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				d.startLoopTimer();
				net = new RandomGraph(N, k);
				dyn.setNewNetwork(net);
				dyn.setInitialConditions(net, th[j]);
				dyn.run(n);
				d.progress(j, 0, th.length, i, 0, avg);
			}
		}
		dyn.closeSaveFile();
		
		RandomGraph net2 = new RandomGraph(N, k);
		Dynamics dyn2 = new Dynamics(net2, dimOpinion, pNewMessage, pEdit);
		dyn2.setSaveFile("Results\\08_02_2021\\all_th_ER.txt");
		dyn2.saveHeader();
		
		for(int j=0; j<th.length; j++) {
			for(int i=0; i<avg; i++) {
				d.startLoopTimer();
				net2 = new RandomGraph(N, k);
				dyn2.setNewNetwork(net2);
				dyn2.setInitialConditions(net2, th[j]);
				dyn2.run(n);
				d.progress(j, 0, th.length, i, 0, avg);
			}
		}
		dyn.closeSaveFile();
		
		d.endTime();
	}
}
