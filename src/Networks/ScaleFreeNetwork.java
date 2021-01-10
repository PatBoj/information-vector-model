package Networks;

import java.util.ArrayList;
import java.util.Random;

public class ScaleFreeNetwork extends Network 
{
	// ~ DATA FIELDS ~
	private int initialNodes;
	private int addingConnections;	
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public ScaleFreeNetwork(int numberOfNodes, int initialNodes, int addingConnections) {
		super(numberOfNodes);
		if (initialNodes < 1) 
			throw new RuntimeException("Invalid initial number of nodes, it must at least one");
		if (addingConnections > initialNodes) 
			throw new RuntimeException("Invalid number of adding connectons, it must be greater or equal to inital nodes");
		this.initialNodes = initialNodes;
		this.addingConnections = addingConnections;
		averageDegree = 0;
		nodes = new ArrayList<Node>(N);
		globalLinks = new ArrayList<Link>();
		components = new ArrayList<Integer>();
		rnd = new Random();
		rnd.setSeed(123456789);
		assignLinks(E);
		typeOfTopology = "BA";
	}
	
	// Constructor #2
	public ScaleFreeNetwork(int numberOfNodes, int addingConnections) {this(numberOfNodes, addingConnections, addingConnections);}
	
	// Constructor #3
	public ScaleFreeNetwork(int numberOfNodes) {this(numberOfNodes, 2, 1);}
	
	// Copy constructor
	public ScaleFreeNetwork(ScaleFreeNetwork network) {
		super(network);
		this.initialNodes = network.initialNodes;
		this.addingConnections = network.addingConnections;
	}
	
	// Default constructor
	public ScaleFreeNetwork() {super();}
	
	// ~ METHODS ~
	// Assigns links to nodes
	private void assignLinks(double probability) {
		// Connects all initial links together
		for(int i=0; i<initialNodes; i++) nodes.add(new Node());
		if(initialNodes > 1)
			for(int i=0; i<initialNodes; i++)
				for(int j=i+1; j<initialNodes; j++)
					addLinkNC(i, j);
		
		int[] toNode = new int[addingConnections];
		
		// Time evolution of network
		for(int i=initialNodes; i<N; i++) {
			nodes.add(new Node());
			for(int j=0; j<addingConnections; j++) {
				toNode[j] = globalLinks.get(rnd.nextInt(getNumberOfLinks()))
						.getConnection()[rnd.nextInt(2)];
				for(int k=0; k<j; k++)
					if(toNode[j] == toNode[k]) {j--; break;}
			}
			for(int j=0; j<addingConnections; j++)
				addLinkNC(toNode[j], i);
		}
			
		averageDegree = 2. * E / N; // average degree <k> = 2E/N
	}
	
	// ~ GETTERS ~
	public int getInitialNodes() {return initialNodes;}
	public int getAddingConnections() {return addingConnections;}
}
