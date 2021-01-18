package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
		final Object[][] table = new String[4][];
		table[0] = new String[] { "foo", "bar", "baz" };
		table[1] = new String[] { "bar2", "foo2", "baz2" };
		table[2] = new String[] { "baz3", "bar3", "foo3" };
		table[3] = new String[] { "foo4", "bar4", "baz4" };

		for (final Object[] row : table) {
		    System.out.format("%-20s %s %s", row);
		    System.out.println();
		}
	}
}
