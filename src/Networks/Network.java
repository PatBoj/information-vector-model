package Networks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import Main.Save;

public class Network {
	// ~ DATA FIELDS ~

	// Number of nodes in the network
	protected int N;

	// Total number of all connections in the network
	protected int E;

	// Average number of neighbors of every node
	protected double averageDegree;

	// List of every link in the network
	protected ArrayList<Link> globalLinks;

	// List of all nodes in the network
	protected ArrayList<Node> nodes;

	// Array of sizes of components in the network
	// The size of this array is the total number of components in the network
	protected ArrayList<Integer> components;

	// Just random number
	protected Random rnd;

	// Adjacency matrix, where 1 mean there is a connection and 0 means that isn't
	// Matrix should be symmetric and it should has zeros on the diagonal
	int[][] adjacencyMatrix;
	
	// Topology type
	String typeOfTopology;

	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Network(int numberOfNodes) {
		N = numberOfNodes;
		E = 0;
		averageDegree = 0;
		nodes = new ArrayList<Node>(N);
		for (int i = 0; i < N; i++)
			nodes.add(new Node());
		globalLinks = new ArrayList<Link>();
		components = new ArrayList<Integer>();
		rnd = new Random();
		typeOfTopology = "custom";
		//randomNumber.setSeed(123456789);
	}
	
	// Copy constructor
	public Network(Network network) {
		this.N = network.N;
		this.E = network.E;
		this.averageDegree = network.averageDegree;
		nodes = new ArrayList<Node>(N);
		for(Node node : network.nodes)
			nodes.add(new Node(node));
		globalLinks = new ArrayList<Link>();
		for(Link link : network.globalLinks)
			globalLinks.add(new Link(link));
		components = new ArrayList<Integer>();
		for(int component : network.components)
			components.add(component);
		this.rnd = network.rnd;
	}

	// Default constructor
	public Network() {
		this(0);
	}

	// ~ METHODS ~
	/*******************/
	/***** NETWORK *****/
	/*******************/

	// Adds link without checking if connection exists
	public void addLinkNC(int node1, int node2) {
		// Errors and exceptions
		if (node1 < 0 || node2 < 0 || node1 > N || node2 > N)
			throw new Error("Adding link - out of range");
		if (node1 == node2)
			throw new Error("Cannot create connection to the same node");

		globalLinks.add(new Link(node1, node2));
		nodes.get(node1).addLinkNC(globalLinks.get(E));
		nodes.get(node2).addLinkNC(globalLinks.get(E));
		E++;
	}

	// Add link and checks if that connection exists
	// if it exist error will appear
	protected void addLink(int node1, int node2) {
		// Errors and exceptions
		if (nodes.get(node1).checkLink(node1, node2) == -1)
			throw new Error("Such connection already exists");

		addLinkNC(node1, node2);
	}

	// Deletes link
	protected void deleteLink(int node1, int node2) {
		// Errors and exceptions
		if (node1 < 0 || node2 < 0 || node1 > N || node2 > N)
			throw new RuntimeException("Deleting link - out of range");
		if (!nodes.get(node1).deleteLink(node1, node2))
			throw new Error("Such connection does not exist so it cannot be deleted");

		E--;
	}

	// Deletes link based on position in the links list
	public void deleteLink(int i) {
		globalLinks.remove(i);
		E--;
	}

	// Deletes all connections (clears array)
	public void clearNodes() {
		for (int i = 0; i < N; i++)
			nodes.get(i).clearLinks();
		globalLinks.clear();
		E = 0;
	}

	// Computes number of components and it's sizes
	protected void computeComponents() {
		components.clear();
		for (int i = 0; i < N; i++)
			nodes.get(i).setComponentAssociation(-1);
		Queue<Integer> queue = new PriorityQueue<Integer>();
		int t;
		int com;

		for (int i = 0; i < N; i++) {
			if (nodes.get(i).getComponentAssociation() == -1) {
				com = 1;
				nodes.get(i).setComponentAssociation(components.size() + 1);
				queue.offer(i);
				while (queue.peek() != null) {
					t = queue.poll();
					for (int j = 0; j < nodes.get(t).getNodeDegree(); j++) {
						for (int l = 0; l < 2; l++) {
							if (nodes.get(nodes.get(t).getConnection(j)[l]).getComponentAssociation() == -1) {
								com++;
								nodes.get(nodes.get(t).getConnection(j)[l])
										.setComponentAssociation(components.size() + 1);

								queue.offer(nodes.get(t).getConnection(j)[l]);
							}
						}
					}
				}
				components.add(com);
			}
		}
	}
	
