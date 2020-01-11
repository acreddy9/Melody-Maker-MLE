import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MLE {
  
  public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
  public static final int numNotesToGenerate = 42;
  public static ArrayList<Integer> generatedNotes = new ArrayList<Integer>();
  public static String generatedNoteList;
  
  public static void main(String[] args) {
    try {
	    Scanner scnr = new Scanner(new File("train.txt"));
	    String noteString = scnr.nextLine();
	    String[] notes = noteString.split(",");
	    for(String n: notes) {
	      trainingSet.add(Integer.parseInt(n));
	    }
    }
    catch(IOException E) {
      E.printStackTrace();
    }
    
    
    // count occurrences and calculate probability of each note in training set
    int x,y,z;
    int[] count_x = new int[88];
    int[][] count_xy = new int[88][88];
    int[][][] count_xyz = new int[88][88][88];
    double[] phat_x = new double[88];
    double[][] phat_xy = new double[88][88];
    double[][][] phat_xyz = new double[88][88][88];
    for (int i = 0; i < trainingSet.size(); i++) {
        // increment count of note x
        x = trainingSet.get(i);
        count_x[x - 21]++;
        
        // if there is a successive note y, increment count of sequence xy
        if (i < trainingSet.size()-1) {
            y = trainingSet.get(i+1);
            count_xy[x - 21][y - 21]++;
            
            // if there is a second successive note z, increment count of sequence xyz
            if (i < trainingSet.size()-2) {
                z = trainingSet.get(i+2);
                count_xyz[x - 21][y - 21][z - 21]++;
            }
        }
    }
    
    // calculate probability of each note
    for (int i = 0; i < phat_x.length; i++) {
        phat_x[i] = (count_x[i] + 1.0) / (trainingSet.size() + 12.0);
    }
    
    // calculate probability of each possible sequence of two notes
    for (int i = 0; i < phat_xy.length; i++) {
        for (int j = 0; j < phat_xy[i].length; j++) {
            phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + 12.0);
        }
    }
    
    // calculate probability of each possible sequence of three notes
    for (int i = 0; i < phat_xyz.length; i++) {
        for (int j = 0; j < phat_xyz[i].length; j++) {
            for (int k = 0; k < phat_xyz[i][j].length; k++) {
                phat_xyz[i][j][k] = (count_xyz[i][j][k] + 1.0) / (count_xy[i][j] + 12.0);
            }
        }
    }
 
    
    // print count and probability of each note
//    for (int i = 0; i < count_x.length; i++) {
//        System.out.print("(" + count_x[i] + ", " + phat_x[i] + ")");
//    }
//    System.out.println();
    
    
    // generate notes based on maximum likelihood estimation
    Random randGen = new Random();
    
    // determine first note based on unigram probabilities
    int firstNote = 0;
    double firstNoteProbability = randGen.nextDouble();
    double cumulativeProbability = 0.0;
    for (int i = 0; i < phat_x.length; i++) {
    	cumulativeProbability += phat_x[i];
    	if (firstNoteProbability <= cumulativeProbability) {
    		firstNote = i;
    		generatedNotes.add(i);
    		break;
    	}
    }
    
    // given first note, determine second note based on bigram probabilities
    int secondNote = 0;
    double secondNoteProbability = randGen.nextDouble();
    cumulativeProbability = 0.0;
	for (int j = 0; j < phat_xy[firstNote].length; j++) {
		cumulativeProbability += phat_xy[firstNote][j];
		if (secondNoteProbability <= cumulativeProbability) {
    		secondNote = j;
    		generatedNotes.add(j);
    		break;
    	}
	}
	
	// given first and second notes, determine rest of notes based on trigram probabilities
	for (int n = 2; n < numNotesToGenerate; n++) {
		x = generatedNotes.get(n-2);
		y = generatedNotes.get(n-1);
		double nextNoteProbability = randGen.nextDouble();
	    cumulativeProbability = 0.0;
		
		for (int k = 0; k < phat_xyz[x][y].length; k++) {
			cumulativeProbability += phat_xyz[x][y][k];
			if (nextNoteProbability <= cumulativeProbability) {
				generatedNotes.add(k);
				break;
			}
		}
		
	}
	
	generatedNoteList = generatedNotes.toString();
	generatedNoteList = generatedNoteList.substring(1, generatedNoteList.length()-1);
	System.out.println(generatedNoteList);

  }

}