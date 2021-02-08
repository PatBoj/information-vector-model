package Dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.naming.ldap.Rdn;

import Main.Save;
import Networks.Network;
import Networks.Node;
import ProgramingTools.Debug;
import ProgramingTools.Time;

public class Dynamics 
{	
	// ~ DATA FIELDS ~
	
	// The network on which the dynamics will be carried out
	private Network network;
	
	// Number of nodes in network
	private int N;
	
	// Length of opinion vectors
	private int D;
	
	// Just random number
	private Random rnd;
	
	// Number of different informations in the network, 
	// when agent sends new message this number is going up
	private int nMsg;
	
	// Probability of sending new message by agent
	private double pNewMessage;
	
	// Probability of deleting bit of information that agent disagrees
	private double pDeleteOneBit;
	
	// Probability of adding new bit of information
	private double pAddOneBit;
	
	// Probability of change one bit of information
	private double pChangeOneBit;
	
	// List of all messages
	private ArrayList<Message> messages;
	
	// Unique id to edits
	private int editID;
	
	// Just debug
	private Debug debug;
	
	// Saving data to file
	private Save s;
	
	private Time tajm;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Dynamics(Network network, int lenghtOfOpinionVector, double pNewMessage)
	{
		if(lenghtOfOpinionVector <= 0)
			throw new Error("Length of opinion vector must be greater than zero.");
		if(pNewMessage < 0 || pNewMessage > 1)
			throw new Error("Probability of sending new message shoud be between 0 and 1.");
		
		rnd = new Random();
		
		this.network = network;
		N = network.getNumberOfNodes();
		this.pNewMessage = pNewMessage;
		D = lenghtOfOpinionVector;
		editID = 0;
		
		nMsg = 0;
		messages = new ArrayList<Message>();
		
		pDeleteOneBit = (double)1/3;
		pAddOneBit = (double)1/3;
		pChangeOneBit = (double)1/3;
		
		setInitialConditions();
		
		debug = new Debug(0);
		tajm = new Time(new String[] {"Just saving"});
	}

	// Constructor #2
	public Dynamics(Network network, double pNewMessage) {this(network, 100, pNewMessage);}
	
	// Constructor #3
	public Dynamics(Network network) {this(network, 12, 0.005);}
	
	// Default constructor
	public Dynamics() {this(new Network());};
	
	// ~ METHODS ~
	
	// Cosine similarity
	// the message[0] is the message nad
	// message[1] is for indexes in opinion vector
	private double cosineSimilarity(int[] opinion, int[][] message) {
		
		double lOpinion = 0;
		double lMessage = 0;
		double dotProduct = 0;
		
		for(int i=0; i<message[0].length; i++) {
			lOpinion += opinion[message[1][i]] * opinion[message[1][i]];
			lMessage += message[0][i] * message[0][i];
			dotProduct += opinion[message[1][i]] * message[0][i];
		}
		
		// when agent has neutral opinion on something it's randomly post it
		if(lOpinion == 0 || lMessage == 0) return rnd.nextDouble();
		else return dotProduct/Math.sqrt(lOpinion * lMessage);
	}
	
	// Gives vector size
	private double length(int[] vector) {
		int squareSum = 0;
		for(int i=0; i<vector.length; i++)
			squareSum += vector[i]*vector[i];
		return Math.sqrt(squareSum);
	}
	
	// Sorts messages by time
	private ArrayList<Message> sortByTime(ArrayList<Message> messages) {
		int tempMax;
		int tempIndex;
		int tempSize = messages.size();
		ArrayList<Message> tempMessages = new ArrayList<Message>(tempSize);
		
		for(int i=0; i<tempSize; i++)
		{
			tempMax = messages.get(0).getTime();
			tempIndex = 0;
			
			for(int j=1; j<messages.size(); j++) {
				if(messages.get(j).getTime() >= tempMax) {
					tempMax = messages.get(j).getTime();
					tempIndex = j;
				}
			}
			
			tempMessages.add(messages.get(tempIndex));
			messages.remove(tempIndex);
		}
		
		return tempMessages;
	}
	
	// Gets last shared message
	private Message getLastMessage() {return messages.get(messages.size()-1);}
	
	// Sets initial opinions for every agent
	private void setInitialOpinions(Network net) {
		int[] tempOpinion = new int[D];
		int tempSumOpinion;
		int i = 0;
		
		while(i < N) {
			tempSumOpinion = 0;
			for(int j=0; j<D; j++) {
				tempOpinion[j] = rnd.nextInt(3)-1;
				tempSumOpinion += tempOpinion[j]*tempOpinion[j]; // non-zero length vectors only 
			}

			if(tempSumOpinion != 0) {
				net.getNode(i).setNodeOpinion(tempOpinion.clone());
				i++;
			}
		}
	}
	
