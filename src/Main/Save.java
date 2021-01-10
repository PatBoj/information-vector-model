package Main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Save 
{
	// ~ DATA FIELDS ~ 
	protected File aFile;
	protected FileWriter writer;
	protected String path;
	
	// ~ CONSTRUCTORS ~
	// Constructor #1
	public Save(String path)
	{
		this.path = path;
		aFile = new File(path); 
		try {writer = new FileWriter(aFile);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Default constructor
	public Save() {this("data.txt");}
	
	// ~ METHODS ~
	// Creates file
	public void createFile(String path)
	{
		aFile = new File(path); 
		try {writer = new FileWriter(aFile);} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes integer to file
	public void writeData(int data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
	        writer.write(data + "");
	        }
	    catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes double to file
	public void writeData(double data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
		    writer.write(data + "");
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes string to file
	public void writeData(String data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			writer.write(data); 
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	
	// Writes data with \t on the end
	public void writeDatatb(double data) {writeData(data + "\t");}
	public void writeDatatb(int data) {writeData(data + "\t");}
	public void writeDatatb(String data) {writeData(data + "\t");}
	
	// Writes data and goes to the next line
	public void writeDataln(int data) {writeData(data + "\n");}
	public void writeDataln(double data) {writeData(data + "\n");}
	public void writeDataln(String data) {writeData(data + "\n");}
	
	// Writes table of integers
	private void writeData(int[] data, String separator, String eol) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			for(int i=0; i<data.length; i++)
				if(i != (data.length-1)) writer.write(data[i] + separator);
				else writer.write(data[i] + "");
			writer.write(eol);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes table of integers
	private void writeData(double[] data, String separator, String eol) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			for(int i=0; i<data.length; i++)
				if(i != (data.length-1)) writer.write(data[i] + separator);
				else writer.write(data[i] + "");
			writer.write(eol);
		}
		catch (IOException e) {e.printStackTrace();}
	}
		
	// Writes table of integers
	private void writeData(String[] data, String separator, String eol) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			for(int i=0; i<data.length; i++)
				if(i != (data.length-1)) writer.write(data[i] + separator);
				else writer.write(data[i] + "");
			writer.write(eol);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Writes data and goes to the next line with custom separator
	public void writeDataln(double[] data, String separator) {writeData(data, separator, "\n");}
	public void writeDataln(int[] data, String separator) {writeData(data, separator, "\n");}
	public void writeDataln(String[] data, String separator) {writeData(data, separator, "\n");}
	
	// Writes data and goes to the next line with separator: "\t"
	public void writeDataln(double[] data) {writeData(data, "\t", "\n");}
	public void writeDataln(int[] data) {writeData(data, "\t", "\n");}
	public void writeDataln(String[] data) {writeData(data, "\t", "\n");}
	
	// Writes data and doesn't go to the next line, tab is the separator
	public void writeDatatb(double[] data) {writeData(data, "\t", "");}
	public void writeDatatb(int[] data) {writeData(data, "\t", "");}
	public void writeDatatb(String[] data) {writeData(data, "\t", "");}
	
	// Writes 2D table to the file
	public void writeData(double[][] data) {
		try {
			if (!aFile.exists()) aFile.createNewFile(); // checking if file exists
			for(int i=0; i<data.length; i++) {
				for(int j=0; j<data[i].length; j++)
					if(i != (data.length-1)) writer.write(data[i] + "\t");
					else writer.write(data[i] + "");
				writer.write("\n");
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	// Closes writer
	public void closeWriter()
	{
		try {writer.close();} 
		catch (IOException e) {	e.printStackTrace(); }
	}
	
	// Deletes file
	public void deleteFile() {
		if(!aFile.delete()) System.out.println("File " + path + " cannot be deleted.");
	}
}