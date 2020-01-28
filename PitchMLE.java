import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class calculates the probabilities of individual pitches and sequences of two and three
 * pitches based on the training set of pitches. A list of pitches is generated using these
 * probabilities and maximum likelihood estimation.
 * 
 * @author atulyareddy and leviredlin
 */
public class PitchMLE {
   
	// list of pitch values in upper staff of sheet music for training songs
	public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
	// number of pitches to generate
	public static int numPitchesToGenerate = 42;
	// list of generated pitches
	public static ArrayList<Integer> generatedPitches = new ArrayList<Integer>();  

	/**
	 * Driver method for this class
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// use the user's desired song length if provided
		if (args[0] != null) {
			numPitchesToGenerate = Integer.parseInt(args[0]);
		}
		// lowest pitch in the training set
		int min = 108;
		// highest pitch in the training set
		int max = 60;
	  
		try {
			// read file produced by TrainSet that contains the list of pitches in training songs 
			Scanner scnr = new Scanner(new File("pitchtrain.txt"));
	        String trainString = scnr.nextLine();
	        String[] trainPitches = trainString.split(",");
	        scnr.close();
	        for(String n: trainPitches) {
	        	int midiNum = Integer.parseInt(n);
	        	trainingSet.add(midiNum);
	        	if (midiNum < min) {
	        		min = midiNum;
	        	}
	        	if (midiNum > max) {
	        		max = midiNum;
	        	}
	        }
		}
		catch(IOException E) {
			E.printStackTrace();
		}
    
		// range within which pitches will be generated (same as range in training set)
		int range = max-min+1;
    
		// first, second, and third pitches in a sequence of 3 pitches
		int x,y,z;
		// stores number of times each pitch occurs in training set
		int[] count_x = new int[range];
		// stores number of times each possible sequence of 2 pitches occurs in training set
		int[][] count_xy = new int[range][range];
		// stores number of times each possible sequence of 3 pitches occurs in training set
		int[][][] count_xyz = new int[range][range][range];
		// stores probability that each pitch occurs
		double[] phat_x = new double[range];
		// stores probability that each possible sequence of 2 pitches occurs
		double[][] phat_xy = new double[range][range];
		// stores probability that each possible sequence of 3 pitches occurs in training set
		double[][][] phat_xyz = new double[range][range][range];
		
		// count occurrences of each pitch, 2-pitch sequence, and 3-pitch sequence in training set
		for (int i = 0; i < trainingSet.size(); i++) {
			// increment count of pitch x
			x = trainingSet.get(i);
			count_x[x - min]++;
			
			// if there is a successive pitch y, increment count of sequence xy
			if (i < trainingSet.size()-1) {
				y = trainingSet.get(i+1);
				count_xy[x - min][y - min]++;
				
				// if there is a second successive pitch z, increment count of sequence xyz
				if (i < trainingSet.size()-2) {
					z = trainingSet.get(i+2);
					count_xyz[x - min][y - min][z - min]++;
				}
			}
		}
		
		// calculate probability of each pitch
		for (int i = 0; i < phat_x.length; i++) {
			phat_x[i] = (count_x[i] + 1.0) / (trainingSet.size() + range);
		}
		
		// calculate probability of each possible sequence of 2 pitches
		for (int i = 0; i < phat_xy.length; i++) {
			for (int j = 0; j < phat_xy[i].length; j++) {
				phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + range);
			}
		}
		
		// calculate probability of each possible sequence of 3 pitches
		for (int i = 0; i < phat_xyz.length; i++) {
			for (int j = 0; j < phat_xyz[i].length; j++) {
				for (int k = 0; k < phat_xyz[i][j].length; k++) {
					phat_xyz[i][j][k] = (count_xyz[i][j][k] + 1.0) / (count_xy[i][j] + range);
				}
			}
		}
    

		// generate pitches based on maximum likelihood estimation
		Random randGen = new Random();
		
		// determine first pitch based on probabilities of individual pitches
		int firstPitch = 0;
		double firstPitchProbability = randGen.nextDouble();
		double cumulativeProbability = 0.0;
		for (int i = 0; i < phat_x.length; i++) {
			cumulativeProbability += phat_x[i];
			if (firstPitchProbability <= cumulativeProbability) {
				firstPitch = i + min;
				generatedPitches.add(firstPitch);
				break;
			}
		}
		
		// given first pitch, determine second pitch based on probabilities of 2-pitch sequences
		double secondPitchProbability = randGen.nextDouble();
		cumulativeProbability = 0.0;
		for (int j = 0; j < phat_xy[firstPitch-min].length; j++) {
			cumulativeProbability += phat_xy[firstPitch-min][j];
			if (secondPitchProbability <= cumulativeProbability) {
				generatedPitches.add(j+min);
				break;
			}
		}
    
    	// given first and second pitches, determine rest of notes based on probabilities of
		// 3-pitch sequences
    	for (int n = 2; n < numPitchesToGenerate; n++) {
    		x = generatedPitches.get(n-2);
    		y = generatedPitches.get(n-1);
    		double nextNoteProbability = randGen.nextDouble();
    		cumulativeProbability = 0.0;
        
    		for (int k = 0; k < phat_xyz[x-min][y-min].length; k++) {
    			cumulativeProbability += phat_xyz[x-min][y-min][k];
    			if (nextNoteProbability <= cumulativeProbability) {
    				generatedPitches.add(k+min);
    				break;
    			}
    		}
    	}
    
	    // write list of generated velocities to songPitches.txt
    	String generatedPitchList = generatedPitches.toString().replaceAll(" ","");
    	generatedPitchList = generatedPitchList.substring(1, generatedPitchList.length()-1);
    	PrintWriter p = new PrintWriter("songPitches.txt");
    	p.write(generatedPitchList);
    	p.close();
  }

}
