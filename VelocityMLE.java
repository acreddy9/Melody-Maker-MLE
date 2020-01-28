import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class calculates the probabilities of individual note velocities and sequences of 2 and 3
 * velocities based on the training set of velocities. A list of velocities is generated using
 * these probabilities and maximum likelihood estimation.
 * 
 * @author atulyareddy and leviredlin
 */
public class VelocityMLE {
	// list of note velocities in upper staff of sheet music for training songs
	public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
	// number of note velocities to generate
	public static int numVelocitiesToGenerate = 42;
	// list of generated note velocities
	public static ArrayList<Integer> generatedVel = new ArrayList<Integer>();  
  
	public static void main(String[] args) throws FileNotFoundException {
		// Use the user's desired song length if provided
		if (args[0] != null) {
			numVelocitiesToGenerate = Integer.parseInt(args[0]);
		}
		// velocity of the quietest note in training set
		int min = 100;
		// velocity of the loudest note in training set
		int max = 0;
      
		try {
			// read file produced by TrainSet that contains the list of velocities in training songs 
			Scanner scnr = new Scanner(new File("veltrain.txt"));
			String velString = scnr.nextLine();
			String[] velocities = velString.split(",");
			scnr.close();
			for(String n: velocities) {
				int velocity = Integer.parseInt(n);
				trainingSet.add(velocity);
				if (velocity < min) {
					min = velocity;	
				}
				if (velocity > max) {
					max = velocity;
				}
			}
		}
		catch(IOException E) {
			E.printStackTrace();
		}
    
		// range within which velocities will be generated (same as range in training set)
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
		
		// count occurrences of each velocity, 2-velocity sequence, and 3-velocity sequence in training set
	    for (int i = 0; i < trainingSet.size(); i++) {
	        // increment count of velocity x
	        x = trainingSet.get(i);
	        count_x[x - min]++;
	        
	        // if there is a successive velocity y, increment count of sequence xy
	        if (i < trainingSet.size()-1) {
	            y = trainingSet.get(i+1);
	            count_xy[x - min][y - min]++;
	            
	            // if there is a second successive velocity z, increment count of sequence xyz
	            if (i < trainingSet.size()-2) {
	                z = trainingSet.get(i+2);
	                count_xyz[x - min][y - min][z - min]++;
	            }
	        }
	    }
	    
	    // calculate probability of each velocity
	    for (int i = 0; i < phat_x.length; i++) {
	        phat_x[i] = (count_x[i] + 1.0) / (trainingSet.size() + range);
	    }
	    
	    // calculate probability of each possible sequence of two velocities
	    for (int i = 0; i < phat_xy.length; i++) {
	        for (int j = 0; j < phat_xy[i].length; j++) {
	            phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + range);
	        }
	    }
	    
	    // calculate probability of each possible sequence of three velocities
	    for (int i = 0; i < phat_xyz.length; i++) {
	        for (int j = 0; j < phat_xyz[i].length; j++) {
	            for (int k = 0; k < phat_xyz[i][j].length; k++) {
	                phat_xyz[i][j][k] = (count_xyz[i][j][k] + 1.0) / (count_xy[i][j] + range);
	            }
	        }
	    }
	    
	    
	    // generate velocities based on maximum likelihood estimation
	    Random randGen = new Random();
	    
		// determine first velocity based on probabilities of individual velocities
	    int firstVelIndex = 0;
	    double firstVelProbability = randGen.nextDouble();
	    double cumulativeProbability = 0.0;
	    for (int i = 0; i < phat_x.length; i++) {
	        cumulativeProbability += phat_x[i];
	        if (firstVelProbability <= cumulativeProbability) {
	            firstVelIndex = i + min;
	            generatedVel.add(firstVelIndex);
	            break;
	        }
	    }
	    
		// given first velocity, determine second velocity based on probabilities of 2-velocity sequences
	    double secondVelProbability = randGen.nextDouble();
	    cumulativeProbability = 0.0;
	    for (int j = 0; j < phat_xy[firstVelIndex-min].length; j++) {
	        cumulativeProbability += phat_xy[firstVelIndex-min][j];
	        if (secondVelProbability <= cumulativeProbability) {
	            generatedVel.add(j+min);
	            break;
	        }
	    }
	    
	    // given first and second velocities, determine rest of velocities based on probabilities of
	 	// 3-velocities sequences
	    for (int n = 2; n < numVelocitiesToGenerate; n++) {
	        x = generatedVel.get(n-2);
	        y = generatedVel.get(n-1);
	        double nextVelProbability = randGen.nextDouble();
	        cumulativeProbability = 0.0;
	        
	        for (int k = 0; k < phat_xyz[x-min][y-min].length; k++) {
	            cumulativeProbability += phat_xyz[x-min][y-min][k];
	            if (nextVelProbability <= cumulativeProbability) {
	                generatedVel.add(k+min);
	                break;
	            }
	        }
	        
	    }
	    
	    // convert back to MIDI velocity values
	    for(int i = 0; i < generatedVel.size(); i++) {
	      generatedVel.set(i, generatedVel.get(i)*5);
	    }
	    
	    // write list of generated velocities to songVels.txt
	    String generatedVelList = generatedVel.toString().replaceAll(" ","");
	    generatedVelList = generatedVelList.substring(1, generatedVelList.length()-1);
	    PrintWriter p = new PrintWriter("songVels.txt");
	    p.write(generatedVelList);
	    p.close();
	
	  }

}
