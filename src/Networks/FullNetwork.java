package Networks;

public class FullNetwork extends Network 
{
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public FullNetwork(int numberOfNodes) {
		super(numberOfNodes);
		assignLinks();
		typeOfTopology = "full";
	}
	
	// Copy constructor
	public FullNetwork(FullNetwork network) {
		super(network);
	}
	
	// Default constructor
	public FullNetwork() {super();}
	
	// ~ METHODS ~
	// Connects all nodes
	private void assignLinks() {
		averageDegree = (N-1) / 2.;
			
		for(int i=0; i<N; i++)
			for(int j=i+1; j<N; j++)
				addLinkNC(i, j);
	}
}
