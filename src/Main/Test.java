package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import Dynamics.Dynamics;
import Dynamics.Message;
import Networks.FullNetwork;
import Networks.LatticeNetwork;
import Networks.Network;
import Networks.RandomGraph;
import Networks.ScaleFreeNetwork;
import ProgramingTools.Tools;

public class Test 
{
	private Tools d;
	
	int N;
	int k;
	int timeSteps;
	int dimOpinion;
	double pEdit;
	double pNewMessage;
	int realisations;
	
	public Test() {		
		N = 1000;
		k = 6;
		timeSteps = 300000;
		dimOpinion = 100;
		pEdit = 0.05;
		pNewMessage = 0.1;
		realisations = 10;
	}
	
	public void testDistance() {
		int N = 12;
		
		Network net = new Network(N);
		net.addLinkNC(0, 1);
		net.addLinkNC(0, 2);
		net.addLinkNC(0, 3);
		net.addLinkNC(0, 4);
		net.addLinkNC(0, 5);
		net.addLinkNC(1, 2);
		net.addLinkNC(2, 3);
		net.addLinkNC(3, 4);
		net.addLinkNC(4, 6);
		net.addLinkNC(5, 6);
		net.addLinkNC(6, 7);
		net.addLinkNC(7, 8);
		net.addLinkNC(7, 9);
		net.addLinkNC(8, 9);
		net.addLinkNC(10, 11);
		
		for(int i=0; i<N; i++) {
			net.resetDistances();
			net.computeDistance(i);
			System.out.println("\tNODE: " + i);
			for(int j=0; j<N; j++)
				System.out.println("Node " + j + ": " + net.getNode(j).getDistance());
			System.out.println();
		}	
	}
	
	public void testDistanceClever() {
		int N = 4;
		
		Network net = new Network(N);
		net.addLinkNC(0, 1);
		net.addLinkNC(1, 3);
		net.addLinkNC(2, 3);
		net.addLinkNC(0, 2);
		
		for(int i=0; i<N; i++) {
			net.computeDistance(i);
			System.out.println("\tNODE: " + i);
			for(int j=0; j<N; j++)
				System.out.println("Node " + j + ": " + net.getNode(j).getDistance());
			System.out.println();
		}	
	}
	
	public void testQueque() {
		LinkedList<Integer> queue = new LinkedList<Integer>();
		for(int i=0; i<10; i++)
			queue.add(i * i);
		int temp = 0;
		
		while(queue.peek() != null) {
			temp = queue.poll();
			if(temp == 4) queue.add(5);
			System.out.print(temp + " ");
		}
	}
	
	public void testERDistance() {
		int N = 1000;
		int k = 6;
		
		RandomGraph net = new RandomGraph(N, (double)k/(N-1));
		
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(int i=0; i<N; i++) {
			net.resetDistances();
			net.computeDistance(i);
			for(int j=0; j<N; j++)
				distances.add(net.getNode(j).getDistance());
		}
		distances.removeIf(distance -> distance.equals(-1));
		distances.removeIf(distance -> distance.equals(0));
		System.out.println("Average: " + d.getAverageInteger(distances) + "\n");
			
		d.displaySimpleHistogram(distances);
		
		/*
		for(int i=0; i<N; i++) {
			net.resetDistances();
			net.computeDistance(i);
			System.out.println("\tNODE: " + i);
			for(int j=0; j<N; j++)
				System.out.println("Node " + j + ": " + net.getNode(j).getDistance());
			System.out.println();
		}
		*/
	}
	
	public void testHistogram() {
		Tools.displaySimpleHistogram(new int[] {5, 5, 6, 7, 8, 9, 9, 9, 10});
	}
	/*
	public void testClosestSimilarity() {
		int N = 1000;
		int k = 6;
		int dimOpinion = 100;
		int n = 20;
		
		Save s1 = new Save("results/ising_test/average_ER.txt");
		
		d.startLoopTimer();
		for(int i=0; i<n; i++) {
			RandomGraph rg = new RandomGraph(N, (double)k/(N-1));
			Dynamics dyn1 = new Dynamics(rg, dimOpinion, 0, 0);
			dyn1.setIsingInitialOpinions(rg, s1);
			d.progress(i, 0, n);
		}
		s1.closeWriter();
		
		Save s2 = new Save("results/ising_test/average_BA.txt");
		
		d.startLoopTimer();
		for(int i=0; i<n; i++) {
			RandomGraph ba = new RandomGraph(N, k/2);
			Dynamics dyn1 = new Dynamics(ba, dimOpinion, 0, 0);
			dyn1.setIsingInitialOpinions(ba, s2);
			d.progress(i, 0, n);
		}
		s2.closeWriter();
	}*/
	
	/*
	 private double[] compressedSensing() {
		double[] X = new double[N-1];
		int counter = 0;
			
		BufferedReader reader = null;
	    Process shell = null;
	    try {
	    	shell = Runtime.getRuntime().exec(
	    			new String[] { "C:\\Program Files\\R\\R-3.6.0\\bin\\x64\\Rscript.exe", 
	    				"D:\\Studia\\Programowanie\\Thesis\\R\\compute_matrix.R"});

	    	reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
	    	String line;
	    	while ((line = reader.readLine()) != null) {
	    		if(counter < N-1) X[counter] = Double.parseDouble(line);
	    		else {break;}
	    		counter++;
	    	}
	    	
	    }
	    catch (IOException e) {e.printStackTrace();}
			
	    return X;
	}
	 */
}
