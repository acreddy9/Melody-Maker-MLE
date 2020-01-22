import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class VelocityMLE {
  
  public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
  public static int numToGenerate = 42;
  public static ArrayList<Integer> generatedVel = new ArrayList<Integer>();  
  
  public static void main(String[] args) throws FileNotFoundException {
      int min = 100;
      int max = 0;
      
      try {
        Scanner scnr = new Scanner(new File("veltrain.txt"));
        String velString = scnr.nextLine();
        String[] velocities = velString.split(",");
        
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
 
    
    // print count and probability of each note
//    for (int i = 0; i < count_x.length; i++) {
//        System.out.print("(" + count_x[i] + ", " + phat_x[i] + ")");
//    }
//    System.out.println();
    
    
    // generate velocities based on maximum likelihood estimation
    Random randGen = new Random();
    
    // determine first velocity based on unigram probabilities
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
    
    // given first velocity, determine second velocity based on bigram probabilities
    double secondVelProbability = randGen.nextDouble();
    cumulativeProbability = 0.0;
    for (int j = 0; j < phat_xy[firstVelIndex-min].length; j++) {
        cumulativeProbability += phat_xy[firstVelIndex-min][j];
        if (secondVelProbability <= cumulativeProbability) {
            generatedVel.add(j+min);
            break;
        }
    }
    
    // given first and second velocities, determine rest of velocities based on trigram probabilities
    for (int n = 2; n < numToGenerate; n++) {
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
    for(int i = 0; i < generatedVel.size(); i++) {
      generatedVel.set(i, generatedVel.get(i)*5);
    }
    String generatedVelList = generatedVel.toString().replaceAll(" ","");
    generatedVelList = generatedVelList.substring(1, generatedVelList.length()-1);
    PrintWriter p = new PrintWriter("songVels.txt");
    p.write(generatedVelList);
    p.close();

  }

}
