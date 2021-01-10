package Dynamics;

import java.util.ArrayList;

public class Message {

	// Content of the message and indexes
	private int[][] content;
	
	// Time of receiving the message
	private int time;
	
	// Number of the node from whom the message is and for what node
	private int[] nodes;
	
	// Ids of this particular information
	private ArrayList<Integer> ids;
	
	// History of editions
	private ArrayList<String> edited;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Message(int[][] content, int time, int[] fromToNode, ArrayList<Integer> ids) {
		for(int msg : content[0])
			if(Math.abs(msg) != 1 && msg !=0)
				throw new Error("Message must contain only -1, 0 or 1.");
		if(ids.size() <= 0)
			throw new Error("Message must have at least one id number.");
			
		this.ids = new ArrayList<Integer>();
		edited = new ArrayList<String>();
		
		this.content = content.clone();
		this.time = time;
		nodes = fromToNode;
		for(int id : ids)
			this.ids.add(id);
	}
	
	// Constructor #2
	public Message(int[][] content, int time, int[] fromToNode, int id) {
		for(int msg : content[0])
			if(Math.abs(msg) != 1 && msg !=0)
				throw new Error("Message must contain only -1, 0 or 1.");
		
		this.ids = new ArrayList<Integer>();
		edited = new ArrayList<String>();
		
		this.content = content.clone();
		this.time = time;
		nodes = fromToNode;
		this.ids.add(id);
	}
	
	//Constructor #3
	public Message(int[][] content, int time, int source, ArrayList<Integer> ids) {
		this(content, time, new int[] {-1, source}, ids);
	}
		
	//Constructor #4
	public Message(int[][] content, int time, int source, int id) {
		this(content, time, new int[] {-1, source}, id);
	}
	
	// Constructor #5
	public Message(int[][] content, int time, int[] fromToNode) {this(content, time, fromToNode, 1);}
	
	// Copy constructor
	public Message(Message message) {
		this.content = message.content.clone();
		this.time = message.time;
		this.nodes = message.nodes.clone();
		this.ids = new ArrayList<Integer>();
		for(int id : message.ids)
			this.ids.add(id);
		this.edited = new ArrayList<String>();
		for(String edit : message.edited)
			this.edited.add(edit);
	}
	
	// Default constructor
	public Message() {this(new int[][] {{0}, {0}}, -1, new int[] {-1, -1});}
	
	// ~ SETTERS ~
	public void addId(int id) {this.ids.add(id);}
	public void setId(ArrayList<Integer> ids) {for(int id : ids) addId(id);}
	public void addEdit(String edit) {edited.add(edit);}
	public void addEdit(ArrayList<String> edited) {for(String edit : edited) this.edited.add(edit);}
	
	// ~ GETTERS ~
	public int[] getMessageContent() {return content[0];}
	public int[] getMessageIndexes() {return content[1];}
	public int[][] getMessageContentAndIndexes() {return content;}
	public int getTime() {return time;}
	public int[] getFromNode() {return nodes;}
	public ArrayList<Integer> getId() {return ids;}
	public ArrayList<String> getEdit() {return edited;}
}
