package Dynamics;

public class Message {
	// ~ DATA FIELDS ~
	// Content of the message and indexes
	private int[][] content;
	
	// Time of receiving the message
	private int time;
	
	// Id of this particular information
	private int id;
	
	// History of editions
	//private ArrayList<String> edited;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Message(int[][] content, int time, int id) {
		if(content.length != 2)
			throw new Error("Array with content contains more than 2 arrays,");
		if(content[0].length != content[1].length)
			throw new Error("Lenght of the message content does not match lengt of the index array.");
		for(int msg : content[0])
			if(Math.abs(msg) != 1 && msg !=0)
				throw new Error("Message must contain only -1, 0 or 1.");
			
		this.id = id;
		//edited = new ArrayList<String>();
		
		this.content = content.clone();
		this.time = time;
	}
	
	// Copy constructor
	public Message(Message message) {
		this.content = message.content.clone();
		this.time = message.time;
		this.id = message.id;
		
		//this.edited = new ArrayList<String>();
		//for(String edit : message.edited)
		//	this.edited.add(edit);
	}
	
	// ~ SETTERS ~
	public void setId(int newId) {id = newId;}
	//public void addEdit(String edit) {edited.add(edit);}
	//public void addEdit(ArrayList<String> edited) {for(String edit : edited) this.edited.add(edit);}
	
	// ~ GETTERS ~
	public int[] getMessageContent() {return content[0];}
	public int[] getMessageIndexes() {return content[1];}
	public int[][] getMessageContentAndIndexes() {return content;}
	public int getTime() {return time;}
	public int getId() {return id;}
	//public ArrayList<String> getEdit() {return edited;}
}
