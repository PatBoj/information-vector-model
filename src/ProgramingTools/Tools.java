package ProgramingTools;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class Tools 
{	
	// ~ METHODS ~		
	// Converts milliseconds to time format: 00:00:00
	public static String convertTime(long miliseconds) {
		long second = (miliseconds / 1000) % 60;
		long minute = (miliseconds / (1000 * 60)) % 60;
		long hour = (miliseconds / (1000 * 60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	// Converts double to string with custom precision
	public static String convertToString(double number, int precision) {
		String temp = "";
		for(int i=0; i<precision; i++) temp += "0";
		if(precision != 0) return new DecimalFormat("#0." + temp).format(number);
		else return new DecimalFormat("#0" + temp).format(number);
	}
	
	// Converts double to string with two decimal places
	public static String convertToString(double number) {return convertToString(number, 2);}
	
	// Converts double to the double with given precision
	public static double convertToDouble(double number, int precision) {
		double temp = number;
		for(int i=0; i<precision; i++)
			temp *= 10;
		temp = Math.round(temp);
		for(int i=0; i<precision; i++)
			temp /= 10;
		return temp;
	}
	
	// Converts double to double with precision 2
	public static double convertToDouble(double number) {return convertToDouble(number, 2);}
	
	// Some class that I don't know what it does
	private static <T> T getValue(Class<T> desiredType, Object o) {
		if(o.getClass().isAssignableFrom(desiredType))
			return desiredType.cast(o);
		else throw new IllegalArgumentException();
	}
	
	// It changes variable type based on the string
	// Object.getClass().getName()
	public static void changeType(String classType, Object variable) {
		Class<?> type;
	    try {
	        type = Class.forName(classType);
	        getValue(type, variable);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	
	public static int dotProduct(int[] array1, int[] array2) {
		if(array1.length != array2.length)
			throw new Error("Cannot compute dot product from vectros which lenght are different.");
		
		int dotProduct = 0;
		for(int i=0; i<array1.length; i++)
			dotProduct += array1[i] * array2[i];
		return dotProduct;
	}
	
	public static double length(int[] array) {
		double length = 0;
		for(int element : array)
			length += element * element;
		return Math.sqrt(length);
	}
	
	public static double cosine(int[] array1, int[] array2) {
		return (int)dotProduct(array1, array2) / (length(array1) * length(array2));
	}
	
	public static int[] convertFromArrayList(ArrayList<Integer> list, int nElements) {
		if(nElements <= 0)
			throw new Error("Number of elements in converthg ArrayList into array must be greater than zero.");
		int[] array = new int[nElements];
		for(int i=0; i<nElements; i++)
			array[i] = list.get(i);
		return array;
	}
	
	public static int[] convertFromArrayList(ArrayList<Integer> list) {return convertFromArrayList(list, list.size());}
	
	// ~ STATISTICS ~	
	public static long getMinimumLong(ArrayList<Long> list) {
		long min = list.get(0);
		for(long temp : list)
			if(temp < min) min = temp;
		return min;
	}
	
	public static double getMinimumDouble(ArrayList<Double> list) {
		double min = list.get(0);
		for(double temp : list)
			if(temp < min) min = temp;
		return min;
	}
	
	public static int getMinimumInteger(ArrayList<Integer> list) {
		int min = list.get(0);
		for(int temp : list)
			if(temp < min) min = temp;
		return min;
	}
	
	public static long getMinimum(long[] array) {
		long min = array[0];
		for(long temp : array)
			if(temp < min) min = temp;
		return min;
	}
	
	public static double getMinimum(double[] array) {
		double min = array[0];
		for(double temp : array)
			if(temp < min) min = temp;
		return min;
	}
	
	public static int getMinimum(int[] array) {
		int min = array[0];
		for(int temp : array)
			if(temp < min) min = temp;
		return min;
	}
	
	public static long getFirstQuartile(ArrayList<Long> list) {
		return list.get((int) Math.floor(list.size()/4.));		
	}
	
	public static long getMedian(ArrayList<Long> list) {
		return list.get((int) Math.floor(list.size()/2.));		
	}
	
	public static long getAverageLong(ArrayList<Long> list) {
		long sum = (long) 0;
		for(long temp : list)
			sum += temp;
		return sum/list.size();
	}
	
	public static double getAverageDouble(ArrayList<Double> list) {
		double sum = 0;
		for(double temp : list)
			sum += temp;
		return sum/list.size();
	}
	
	public static double getAverageInteger(ArrayList<Integer> list) {
		double sum = 0;
		for(int temp : list)
			sum += temp;
		return sum/list.size();
	}
	
	public static long getAverage(long[] array) {
		long sum = (long) 0;
		for(long temp : array)
			sum += temp;
		return sum/array.length;
	}
	
	public static double getAverage(double[] array) {
		double sum = 0;
		for(double temp : array)
			sum += temp;
		return sum/array.length;
	}
	
	public static double getAverage(int[] array) {
		double sum = 0;
		for(int temp : array)
			sum += temp;
		return sum/array.length;
	}
	
	public static long getThirdQuartile(ArrayList<Long> list) {
		return list.get((int) Math.floor(3*list.size()/4.));		
	}
	
	public static long getMaximumLong(ArrayList<Long> list) {
		long max = list.get(0);
		for(long temp : list)
			if(temp > max) max = temp;
		return max;
	}
	
	public static double getMaximumDouble(ArrayList<Double> list) {
		double max = list.get(0);
		for(double temp : list)
			if(temp > max) max = temp;
		return max;
	}
	
	public static int getMaximumInteger(ArrayList<Integer> list) {
		int max = list.get(0);
		for(int temp : list)
			if(temp > max) max = temp;
		return max;
	}
	
	public static long getMaximum(long[] array) {
		long max = array[0];
		for(long temp : array)
			if(temp > max) max = temp;
		return max;
	}
	
	public static double getMaximum(double[] array) {
		double max = array[0];
		for(double temp : array)
			if(temp > max) max = temp;
		return max;
	}
	
	public static int getMaximum(int[] array) {
		int max = array[0];
		for(int temp : array)
			if(temp > max) max = temp;
		return max;
	}
	
	public static int getMinimumIndex(double[] array) {
		double min = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++)
			if(array[i] < min) {min = array[i]; index = i;}
		return index;
	}
	
	public static int getMaximumIndex(double[] array) {
		double max = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++)
			if(array[i] > max) {max = array[i]; index = i;}
		return index;
	}
	
	// Computes correlation between two sets of data
	public static double getAgentsCorrelation(int[] array1, int[] array2) {
		if(array1.length != array2.length) 
			throw new Error("Tables length should be the same");
			
		int D = array1.length;
			
		double avg1 = 0;
		double avg2 = 0;
			
		double sd1 = 0;
		double sd2 = 0;
		
		double r = 0;
			
		for(int i=0; i<D; i++) {
			avg1 += array1[i];
			avg2 += array2[i];
		}
			
		avg1 /= D;
		avg2 /= D;
			
		for(int i=0; i<D; i++) {
			sd1 += Math.pow(array1[i] - avg1, 2);
			sd2 += Math.pow(array2[i] - avg2, 2);
			r += (array1[i] - avg1) * (array2[i] - avg2);
		}
			
		r /= Math.sqrt(sd1 * sd2);
			
		return r;
	}
	
	// ~ PRINTING ARRAYS ~
	public static void printArrayLong(ArrayList<Long> list, String sep) {
		for(long temp : list)
			System.out.print(convertToString(temp) + sep);
	}
	
	public static void printArrayLongln(ArrayList<Long> list) {printArrayLong(list, "\n");}
	public static void printArrayLong(ArrayList<Long> list) {printArrayLong(list, "");}
	
	public static void printArrayDouble(ArrayList<Double> list, String sep) {
		for(double temp : list)
			System.out.print(convertToString(temp) + sep);
	}
	
	public static void printArrayDoubleln(ArrayList<Double> list) {printArrayDouble(list, "\n");}
	public static void printArrayDouble(ArrayList<Double> list) {printArrayDouble(list, " ");}
	
	public static void printArrayInteger(ArrayList<Integer> list, String sep) {
		for(int temp : list)
			System.out.print(temp + sep);
	}
	
	public static void printArrayIntegerln(ArrayList<Integer> list) {printArrayInteger(list, "\n");}
	public static void printArrayInteger(ArrayList<Integer> list) {printArrayInteger(list, " ");}
	
	public static void printArray(long[] array, String sep) {
		for(long temp : array) 
			System.out.print(convertToString(temp) + sep);
	}
	
	public static void printArrayString(ArrayList<String> list, String sep) {
		for(String temp : list)
			System.out.print(temp + sep);
	}
	
	public static void printArrayStringln(ArrayList<String> list) {printArrayString(list, "\n");}
	public static void printArrayString(ArrayList<String> list) {printArrayString(list, " ");}
	
	public static void printArrayln(long[] array) {printArray(array, "\n");}
	public static void printArray(long[] array) {printArray(array, " ");}
	
	public static void printArray(double[] array, String str) {
		for(double temp : array)
			System.out.print(convertToString(temp) + str);
	}
	
	public static void printArrayln(double[] array) {printArray(array, "\n");}
	public static void printArray(double[] array) {printArray(array, " ");}
	
	public static void printArray(int[] array, String str) {
		for(int temp : array)
			System.out.print(temp + str);
	}
	
	public static void printArrayln(int[] array) {printArray(array, "\n");}
	public static void printArray(int[] array) {printArray(array, " ");}
	
	public static void printArray(String[] array, String sep) {
		for(String temp : array)
			System.out.print(temp + sep);
	}
	
	public static void printArrayln(String[] array) {printArray(array, "\n");}
	public static void printArray(String[] array) {printArray(array, " ");}
	
	// ~ GETTERS ~
	public static long getLastLong(ArrayList<Long> list) {
		return list.get(list.size()-1);
	}
	
	public static double getLastDouble(ArrayList<Double> list) {
		return list.get(list.size()-1);
	}
	
	public static int getLastInteger(ArrayList<Integer> list) {
		return list.get(list.size()-1);
	}
	
	public static long getLast(long[] array) {
		return array[array.length-1];
	}
	
	public static double getLast(double[] array) {
		return array[array.length-1];
	}
	
	public static int getLast(int[] array) {
		return array[array.length-1];
	}
	
	// ~ HISTOGRAMS ~
	public static int[][] createSimpleHistogram(int[] data) {
		int min = getMinimum(data);
		int max = getMaximum(data);
		int n = max - min + 1; // number of bins
		
		// histogram[0] - number of bin
		// histogram[1] - count in the bin
		int[][] histogram = new int[2][n];
		
		for(int i=0; i<n; i++) {
			histogram[0][i] = i + min;
			histogram[1][i] = 0;
		}
			
		for(int i=0; i<data.length; i++)
			histogram[1][data[i]-min] += 1;
		
		return histogram;
	}
	
	public static void displaySimpleHistogram(int[] data) {		
		int[][] histogram = createSimpleHistogram(data);
		
		// displays simple histogram on the console
		for(int i=0; i<histogram[0].length; i++) {
			System.out.print(histogram[0][i] + " " + (i+histogram[0][0] < 10 ? " " : ""));
			for(int j=0; j<histogram[1][i]; j++)
				System.out.print("0");
			System.out.println();
		}
	}
	
	public static int[][] createSimpleHistogram(ArrayList<Integer> data) {
		int min = getMinimumInteger(data);
		int max = getMaximumInteger(data);
		int n = max - min + 1;
		
		// histogram[0] - number of bin
		// histogram[1] - count in the bin
		int[][] histogram = new int[2][n];
		for(int i=0; i<n; i++) {
			histogram[0][i] = i + min;
			histogram[1][i] = 0;
		}
			
		for(int i=0; i<data.size(); i++)
			histogram[1][data.get(i)-min] += 1;
		
		return histogram;
	}
	
	public void displaySimpleHistogram(ArrayList<Integer> data) {
		int[][] histogram = createSimpleHistogram(data);
		
		// displays simple histogram on the console
		for(int i=0; i<histogram[0].length; i++) {
			System.out.print(histogram[0][i] + " " + (i+histogram[0][0] < 10 ? " " : ""));
			for(int j=0; j<histogram[1][i]; j++)
				System.out.print("0");
			System.out.println();
		}
	}
}