	public void computeDistance(int index) {
		resetDistances();
		getNode(index).setDistance(0);
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(index);
		
		int tempIndex = -1;
		int neighborIndex = -1;
		
		while(queue.peek() != null) {
			tempIndex = queue.poll();
			
			for(int i=0; i<getNodeDegree(tempIndex); i++) {
				
				if(getNode(tempIndex).getConnection(i)[0] != tempIndex)
					neighborIndex = getNode(tempIndex).getConnection(i)[0];
				else
					neighborIndex = getNode(tempIndex).getConnection(i)[1];
				
				if(getNode(neighborIndex).getDistance() == -1)
					queue.add(neighborIndex);
				
				if(getNode(neighborIndex).getDistance() == -1 | getNode(neighborIndex).getDistance() > getNode(tempIndex).getDistance() + 1)
					getNode(neighborIndex).setDistance(getNode(tempIndex).getDistance() + 1);
			}
		}
	}

	// Creates adjacency matrix
	protected void createAdjacencyMatrix() {
		adjacencyMatrix = new int[N][N];

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				adjacencyMatrix[i][j] = 0;

		for (int i = 0; i < E; i++) {
			adjacencyMatrix[globalLinks.get(i).getConnection()[0]][globalLinks.get(i).getConnection()[1]] = 1;
			adjacencyMatrix[globalLinks.get(i).getConnection()[1]][globalLinks.get(i).getConnection()[0]] = 1;
		}
	}

	// Displays adjacency matrix on a console
	public void displayAdjacencyMatrix() {
		createAdjacencyMatrix();
		for (int i = 0; i < N; i++) {
			System.out.print("|");
			for (int j = 0; j < N; j++) {
				System.out.print(adjacencyMatrix[i][j]);
				if (j != N - 1)
					System.out.print(" ");
			}
			System.out.print("|" + "\n");
		}
	}

	// Displays adjacency matrix with node's numbers on a console
	public void displayAdjacencyMatrixIndex() {
		createAdjacencyMatrix();
		System.out.print("   ");
		for (int i = 1; i <= N; i++)
			System.out.print(i + " ");
		System.out.print("\n");

		for (int i = 0; i < N; i++) {
			System.out.print(i + 1 + " |");
			for (int j = 0; j < N; j++) {
				System.out.print(adjacencyMatrix[i][j]);
				if (j != N - 1)
					System.out.print(" ");
			}
			System.out.print("|" + "\n");
		}
	}

	// Saves agency matrix in given path
	public void saveAdjacencyMatrix(String path) {
		Save adjMat = new Save(path);
		createAdjacencyMatrix();
		for (int i = 0; i < N; i++)
			adjMat.writeDataln(adjacencyMatrix[i]);
		adjMat.closeWriter();
	}
	
	public void resetDistances() {
		for(int i=0; i<N; i++)
			nodes.get(i).setDistance(-1);
	}
	
	// Saves agency matrix in default directory
	public void saveAdjacencyMatrix() {saveAdjacencyMatrix("adjacency_matrix.txt");}

	// ~ GETTERS ~

	public int getNumberOfNodes() {return N;}
	public int getNumberOfLinks() {return E;}
	public double getAverageDegree() {return 2 * (double) E / N;}
	public int[][] getAdjacencyMatrix() {return adjacencyMatrix;}
	public ArrayList<Node> getNodes() {return nodes;}
	public Node getNode(int i) {return nodes.get(i);}
	public ArrayList<Link> getLinks() {return globalLinks;}
	public Link getLink(int i) {return globalLinks.get(i);}
	public int getNodeDegree(int i) {return nodes.get(i).getNodeDegree();}
	public ArrayList<Integer> getComponents() {
		computeComponents();
		return components;
	}
	public int getNumberOfComponents() {return getComponents().size();}
	public String getTopologyType() {return typeOfTopology;}
}