	// Sets random opinions and given cosine threshold to all nodes to given network
	public void setInitialConditions(Network net, double threshold) {
		setInitialOpinions(net);
			
		//Sets random cosine threshold to all nodes
		for(int j=0; j<N; j++) 
			net.getNode(j).setThreshold(threshold);
	}
	
	// Sets random opinions and random cosine threshold to all nodes to given network
	public void setInitialConditions(Network net) {
		setInitialOpinions(net);
		
		//Sets random cosine threshold to all nodes
		for(int j=0; j<N; j++) 
			net.getNode(j).setThreshold(2 * rnd.nextDouble() - 1);
	}
	
	// Sets random opinions and cosine threshold to all nodes to network in this class
	private void setInitialConditions() {setInitialConditions(network);}
	public void setInitialConditions(double threshold) {setInitialConditions(network, threshold);}
	
	// Sends message in to the network
	public void sendRandomMessage(int sourceNode, int time) {
		nMsg++;
		int mLenght = rnd.nextInt((int)(.08*D))+1;
		
		int[] tempIndexes = new int[mLenght];
		boolean distinct = false;
		int[] messageContent = new int[mLenght];
		boolean zeroLength = true;
		
		while(zeroLength) {
			distinct = false;
			while(!distinct) {
				for(int i=0; i<mLenght; i++)
					tempIndexes[i] = rnd.nextInt(D);
				
				distinct = true;
				for(int i=0; i<mLenght-1; i++)
					if(tempIndexes[i] == tempIndexes[i+1]) {
						distinct = false;
						break;
					}
			}
			
			for(int i=0; i<mLenght; i++)
				messageContent[i] = 0;
			
			for(int i=0; i<mLenght; i++) 
				messageContent[i] = getNodeOpinion(i)[tempIndexes[i]];
			if(length(messageContent) != 0) zeroLength = false;
		}
		
		messages.add(new Message(new int[][] {messageContent.clone(), tempIndexes.clone()}, time, sourceNode, nMsg));
		setMessage(sourceNode, getLastMessage());
	}
	
	// Checks if message is the same as node opinion
	private boolean isIdentical(int[] nodeOpinion, int[][] content) {	
		for(int i=0; i<content[0].length; i++)
			if(nodeOpinion[content[1][i]] != content[0][i])
				return false;
		return true;
	}
	
	/*private boolean alreadyShared(ArrayList<Message> sendByNode, Message neighborMessage) {
		for(int j=0; j<sendByNode.size(); j++)
			for(int k=0; k<sendByNode.get(j).getId().size(); k++)
				for(int m=0; m<neighborMessage.getId().size(); m++)
					if(sendByNode.get(j).getId().get(k) == neighborMessage.getId().get(m))
						return true;
		
		return false;
	}*/
	
	// Checks if given message was already shared by node
	private boolean alreadyShared(ArrayList<Integer> sendByNode, Message neighborMessage) {		
		for(int i=0; i<sendByNode.size(); i++)
			if(sendByNode.get(i).equals(neighborMessage.getId().get(0)))
				return true;
		return false;
	}
	
	// Deletes one bit of information from message
	private int[][] deleteOneBit(int[] nodeOpinion, int[][] content) {
		int[][] newContent = new int[2][content[0].length - 1];
		int randomVariable = -1;
		boolean changed = false;

		while(!changed) {
			randomVariable = rnd.nextInt(content[0].length);
			if(nodeOpinion[content[1][randomVariable]] != content[0][randomVariable])
				changed = true;
		}
		
		for(int i=0; i<newContent[0].length; i++) {
			newContent[0][i] = i < randomVariable ? content[0][i] : content[0][i+1];
			newContent[1][i] = i < randomVariable ? content[1][i] : content[1][i+1];
		}
		
		return newContent;
	}
	
	// Adds one bit of information to message
	private int[][] addOneBit(int[] nodeOpinion, int[][] content) {
		int[][] newContent = new int[2][content[0].length+1];
		int randomVariable = -1;
		boolean changed = false;
		
		while(!changed) {
			changed = true;
			randomVariable = rnd.nextInt(D);
			for(int i=0; i<content[1].length; i++)
				if(randomVariable == content[1][i]) {
					changed = false;
					break;
				}
		}
		
		for(int i=0; i<newContent[0].length-1; i++) {
			newContent[0][i] = content[0][i];
			newContent[1][i] = content[1][i];
		}
		
		newContent[0][newContent[0].length-1] = nodeOpinion[randomVariable];
		newContent[1][newContent[1].length-1] = randomVariable;
		
		return newContent;
	}
	
