package Networks;

import java.util.ArrayList;

public class RandomGraph extends Network 
{
	// ~ DATA FIELDS ~
	private double probability;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public RandomGraph(int numberOfNodes, int numberOfLinks) {
		super(numberOfNodes);
		if (numberOfLinks > N*(N-1)/2) 
			throw new RuntimeException("Number of links is greater than possible links");
		E = numberOfLinks;
		averageDegree = 0;
		nodes = new ArrayList<Node>(N);
		for(int i=0; i<N; i++) nodes.add(new Node());
		globalLinks = new ArrayList<Link>();
		components = new ArrayList<Integer>();
		assignLinks(numberOfLinks);
		typeOfTopology = "ER";
	}
	
	// Constructor #2
	public RandomGraph(int numberOfNodes, double probability) {
		super(numberOfNodes);
		if(probability < 0 || probability > 1)
			throw new RuntimeException("Probability must be between 0 and 1.");
		E = 0;
		averageDegree = 0;
		this.probability = probability;
		nodes = new ArrayList<Node>(N);
		for(int i=0; i<N; i++) nodes.add(new Node());
		globalLinks = new ArrayList<Link>();
		components = new ArrayList<Integer>();
		assignLinks(probability);
		typeOfTopology = "ER";
	}
	
	// Constructor #3
	public RandomGraph(int numberOfNodes) {super(numberOfNodes);}
	
	// Copy constructor
	public RandomGraph(RandomGraph network) {
		super(network);
		this.probability = network.probability;
	}
	
	// Default constructor
	public RandomGraph() {super();}
	
	// ~ METHODS ~
	// Assigns links to nodes by given probability
	private void assignLinks(double probability) {
		for(int i=0; i<N; i++)
			for(int j=i+1; j<N; j++)
				if(rnd.nextDouble() < probability)
					addLinkNC(i, j);
		averageDegree = 2. * E / N; // average degree <k> = 2E/N
	}
		
	// Assigns links to nodes by given amount of links
	private void assignLinks(int numberOfLinks) {
		int i = 0;
		int j = 0;
		int currentLinks = 0;
		averageDegree = 2. * numberOfLinks / N; // average degree <k>=2E/N
		probability = averageDegree / (N-1); // probability p = 2E/[N(N-1)] = <k>/(N-1)
			
		while(currentLinks < numberOfLinks) {
			i = rnd.nextInt(N-1);
			j = rnd.nextInt(N-1);
				
			if(i != j && nodes.get(i).checkLink(i, j) == -1) {
				globalLinks.add(new Link(i, j));
				nodes.get(i).addLinkNC(globalLinks.get(globalLinks.size()-1));
				nodes.get(j).addLinkNC(globalLinks.get(globalLinks.size()-1));
				currentLinks++;
			}
		}
	}
	
	// ~ GETTERS ~
	public double getProbability() {return probability;}
}
