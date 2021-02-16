package Main;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ProgramingTools.Debug;

public class Main {
	public static void main(String args[]) throws InterruptedException {
		//Experiments ex = new Experiments();
		//ex.testHeaders();
		
		Save s = new Save("ELO/text.txt");
		s.writeDataln("JEstem");
		s.closeWriter();
	}
}