import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class LengthMLE {
  
  public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
  public static int numToGenerate = 42;
  public static ArrayList<Integer> generatedLen = new ArrayList<Integer>();  
  
  public static void main(String[] args) throws FileNotFoundException {
      int min = 2000;
      int max = 0;
      
      try {
        Scanner scnr = new Scanner(new File("lentrain.txt"));
        String lenString = scnr.nextLine();
        String[] lengths = lenString.split(",");
        for(int i = 0; i < lengths.length; i++) {
          switch(Integer.parseInt(lengths[i])) {
            case 16:
              lengths[i] = "1";
              break;
            case 8:
              lengths[i] = "2";
              break;
            case 4:
              lengths[i] = "3";
              break;
            case 2:
              lengths[i] = "4";
              break;
            case 1:
              lengths[i] = "5";
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
    
    // calculate probability of each possible sequence of two lengths
    for (int i = 0; i < phat_xy.length; i++) {
        for (int j = 0; j < phat_xy[i].length; j++) {
            phat_xy[i][j] = (count_xy[i][j] + 1.0) / (count_x[i] + range);
        }
    }
    
    // calculate probability of each possible sequence of three lengths
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
    int firstLenIndex = 0;
    double firstLenProbability = randGen.nextDouble();
    double cumulativeProbability = 0.0;
    for (int i = 0; i < phat_x.length; i++) {
        cumulativeProbability += phat_x[i];
        if (firstLenProbability <= cumulativeProbability) {
            firstLenIndex = i + min;
            generatedLen.add(firstLenIndex);
            break;
        }
    }
    
    // given first note, determine second note based on bigram probabilities
    double secondLenProbability = randGen.nextDouble();
    cumulativeProbability = 0.0;
    for (int j = 0; j < phat_xy[firstLenIndex-min].length; j++) {
        cumulativeProbability += phat_xy[firstLenIndex-min][j];
        if (secondLenProbability <= cumulativeProbability) {
            generatedLen.add(j+min);
            break;
        }
    }
    // given first and second notes, determine rest of lengths based on trigram probabilities
    for (int n = 2; n < numToGenerate; n++) {
        x = generatedLen.get(n-2);
        y = generatedLen.get(n-1);
        double nextLenProbability = randGen.nextDouble();
        cumulativeProbability = 0.0;
        
        for (int k = 0; k < phat_xyz[x-min][y-min].length; k++) {
            cumulativeProbability += phat_xyz[x-min][y-min][k];
            if (nextLenProbability <= cumulativeProbability) {
                generatedLen.add(k+min);
                break;
            }
        }
        
    }
    for(int i = 0; i < generatedLen.size(); i++) {
      switch(generatedLen.get(i)) {
        case 1:
          generatedLen.set(i,16);
          break;
        case 2:
          generatedLen.set(i,8);
          break;
        case 3:
          generatedLen.set(i,4);
          break;
        case 4:
          generatedLen.set(i,2);
          break;
        case 5:
          generatedLen.set(i,1);
          break;
      }
    }
    for(int i = 0; i < generatedLen.size(); i++) {
      generatedLen.set(i, generatedLen.get(i)*120);
    }
    String generatedLenList = generatedLen.toString().replaceAll(" ","");
    generatedLenList = generatedLenList.substring(1, generatedLenList.length()-1);
    PrintWriter p = new PrintWriter("songLens.txt");
    p.write(generatedLenList);
    p.close();

  }

}
