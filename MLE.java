import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MLE {
  
  public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
  public static final int numNotesToGenerate = 42;
  public static ArrayList<Integer> generatedNotes = new ArrayList<Integer>();  
  
  public static void main(String[] args) throws FileNotFoundException {
	  int min = 108;
      int max = 60;
	  
	  try {
        Scanner scnr = new Scanner(new File("train.txt"));
        String noteString = scnr.nextLine();
        String[] notes = noteString.split(",");
        
        for(String n: notes) {
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
    
    int range = max-min+1;
    
    // count occurrences and calculate probability of each note in training set
    int x,y,z;
    int[] count_x = new int[range];
    int[][] count_xy = new int[range][range];
    int[][][] count_xyz = new int[range][range][range];
    double[] phat_x = new double[range];
    double[][] phat_xy = new double[range][range];
    double[][][] phat_xyz = new double[range][range][range];
    for (int i = 0; i < trainingSet.size(); i++) {
        // increment count of note x
        x = trainingSet.get(i);
        count_x[x - min]++;
        
        // if there is a successive note y, increment count of sequence xy
        if (i < trainingSet.size()-1) {
            y = trainingSet.get(i+1);
            count_xy[x - min][y - min]++;
            
            // if there is a second successive note z, increment count of sequence xyz
            if (i < trainingSet.size()-2) {
                z = trainingSet.get(i+2);
                count_xyz[x - min][y - min][z - min]++;
            }
        }
    }
    
    // calculate probability of each note
    for (int i = 0; i < phat_x.length; i++) {
        phat_x[i] = (count_x[i] + 1.0) / (trainingSet.size() + range);
    }
    
    // calculate probability of each possible sequence of two notes
    for (int i = 0; i < phat_xy.length; i++) {
        for (int j = 0; j < phat_xy[i].length; j++) {
            phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + range);
        }
    }
    
    // calculate probability of each possible sequence of three notes
    for (int i = 0; i < phat_xyz.length; i++) {
        for (int j = 0; j < phat_xyz[i].length; j++) {
            for (int k = 0; k < phat_xyz[i][j].length; k++) {
                phat_xyz[i][j][k] = (count_xyz[i][j][k] + 1.0) / (count_xy[i][j] + range);
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
    int firstNoteIndex = 0;
    double firstNoteProbability = randGen.nextDouble();
    double cumulativeProbability = 0.0;
    for (int i = 0; i < phat_x.length; i++) {
        cumulativeProbability += phat_x[i];
        if (firstNoteProbability <= cumulativeProbability) {
            firstNoteIndex = i + min;
            generatedNotes.add(firstNoteIndex);
            break;
        }
    }
    
    // given first note, determine second note based on bigram probabilities
    double secondNoteProbability = randGen.nextDouble();
    cumulativeProbability = 0.0;
    for (int j = 0; j < phat_xy[firstNoteIndex-min].length; j++) {
        cumulativeProbability += phat_xy[firstNoteIndex-min][j];
        if (secondNoteProbability <= cumulativeProbability) {
            generatedNotes.add(j+min);
            break;
        }
    }
    
    // given first and second notes, determine rest of notes based on trigram probabilities
    for (int n = 2; n < numNotesToGenerate; n++) {
        x = generatedNotes.get(n-2);
        y = generatedNotes.get(n-1);
        double nextNoteProbability = randGen.nextDouble();
        cumulativeProbability = 0.0;
        
        for (int k = 0; k < phat_xyz[x-min][y-min].length; k++) {
            cumulativeProbability += phat_xyz[x-min][y-min][k];
            if (nextNoteProbability <= cumulativeProbability) {
                generatedNotes.add(k+min);
                break;
            }
        }
        
    }
    
    String generatedNoteList = generatedNotes.toString().replaceAll(" ","");
    generatedNoteList = generatedNoteList.substring(1, generatedNoteList.length()-1);
    PrintWriter p = new PrintWriter("song.txt");
    p.write(generatedNoteList);
    p.close();

  }

}
