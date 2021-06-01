package Dynamics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Main.Save;
import Networks.Network;
import Networks.Node;
import ProgramingTools.Time;
import ProgramingTools.Tools;

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
	// It's just an id of a message
	private int nMsg;
	
	// Probability of sending new message by agent
	private double pNewMessage;
	
	// Probability of changing message
	private double pEdit;
	
	// Probability of deleting bit of information that agent disagrees
	private double pDeleteOneBit;
	
	// Probability of adding new bit of information
	private double pAddOneBit;
	
	// Probability of change one bit of information
	private double pChangeOneBit;
	
	// Cosine threshold
	private double cosineThreshold;
	
	// List of all messages
	private ArrayList<Message> messages;
	
	// Array to agregata data
	// [0] - id counter
	// [1] - average length
	// [2] - average emotion
	private double[][] dataToSave;
	
	// Max index to save
	private int maxIndexSave;
	
	// Unique id of edits
	//private int editID;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Dynamics(Network network, int lenghtOfOpinionVector, double pNewMessage, double pEdit, double cosineThreshold) {
		if(lenghtOfOpinionVector <= 0)
			throw new Error("Length of opinion vector must be greater than zero.");
		if(pNewMessage < 0 || pNewMessage > 1)
			throw new Error("Probability of sending new message shoud be between 0 and 1.");
		
		rnd = new Random();
		
		this.network = network;
		N = network.getNumberOfNodes();
		this.pNewMessage = pNewMessage;
		this.pEdit = pEdit;
		D = lenghtOfOpinionVector;
		//editID = 0;
		
		nMsg = 0;
		messages = new ArrayList<Message>();
		
		pDeleteOneBit = (double)1/3;
		pAddOneBit = (double)1/3;
		pChangeOneBit = (double)1/3;
		
		this.cosineThreshold = cosineThreshold;
		
		setInitialOpinions();
	}
	
	// ~ INITIAL CONDITIONS ~
	
	// Sets initial opinions based on Ising model
	public void setInitialOpinions(Network net, int time) {
		setInitialOpinions(net);
		
		double beta = 2; // exponent
		int i = -1; // random node
		int changeIndex = -1; // random index of opinion vector
		int newOpinion = -2; // new opinion in given index
		int e = calculateWholeEnergy(net); // whole energy of the system
		int de = 0; // energy change
		
		for(int j=0; j<time; j++) {
			i = rnd.nextInt(N);
			changeIndex = rnd.nextInt(D);
			newOpinion = (net.getNode(i).getNodeOpinion()[changeIndex] + 1 + rnd.nextInt(2)+1) % 3 - 1;
			de = calculateEnergyChange(net, i, newOpinion, changeIndex);
			
			if(de < 0) {
				net.getNode(i).setOneNodeOpinion(changeIndex, newOpinion);
				e += de;
			}
			else if(Math.exp(- beta * de) > rnd.nextDouble()) {
				net.getNode(i).setOneNodeOpinion(changeIndex, newOpinion);
				e += de;
			}
			//if((j+1) % 1000 == 0)
			//	computeTheClosestSimilarity(j+1, s);
		}
	}

	// Sets random initial opinions for every agent
	public void setInitialOpinions(Network net) {
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
	
	// Sets Ising initial opinions based on the given time
	public void setInitialOpinions(int time) {
		if(time !=0) setInitialOpinions(network, time);
		else setInitialOpinions(network);
	}
	
	// Sets random initial opinions
	public void setInitialOpinions() {setInitialOpinions(network);}
	
	// Calculates whole energy of the system
	// it's sum of product of every element in the vector of every pair in the network
	private int calculateWholeEnergy(Network net) {
		int e = 0;
		int neighborIndex = -1;
			
		for(int i=0; i<N; i++)
			for(int j=0; j<net.getNodeDegree(i); j++) {
				neighborIndex = net.getNeighbourIndex(i, j);
				for(int k=0; k<D; k++)
					e -= net.getNode(i).getNodeOpinion()[k] * net.getNode(neighborIndex).getNodeOpinion()[k];
			}
			
		return e/2;
	}
	
	// Calculates energy change when one opinion was change
	private int calculateEnergyChange(Network net, int i, int newValue, int changeIndex) {
		if(changeIndex < 0 | changeIndex >= D)
			throw new Error("Change index out of bound");
			
		int de = 0;
		int neighborIndex = -1;
			
		for(int j=0; j<net.getNodeDegree(i); j++) {
			neighborIndex = net.getNeighbourIndex(i, j);
			de += net.getNode(i).getNodeOpinion()[changeIndex] * net.getNode(neighborIndex).getNodeOpinion()[changeIndex];
			de -= newValue * net.getNode(neighborIndex).getNodeOpinion()[changeIndex];
		}
			
		return de;
	}

	// ~ METHODS ~
	
	// Cosine similarity
	// the message[0] is the message content
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
		
		// when agent has neutral opinion on something he's randomly post it
		if(lOpinion == 0 || lMessage == 0) return rnd.nextDouble();
		else return dotProduct/Math.sqrt(lOpinion * lMessage);
	}
	
	// Get agents cosine similarity
	private double agentsSimilarity(int[] opinion1, int[] opinion2) {
		return Tools.cosine(opinion1, opinion2);
	}
	
	// Gets last shared message
	private Message getLastMessage() {return messages.get(messages.size()-1);}
	
	// Computes the closes similarity of the agents
	private void computeTheClosestSimilarity(int time, Save s) {
		double sum = 0;
		double counts = 0;
		
		for(int i=0; i<N; i++) {
			network.computeDistance(i);
			
			for(int j=0; j<N; j++)
				if(network.getNode(j).getDistance() == 1) {
					sum += agentsSimilarity(network.getNode(j).getNodeOpinion(), network.getNode(i).getNodeOpinion());
					counts += 1;
				}
		}
		
		s.writeDatatb(time);
		s.writeDataln(sum/counts);
	}
	
	// Computes and saves all similarities of the agents based on the distance between them
	// also creates histogram
	public void computeAndSaveCorrelations(int time, int energy, Save s) {
		int pairs = N*N;
		
		double[][] data = new double[2][pairs];
		
		for(int i=0; i<N; i++) {
			network.resetDistances();
			network.computeDistance(i);
			for(int j=0; j<N; j++) {
				data[0][i*N + j] = network.getNode(j).getDistance();
				data[1][i*N + j] = agentsSimilarity(network.getNode(j).getNodeOpinion(), network.getNode(i).getNodeOpinion());
			}
		}
		
		int maxDistance = (int)Tools.getMaximum(data[0]);
		double dc = 0.05;
		int bins = (int)(2/dc + 1);
		
		double[][][] histogram = new double[maxDistance+1][2][bins-1];
		
		for(int i=0; i<maxDistance+1; i++)
			for(int j=0; j<bins-1; j++) {
				histogram[i][0][j] = Tools.convertToDouble(-0.975 + j*dc, 3);
				histogram[i][1][j] = 0;
			}
		
		for(int i=0; i<pairs; i++) {
			if(data[0][i] != -1) {
				for(int j=0; j<bins-2; j++) {
					if(data[1][i] >= Tools.convertToDouble(histogram[(int)data[0][i]][0][j] - dc/2, 3) & data[1][i] < Tools.convertToDouble(histogram[(int)data[0][i]][0][j] + dc/2, 3)) {
						histogram[(int)data[0][i]][1][j] += 1;		
						break;
					}
				}
				if(data[1][i] >= Tools.convertToDouble(histogram[(int)data[0][i]][0][bins-2] - dc/2, 3) & data[1][i] <= Tools.convertToDouble(histogram[(int)data[0][i]][0][bins-2] + dc/2, 3))
					histogram[(int)data[0][i]][1][bins-2] += 1;		
			}
		}
		
		int sum;
		for(int i=0; i<maxDistance+1; i++) {
			sum = 0;
			for(int j=0; j<bins-1; j++)
				sum += histogram[i][1][j];
			for(int j=0; j<bins-1; j++)
				histogram [i][1][j] /= sum;
		}
		
		for(int i=0; i<maxDistance+1; i++) {
			for(int j=0; j<bins-1; j++) {
				s.writeDatatb(time);
				s.writeDatatb(energy);
				s.writeDatatb(i);
				s.writeDatatb(histogram[i][0][j]);
				s.writeDataln(histogram[i][1][j]);
			}
		}
	}
	
	// Checks if message is the same as node opinion
	private boolean isIdentical(int[] nodeOpinion, int[][] content) {	
		for(int i=0; i<content[0].length; i++)
			if(nodeOpinion[content[1][i]] != content[0][i])
				return false;
		return true;
	}
	
	// ~ ACTUAL DYNAMICS ~
	
	// Sends message in to the network
	public void sendRandomMessage(int sourceNode, int time) {
		int mLength = rnd.nextInt((int)Tools.convertToDouble(.14*D, 0)) + 1;
		
		ArrayList<Integer> indexes = new ArrayList<Integer>(D);
		for(int i=0; i<D; i++)
			indexes.add(i);
		
		int[] message = new int[mLength];
		boolean zeroLength = true;
		
		while(zeroLength) {
			Collections.shuffle(indexes);
			
			for(int i=0; i<mLength; i++) 
				message[i] = getNodeOpinion(i)[indexes.get(i)];
			if(Tools.length(message) != 0) zeroLength = false;
		}
		
		messages.add(new Message(new int[][] {message.clone(), Tools.convertFromArrayList(indexes, mLength)}, time, nMsg));
		setMessage(sourceNode, getLastMessage());
		setDashboard(sourceNode, getLastMessage());
		nMsg++;
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
		for(int i=sendByNode.size()-1; i>=0; i--)
			if(sendByNode.get(i).equals(neighborMessage.getId()))
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
	private int[][] changeOneBit(int[] nodeOpinion, int[][] content, double sim) {
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
	private void oneStep(int time) {
		int node = rnd.nextInt(N); // pick random node from the network
		boolean alreadyShared; // true if message with this ID was shared by agent
		double cosineSimilarity; // cosine similarity between message and node opinion
		boolean isIdentical; // true if the message is the same as agents's opinion
		int[][] newContent; // new message content
		// String edit = ""; // show change in the message
		
		// Sends new message to the network
		if(rnd.nextDouble() < pNewMessage) {
			sendRandomMessage(rnd.nextInt(N), time);
			saveDataToArray();
		} else { // Share message
			// Checks if the last neighbor message is similar to the node's opinion vector
			// this loop is for all messages shared by node's neighbors
			for(int i=getDashboardSize(node) - 1; i>=0; i--) {
				cosineSimilarity = cosineSimilarity(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes());
				// If the agent like it the message goes to next condition
				if(cosineSimilarity >= cosineThreshold) {
					alreadyShared = alreadyShared(getNodeSharedIds(node), getDashboard(node).get(i));
					// Checks if this message was already shared (by ID)
					if(!alreadyShared) {
						// Message can be edit before sharing
						// but if it's matching the node's opinion it shouldn't be changed
						if(rnd.nextDouble() < pEdit) {
							double randomChance = rnd.nextDouble();
							isIdentical = isIdentical(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes());
							//editID++;
							
							// Delete information
							// if of curse length of the message is greater than 1
							if(!isIdentical && randomChance < pDeleteOneBit && getDashboard(node).get(i).getMessageContentAndIndexes()[0].length > 1) {
								newContent = deleteOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes()).clone();
								//edit = "del" + editID;
							}
							// Change information
							else if (!isIdentical && randomChance < pDeleteOneBit + pChangeOneBit) {
								newContent = changeOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes(), cosineSimilarity).clone();
								//edit = "chg" + editID;
							}
							// Add new information
							else if(randomChance <= pDeleteOneBit + pDeleteOneBit + pChangeOneBit) {
								newContent = addOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes()).clone();
								//edit = "add" + editID;
							}
							// Else throw an error
							else 
								throw new Error("Something wrong with probabilities of changing, deleting and adding new pice of information.");
						}
						else newContent = getDashboard(node).get(i).getMessageContentAndIndexes().clone();
						messages.add(new Message(newContent.clone(), time, getDashboard(node).get(i).getId()));
						//getLastMessage().addEdit(getDashboard(node).get(i).getEdit());
						//if(edit != "") getLastMessage().addEdit(edit);
						
						setMessage(node, getLastMessage());
						setDashboard(node, getLastMessage());
						saveDataToArray();
						//break; // COMMENT IT IF YOU WANT TO EXCLUDE COMPETITION
					}
					//else getDashboard(node).remove(i);
				}
				//else getDashboard(node).remove(i);
			}
			getDashboard(node).clear(); // NON COMMENT IT IF YOU WANT TO EXCLUDE COMPETITION
		}
	}
	
	private void oneStepCompetition(int node, int time) {
		boolean alreadyShared; // true if message with this ID was shared by agent
		double cosineSimilarity; // cosine similarity between message and node opinion
		boolean isIdentical; // true if the message is the same as agents's opinion
		int[][] newContent; // new message content
		
		// Checks if the last neighbor message is similar to the node's opinion vector
		// this loop is for all messages shared by node's neighbors
		for(int i=getDashboardSize(node) - 1; i>=0; i--) {
			cosineSimilarity = cosineSimilarity(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes());
			// If the agent like it the message goes to next condition
			if(cosineSimilarity >= cosineThreshold) {
				alreadyShared = alreadyShared(getNodeSharedIds(node), getDashboard(node).get(i));
				// Checks if this message was already shared (by ID)
				if(!alreadyShared) {
					// Message can be edit before sharing
					// but if it's matching the node's opinion it shouldn't be changed
					if(rnd.nextDouble() < pEdit) {
						double randomChance = rnd.nextDouble();
						isIdentical = isIdentical(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes());
						//editID++;
							
						// Delete information
						// if of curse length of the message is greater than 1
						if(!isIdentical && randomChance < pDeleteOneBit && getDashboard(node).get(i).getMessageContentAndIndexes()[0].length > 1) {
							newContent = deleteOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes()).clone();
							//edit = "del" + editID;
						}
						// Change information
						else if (!isIdentical && randomChance < pDeleteOneBit + pChangeOneBit) {
							newContent = changeOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes(), cosineSimilarity).clone();
							//edit = "chg" + editID;
						}
						// Add new information
						else if(randomChance <= pDeleteOneBit + pDeleteOneBit + pChangeOneBit) {
							newContent = addOneBit(getNodeOpinion(node), getDashboard(node).get(i).getMessageContentAndIndexes()).clone();
							//edit = "add" + editID;
						}
						// Else throw an error
						else 
							throw new Error("Something wrong with probabilities of changing, deleting and adding new pice of information.");
					}
					else newContent = getDashboard(node).get(i).getMessageContentAndIndexes().clone();
					messages.add(new Message(newContent.clone(), time, getDashboard(node).get(i).getId()));
					//getLastMessage().addEdit(getDashboard(node).get(i).getEdit());
					//if(edit != "") getLastMessage().addEdit(edit);
						
					setMessage(node, getLastMessage());
					setDashboard(node, getLastMessage());
					saveDataToArray();
					//break; // COMMENT IT IF YOU WANT TO EXCLUDE COMPETITION
				}
					//else getDashboard(node).remove(i);
			}
			//else getDashboard(node).remove(i);
		}
		getDashboard(node).clear(); // NON COMMENT IT IF YOU WANT TO EXCLUDE COMPETITION
	}
	
	// Runs entire simulation
	public void run(int maxTime) {
		dataToSave = new double[3][maxTime];
		for(int i=0; i<3; i++)
			for(int j=0; j<maxTime; j++)
				dataToSave[i][j] = 0;
		maxIndexSave = 0;
		
		sendRandomMessage(rnd.nextInt(N), 0);
		saveDataToArray();
		
		for(int i=0; i<maxTime; i++)
			oneStep(i+1);
		
		for(int i=0; i<10000; i++)
			oneStepCompetition(i % N, maxTime+1+i);
	}
	
	public void saveHeader(Save s) {
		// Commented lines are not necessary right now
		//int nEdit = 30;
		
		//s.writeDatatb("repetition");
		s.writeDatatb("count");
		s.writeDatatb("avg_length");
		s.writeDataln("avg_emotion");
		//s.writeDatatb("time");
		//s.writeDatatb("type");
		//s.writeDatatb("threshold");
		//s.writeDataln("threshold");
		//for(int i=0; i<D; i++)
		//	s.writeDatatb("inf" + (i+1));
		//for(int i=0; i<nEdit; i++)
		//	s.writeDatatb("edit" + (i+1));
		//s.writeDataln("edit" + nEdit);
	}
	
	public void saveParameters(Save s, String dynType, int realisations, int maxTime) {
		s.writeDatatb("N");
		s.writeDatatb(N);
		s.writeDataln("Number of nodes in the newtork");
		
		s.writeDatatb("<k>");
		s.writeDatatb(network.getAverageDegree());
		s.writeDataln("Average degree");
		
		s.writeDatatb("network type");
		s.writeDatatb(network.getTopologyType());
		s.writeDataln("Topology type of the network");
		
		s.writeDatatb("D");
		s.writeDatatb(D);
		s.writeDataln("Length of the opinion vector");
		
		s.writeDatatb("eta");
		s.writeDatatb(pEdit);
		s.writeDataln("Probability of creating new message");
		
		s.writeDatatb("tau");
		s.writeDatatb(Tools.convertToDouble(cosineThreshold));
		s.writeDataln("Probability of edit an information");
		
		s.writeDatatb("alpha");
		s.writeDatatb(pNewMessage);
		s.writeDataln("Probability of sending new message");
		
		s.writeDatatb("dynamics type");
		s.writeDatatb(dynType);
		s.writeDataln("Type of the dynamics, with or without competition");
		
		s.writeDatatb("closest similarity");
		s.writeDatatb(getNeighoursAverageSimilarity());
		s.writeDataln("Avereage cosine similarity between the closest neighbors in the network");
		
		s.writeDatatb("realizations");
		s.writeDatatb(realisations);
		s.writeDataln("Number of independet realisations");
		
		s.writeDatatb("time steps");
		s.writeDatatb(maxTime);
		s.writeDataln("Number of time steps");
		
		s.writeDataln("");
	}
	
	private void save(Save s) {
		//s.writeDatatb(repetition);
		s.writeDatatb(getLastMessage().getId());
		s.writeDatatb(getLastMessage().getMessageContent().length);
		s.writeDataln(Tools.getAverage(getLastMessage().getMessageContent()));
		//s.writeDatatb(getLastMessage().getTime());
		//s.writeDatatb(type);
		//s.writeDatatb(getThreshold());
		//s.writeDataln(getThreshold());
		/*
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
		}*/
	}
	
	// Aggregates data to save
	private void saveDataToArray() {
		int msgID = getLastMessage().getId();
		if(msgID > maxIndexSave) maxIndexSave = msgID;
		
		dataToSave[0][msgID]++;
		dataToSave[1][msgID] += getLastMessage().getMessageContent().length;
		dataToSave[2][msgID] += Tools.getAverage(getLastMessage().getMessageContent());
	}
	
	// Saves aggregated data to file
	public void saveData(Save s) {
		saveHeader(s);
		for(int i=0; i<=maxIndexSave; i++) {
			s.writeDatatb(Tools.convertToString(dataToSave[0][i], 0));
			s.writeDatatb(dataToSave[1][i] / dataToSave[0][i]);
			s.writeDataln(dataToSave[2][i] / dataToSave[0][i]);
		}
	}
	
	// ~ SET ~

	// Sets probability of change, add or delete one bit of information
	public void setProbabilities(double pChg, double pAdd, double pDel) {
		pChangeOneBit = pChg;
		pAddOneBit = pAdd;
		pDeleteOneBit = pDel;
	}
	
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
	
	public void setSharedMessage(int i, Message msg) {
		if (i < 0 || i > N)
			throw new Error("Indexes out of range.");
		getNode(i).setSharedMessage(msg);
	}
	
	// Set new network to the dynamics
	public void setNewNetwork(Network network) {
		this.network = network;
		N = network.getNumberOfNodes();
		
		messages.clear();
		//editID = 0;
		nMsg = 0;
		setInitialOpinions();
	}
	
	// Sets probability of creating new message
	public void setProbabilityNewMessage(double pNewMessage) {this.pNewMessage = pNewMessage;}
	
	// Add message to the every neighbor dashboard
	public void setDashboard(int i, Message msg) {
		if (i < 0 || i > N)
			throw new Error("Index out of range.");
		int connectionIndex = -1;
		
		for(int j=0; j<getNodeDegree(i); j++) {
			connectionIndex = getNode(i).getConnection(j)[0] != i ? 0 : 1;
			setSharedMessage(getNode(i).getConnection(j)[connectionIndex], msg);
		}
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
	
	// Get average similarity between the closest neighbors in the network
	private double getNeighoursAverageSimilarity() {
		double sum = 0;
		double counts = 0;
		
		for(int i=0; i<N; i++) {
			network.computeDistance(i);
			
			for(int j=0; j<N; j++)
				if(network.getNode(j).getDistance() == 1) {
					sum += agentsSimilarity(network.getNode(j).getNodeOpinion(), network.getNode(i).getNodeOpinion());
					counts += 1;
				}
		}
		
		return(sum/counts);
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
	public int getDashboardSize(int i) {return network.getNode(i).getNodeDashboard().size();}
	public ArrayList<Message> getDashboard(int i) {return network.getNode(i).getNodeDashboard();}
}
