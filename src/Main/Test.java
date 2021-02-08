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
	
	Test() {
		d = new Debug(); 
	}
	
	public void test() {
		ArrayList<Integer> test = new ArrayList<Integer>();
		
		test.add(10);
		test.add(5);
		test.add(2);
		test.add(4);
		test.add(15);
		
		for(int i : test)
			System.out.print(i + "\t");
		System.out.println();
		
		Collections.sort(test);
		for(int i : test)
			System.out.print(i + "\t");
		System.out.println();
	}
}
