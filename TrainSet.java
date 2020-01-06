import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Random;

public class TrainSet {
	
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static String noteList = "";
    public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();

    public static void main(String[] args) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File("twinkle_twinkle.mid"));
        System.out.print("{");
        for (Track track :  sequence.getTracks()) {
      
            //System.out.println("Track " + trackNumber + ": size = " + track.size());
            //System.out.println();
            for (int i=0; i < track.size(); i++) { 
              
                MidiEvent event = track.get(i);
               // System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                       
                        // Took off -1, check later if this should have been there
                        int octave = (key / 12);
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if (octave > 0) {
                          //System.out.print(key);
                          noteList = noteList + key+",";
                          trainingSet.add(key);
                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        //System.out.println(noteName + octave + " key=" + key + " velocity: " + velocity);
                          
                        }
                        else {
                          //System.out.print(key);
                         // System.out.println();
                        }
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if (octave > 0) {
                        //System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        //System.out.println(noteName + octave + " key=" + key + " velocity: " + velocity);
                        }
                        else {
                         // System.out.println();
                        }
                          
                    } else {
                        //System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                  //  System.out.println("Other message: " + message.getClass());
                }
            }

           // System.out.println();
        }
        noteList = noteList.substring(0,noteList.length()-1);
        System.out.print(noteList);
        System.out.println("}");
        System.out.println(trainingSet.size()); // 69 = 42 (treble clef notes) + 27 (base clef notes)
        
        
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
        for (int i = 0; i < count_x.length; i++) {
        	System.out.print("(" + count_x[i] + ", " + phat_x[i] + ")");
        }
        System.out.println();
        
        // choose starting note
        Random randGen = new Random();
        int startingNote = randGen.nextInt(13);
    }
}