	// Changes one bit of information in message
	private int[][] changeOneBit(int[] nodeOpinion, int[][] content) {
		int[][] newContent = new int[2][content[0].length];
		int randomVariable = -1;
		boolean changed = false;
		
		while(!changed) {
			randomVariable = rnd.nextInt(content[0].length);
			if(nodeOpinion[content[1][randomVariable]] != content[0][randomVariable])
				changed = true;
		}
		
		for(int i=0; i<newContent[0].length; i++) {
			if(i == randomVariable) 
				newContent[0][i] = nodeOpinion[content[1][i]];
			else
				newContent[0][i] = content[0][i];
			newContent[1][i] = content[1][i];
		}
		
		return newContent;
	}
	
	// One time step
	private void oneStep(int time, double pEdit, int repetition, String type) {
		int node = rnd.nextInt(N); // pick random node from the network
		ArrayList<Message> neighborMessages; // all neighbors messages
		boolean alreadyShared; // true if message with this ID was shared by agent
		double cosineSimilarity; // cosine similarity between message and node opinion
		int[][] newContent; // new message content
		String edit = "";
		
		// Sends new message to the network
		if(rnd.nextDouble() < pNewMessage) {
			sendRandomMessage(rnd.nextInt(N), time);
			save(s, repetition, type);
		} else { // Share message
			neighborMessages = sortByTime(getNeighborMessages(node)); // gets all neighbor messages and sorts them by time
			// Checks if this message was already shared (by ID)
			// this loop is for all messages shared by node's neighbors
			for(int i=0; i<neighborMessages.size(); i++) {
				alreadyShared = alreadyShared(getNodeSharedIds(node), neighborMessages.get(i));
				// If it wasn't shared go to the next condition
				if(!alreadyShared) {
					cosineSimilarity = cosineSimilarity(getNodeOpinion(node), neighborMessages.get(i).getMessageContentAndIndexes());
					// If it is above threshold, share this message
					if(cosineSimilarity > getNodeThreshold(node)) {
						// Message can be edit before sharing
						// but if it's matching the node's opinion it shouldn't be changed
						if(!isIdentical(getNodeOpinion(node), neighborMessages.get(i).getMessageContentAndIndexes()) && rnd.nextDouble() < pEdit) {
							double randomChance = rnd.nextDouble();
							editID++;
							
							// Delete information
							// if of curse length of the message is greater than 1
							if(randomChance < pDeleteOneBit && neighborMessages.get(i).getMessageContentAndIndexes()[0].length > 1) {
								newContent = deleteOneBit(getNodeOpinion(node), neighborMessages.get(i).getMessageContentAndIndexes()).clone();
								edit = "del" + editID;
							}
							// Add new information
							else if(randomChance < pDeleteOneBit + pAddOneBit) {
								newContent = addOneBit(getNodeOpinion(node), neighborMessages.get(i).getMessageContentAndIndexes()).clone();
								edit = "add" + editID;
							}
							// Change information
							else if (randomChance <= pDeleteOneBit + pAddOneBit + pChangeOneBit) {
								newContent = changeOneBit(getNodeOpinion(node), neighborMessages.get(i).getMessageContentAndIndexes()).clone();
								edit = "chg" + editID;
							}
							
							// Else throw an error
							else 
								throw new Error("Something wrong with probabilities of changing, deleting and adding new pice of information.");
						}
						else newContent = neighborMessages.get(i).getMessageContentAndIndexes().clone();
						messages.add(new Message(newContent.clone(), time, new int[] {i, node}, neighborMessages.get(i).getId()));
						getLastMessage().addEdit(neighborMessages.get(i).getEdit());
						if(edit != "") getLastMessage().addEdit(edit);
						setMessage(node, getLastMessage());
						
						save(s, repetition, type);
						break;
					}
				}
			}
		}
	}
	
	public void run(int maxTime, double pEdit, int repetition, int maxRepetitions, String type) {
		sendRandomMessage(rnd.nextInt(N), 0);
		
		save(s, repetition, type);
		debug.startLoopTimer();
		for(int i=0; i<maxTime; i++) {
			oneStep(i+1, pEdit, repetition, type);
			if((i+1)%1000 == 0) debug.progressSimple(repetition-1, 0, maxRepetitions, i, 0, maxTime);
		}
		tajm.printTimeResults();
	}
	
	//public void run(int maxTime, int repetition, int maxRepetitions) {run(maxTime, 0.0, repetition, maxRepetitions);}
	
	//public void run(int repetition, int maxRepetitions) {run(5000, 0.0, repetition, maxRepetitions);}
	
