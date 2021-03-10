package Networks;

public class LatticeNetwork extends Network
{
	// ~ DATA FIELDS ~ 
	int n;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public LatticeNetwork(int numberOfNodes) {
		super(numberOfNodes);
		n = (int)Math.sqrt(N);
		if(N != n*n) 
			throw new RuntimeException("Number of nodes does not have integer square root.");
		assignLinks();
		typeOfTopology = "SQ";
	}
	
	// Copy constructor
	public LatticeNetwork(LatticeNetwork network) {
		super(network);
		this.n = network.n;
	}
	
	// Default constructor
	public LatticeNetwork() {super();}
	
	// ~ METHODS ~
	// Assigns links to lattice graph
	private void assignLinks() {
		for(int j=1; j<n+1; j++) 
			for(int i=1; i<n; i++) 
				addLinkNC(i + (j-1)*n-1, i+1 + (j-1)*n-1);
		
		for(int j=1; j<n; j++)
			for(int i=1; i<=n; i++) 
				addLinkNC(i + (j-1)*n-1, i + j*n-1);
		
		averageDegree = 2. * E / N;
	}
}
