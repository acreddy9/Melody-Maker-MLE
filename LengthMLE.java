import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class calculates the probabilities of individual note lengths and sequences of 2 and 3 
 * lengths based on the training set of note lengths. A list of lengths is generated using these
 * probabilities and maximum likelihood estimation.
 * 
 * @author atulyareddy and leviredlin
 */
public class LengthMLE {
	// list of note lengths in upper staff of sheet music for training songs
	public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
	// number of note lengths to generate
	public static int numLengthsToGenerate = 42;
	// list of generated note lengths
	public static ArrayList<Integer> generatedLengths = new ArrayList<Integer>();  
	
	/**
	 * Driver method for this class
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// use the user's desired song length if provided
		if (args[0] != null) {
			numLengthsToGenerate = Integer.parseInt(args[0]);
		}
		
		// length of shortest note in training set
		int min = 2000;
		// length of longest note in training set
		int max = 0;
      
		try {
			// read file produced by TrainSet that contains the list of lengths in training songs 
			Scanner scnr = new Scanner(new File("lentrain.txt"));
			String lenString = scnr.nextLine();
			String[] lengths = lenString.split(",");
			scnr.close();
			
			// assign consecutive integers to represent length values so they are the only values
			// within the range when generating new lengths
			for(int i = 0; i < lengths.length; i++) {
				switch(Integer.parseInt(lengths[i])) {
					case 16: lengths[i] = "1"; // whole note
						break;
					case 8: lengths[i] = "2"; // half note
						break;
					case 4: lengths[i] = "3"; // quarter note
						break;
					case 2: lengths[i] = "4"; // eighth note
						break;
					case 1: lengths[i] = "5"; // sixteenth note
						break;
				}
			}
        
			for(String n: lengths) {
				int length = Integer.parseInt(n);
				trainingSet.add(length);
				if (length < min) {
					min = length;
				}
				if (length > max) {
					max = length;
				}
			}
		}
		catch(IOException E) {
			E.printStackTrace();
		}

		// range within which lengths will be generated (same as range in training set)
		int range = max-min+1;
		
		// first, second, and third lengths in a sequence of 3 pitches
		int x,y,z;
		// stores number of times each length occurs in training set
		int[] count_x = new int[range];
		// stores number of times each possible sequence of 2 lengths occurs in training set
		int[][] count_xy = new int[range][range];
		// stores number of times each possible sequence of 3 lengths occurs in training set
		int[][][] count_xyz = new int[range][range][range];
		// stores probability that each length occurs
		double[] phat_x = new double[range];
		// stores probability that each possible sequence of 2 lengths occurs
		double[][] phat_xy = new double[range][range];
		// stores probability that each possible sequence of 3 lengths occurs in training set
		double[][][] phat_xyz = new double[range][range][range];

		// count occurrences of each length, 2-length sequence, and 3-length sequence in training set
		for (int i = 0; i < trainingSet.size(); i++) {
			// increment count of length x
			x = trainingSet.get(i);
			count_x[x - min]++;
        
			// if there is a successive length y, increment count of sequence xy
			if (i < trainingSet.size()-1) {
				y = trainingSet.get(i+1);
				count_xy[x - min][y - min]++;
            
				// if there is a second successive length z, increment count of sequence xyz
				if (i < trainingSet.size()-2) {
					z = trainingSet.get(i+2);
					count_xyz[x - min][y - min][z - min]++;
				}
			}
		}
    
		// calculate probability of each length
		for (int i = 0; i < phat_x.length; i++) {
			phat_x[i] = (count_x[i] + 1.0) / (trainingSet.size() + range);
		}
    
		// calculate probability of each possible sequence of 2 lengths
		for (int i = 0; i < phat_xy.length; i++) {
			for (int j = 0; j < phat_xy[i].length; j++) {
				phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + range);
			}
		}
    
	    // calculate probability of each possible sequence of 3 lengths
	    for (int i = 0; i < phat_xyz.length; i++) {
	        for (int j = 0; j < phat_xyz[i].length; j++) {
	            for (int k = 0; k < phat_xyz[i][j].length; k++) {
	                phat_xyz[i][j][k] = (count_xyz[i][j][k] + 1.0) / (count_xy[i][j] + range);
	            }
	        }
	    }
    
	    // generate lengths based on maximum likelihood estimation
	    Random randGen = new Random();
	    
		// determine first length based on probabilities of individual lengths
	    int firstLenIndex = 0;
	    double firstLenProbability = randGen.nextDouble();
	    double cumulativeProbability = 0.0;
	    for (int i = 0; i < phat_x.length; i++) {
	        cumulativeProbability += phat_x[i];
	        if (firstLenProbability <= cumulativeProbability) {
	            firstLenIndex = i + min;
	            generatedLengths.add(firstLenIndex);
	            break;
	        }
	    }
    
		// given first length, determine second length based on probabilities of 2-length sequences
	    double secondLenProbability = randGen.nextDouble();
	    cumulativeProbability = 0.0;
	    for (int j = 0; j < phat_xy[firstLenIndex-min].length; j++) {
	        cumulativeProbability += phat_xy[firstLenIndex-min][j];
	        if (secondLenProbability <= cumulativeProbability) {
	            generatedLengths.add(j+min);
	            break;
	        }
	    }
	    
    	// given first and second lengths, determine rest of lengths based on probabilities of
		// 3-length sequences
	    for (int n = 2; n < numLengthsToGenerate; n++) {
	        x = generatedLengths.get(n-2);
	        y = generatedLengths.get(n-1);
	        double nextLenProbability = randGen.nextDouble();
	        cumulativeProbability = 0.0;
	        
	        for (int k = 0; k < phat_xyz[x-min][y-min].length; k++) {
	            cumulativeProbability += phat_xyz[x-min][y-min][k];
	            if (nextLenProbability <= cumulativeProbability) {
	            	generatedLengths.add(k+min);
	                break;
	            }
	        }
	    }
	    
	    // convert back to MIDI note length values
	    for(int i = 0; i < generatedLengths.size(); i++) {
	    	switch(generatedLengths.get(i)) {
	        	case 1: generatedLengths.set(i,16*120);
	        		break;
	        	case 2: generatedLengths.set(i,8*120);
	        		break;
	        	case 3: generatedLengths.set(i,4*120);
	        		break;
	        	case 4: generatedLengths.set(i,2*120);
	        		break;
	        	case 5: generatedLengths.set(i,1*120);
	        		break;
	    	}
	    }

	    // write list of generated velocities to songLens.txt
	    String generatedLengthsList = generatedLengths.toString().replaceAll(" ","");
	    generatedLengthsList = generatedLengthsList.substring(1, generatedLengthsList.length()-1);
	    PrintWriter p = new PrintWriter("songLens.txt");
	    p.write(generatedLengthsList);
	    p.close();
	
	  }

}