	private void save(Save s, int repetition, String type) {
		s.writeDatatb(repetition);
		s.writeDatatb(getLastMessage().getId().get(0));
		s.writeDatatb(getLastMessage().getTime());
		s.writeDatatb(type);
		s.writeDatatb(getThreshold());
		
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int index : getLastMessage().getMessageIndexes())
			indexes.add(index);
		Collections.sort(indexes);
		
		int i = 0;
		int tempIndex = 0;
		
		while(indexes.size() != 0 && i != D-1) {
			if(i == indexes.get(0)) {
				tajm.startTimer();
				s.writeData(getLastMessage().getMessageContent()[tempIndex] + "\t");
				tajm.pauseTimer(0);
				indexes.remove(0);
				tempIndex++;
			} else {
				tajm.startTimer();
				s.writeData("NULL\t");
				tajm.pauseTimer(0);
			}
			i++;
		}
		
		for(int j=i; j<D-1; j++) {
			tajm.startTimer();
			s.writeData("NULL\t");
			tajm.pauseTimer(0);
		}
			
		
		if(indexes.size() != 0) {
			tajm.startTimer();
			s.writeData(getLastMessage().getMessageContent()[tempIndex] + "\n");
			tajm.pauseTimer(0);
		} else {
			tajm.startTimer();
			s.writeData("NULL\n");
			tajm.pauseTimer(0);
		}
		
		for(int j=0; j<getLastMessage().getEdit().size(); j++) {
			s.writeData(getLastMessage().getEdit().get(j) + (String)(j == (getLastMessage().getEdit().size()-1) ? "\n" : "\t"));
		}
	}
	
	// ~ SET ~	
	
	// Sets opinion of a single node
	public void setNodeOpinion(int i, int[] opinion) {
		if (i < 0 || i > N)
			throw new Error("Indexes out of range.");
			
		getNode(i).setNodeOpinion(opinion);
	}
		
	public void setMessage(int i, Message msg) {
		if (i < 0 || i > N)
			throw new Error("Indexes out of range.");
		getNode(i).setMessage(msg);
	}
	
	// Set new network to the dynamics
	public void setNewNetwork(Network network) {
		this.network = network;
		N = network.getNumberOfNodes();
		
		messages.clear();
		editID = 0;
		nMsg = 0;
	}
	
	public void setProbabilityNewMessage(double pNewMessage) {this.pNewMessage = pNewMessage;}
	public void setSaveFile(String path) {s = new Save(path);}
	public void saveHeader() {
		int nEdit = 30;
		
		s.writeDatatb("repetition");
		s.writeDatatb("id");
		s.writeDatatb("time");
		s.writeDatatb("type");
		s.writeDatatb("threshold");
		for(int i=0; i<D; i++)
			s.writeDatatb("inf" + (i+1));
		for(int i=0; i<nEdit; i++)
			s.writeDatatb("edit" + (i+1));
		s.writeDataln("edit" + nEdit);
	}
	public void closeSaveFile() {s.closeWriter();}
	public void setProbabilities(double pChg, double pAdd, double pDel) {
		pChangeOneBit = pChg;
		pAddOneBit = pAdd;
		pDeleteOneBit = pDel;
	}
	
	// ~ GETTERS ~
	
	// Get node opinion
	public int[] getNodeOpinion(int i) {
		// Errors and exceptions
		if (i < 0 || i > N)
			throw new Error("Index out of range.");

		return getNode(i).getNodeOpinion();
	}
	
	// Get node's "i" all neighbors messages
	public ArrayList<Message> getNeighborMessages(int i) {
		if (i < 0 || i > N)
			throw new Error("Index out of range.");
		
		ArrayList<Message> messages = new ArrayList<Message>();
		int connectionIndex = -1;
		
		for(int j=0; j<getNodeDegree(i); j++) {
			connectionIndex = getNode(i).getConnection(j)[0] != i ? 0 : 1;
			for(Message msg : getNodeMessages(getNode(i).getConnection(j)[connectionIndex]))
				messages.add(msg);
		}

		return messages;
	}
	
	public ArrayList<Message> getAllMessages() {return messages;}
	public ArrayList<Message> getNodeMessages(int i) {return network.getNodes().get(i).getAllNodeMessages();}
	public double getProbabilityNewMessage() {return pNewMessage;}
	public double getNodeThreshold(int i) {return network.getNodes().get(i).getCosineThreshold();}
	public ArrayList<Integer> getNodeSharedIds(int i) {return network.getNode(i).getSharedIds();}
	public int getNodeDegree(int i) {return network.getNodeDegree(i);}
	public Node getNode(int i) {return network.getNode(i);}
	public String getTopologyType() {return network.getTopologyType();}
	public double getThreshold() {return network.getNode(0).getCosineThreshold();}
}
