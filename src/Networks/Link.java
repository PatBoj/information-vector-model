package Networks;
public class Link 
{
	// ~ DATA FIELDS ~
	
	// Number of connected nodes
	private int[] node;
	private double weight;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	Link(int node1, int node2, double weight) {
		node = new int[2];
		node[0] = node1;
		node[1] = node2;
		this.weight = weight;
	}
	
	// Constructor #2
	Link(int node1, int node2) {this(node1, node2, 1);}
	
	// Copy constructor
	Link(Link link) {
		this.node = link.node.clone();
		this.weight = link.weight;
	}
	
	// Default constructor
	public Link() {this(-1, -1, 0);}
	
	// ~ METHODS ~
	
	// Checking is there a connection between node1 and node2 given by integers
	boolean checkConnection(int node1, int node2) {
		if(node1 == node2) return false;
		if(node[0] == node1)
			if(node[1] == node2) return true;
			else return false;
		else if(node[0] == node2)
			if(node[1] == node1) return true;
			else return false;
		else return false;
	}
	
	// Checking is there a connection between node1 and node2 given by table
	boolean checkConnection(int[] node) {return checkConnection(node[0], node[1]);}
	
	// Checking is there a connection between node1 and node2 given by link object
	boolean checkConnection(Link link) {return checkConnection(link.getConnection());}
	
	// ~ GETTERS ~
	int[] getConnection() {return node;}
	double getWeight() {return weight;} 
}