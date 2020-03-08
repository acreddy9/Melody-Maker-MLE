import java.io.File;
import java.io.PrintWriter;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;

public class TrainSet {
    
    // Fields to store note names and functions for ease of use
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    
    // String to store the notes found in the midi file
    public static String noteList = "";
    public static ArrayList<Integer> trainingSet = new ArrayList<Integer>();
    
    // String to store the velocities for each note in the train set
    public static String velList = "";
    public static ArrayList<Integer> VTrainingSet = new ArrayList<Integer>();
    
    // Stores on and off times to generate note lengths
    public static ArrayList<int[]> onTicks = new ArrayList<int[]>();
    public static ArrayList<int[]> offTicks = new ArrayList<int[]>();
    public static String lengthList = "";

    // Determines the files in the train set and adds them to a list that will be iterated through 
    public static ArrayList<String> listFilesForFolder(final File folder) {
      ArrayList<String> names = new ArrayList<String>();
      for (final File fileEntry : folder.listFiles()) {
          if (fileEntry.isDirectory()) {
              listFilesForFolder(fileEntry);
          } else {
              names.add(fileEntry.getName());
          }
      }
      return names;
  }
    
    // Driver method for class 
    public static void main(String[] args) throws Exception {
      final File folder = new File("trainsongs");
        
      // Printwriters for each of the separate MLE classes
      PrintWriter p = new PrintWriter("pitchtrain.txt");
      PrintWriter v = new PrintWriter("veltrain.txt");
      PrintWriter l = new PrintWriter("lentrain.txt");
      ArrayList<String> names = listFilesForFolder(folder);
      
      // Iterates through each file in the train set 
      for(String fileName:names) {
        Sequence sequence = MidiSystem.getSequence(new File("trainsongs"+File.separator+fileName));
          
         // Resets printwriters
         p = new PrintWriter("pitchtrain.txt");
         v = new PrintWriter("veltrain.txt");
         l = new PrintWriter("lentrain.txt");
          
        // Iterates through the track 
        for (Track track :  sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) { 
              
              // Gets the message for each track to determine notes
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;    
                    
                    // Checks for valid note on messages
                   if (sm.getCommand() == NOTE_ON && (sm.getData2() != 0)) {
                       
                     // The key value from the note
                        int key = sm.getData1();
                       
                        // Determines octave from key
                        int octave = (key / 12);
                        
                        // Velocity data from notes
                        int velocity = sm.getData2();
                        // only middle C and higher (octave > 0 gets the base clef)
                        if (octave > 4) {
                          
                          // Adds each note, velocity and time stamp to the appropriate variables
                          noteList = noteList + key+",";
                          velocity = (Math.round(velocity/5));
                          velList = velList + velocity +",";
                          trainingSet.add(key);
                          onTicks.add(new int[]{(int) event.getTick(), key});
                        }
                        
                    } else if (sm.getCommand() == NOTE_OFF || ((sm.getData2() == 0) && (sm.getCommand() == NOTE_ON)))  {
                      
                      // Adds note off to variables to calculate note length
                        int key = sm.getData1();
                        int octave = (key / 12);
                        if (octave > 4) {
                        offTicks.add(new int[]{(int) event.getTick(), key});
                        }     
                    } 
                } 
            }
        }
        
        // Adds to the note and velocity list for the train set to be generated
        if (names.indexOf(fileName) == names.size()-1) {
          noteList = noteList.substring(0,noteList.length()-1);
          velList = velList.substring(0,velList.length()-1);
        }
        p.print(noteList);
        v.print(velList); 
        // Closes file writers
        p.close();
        v.close();  
          
      }
   
         
         // Calculates note lengths by removing one on timestamp at a time and finding its associated off timestamp and determining the difference
      while(!onTicks.isEmpty()) {
        int[] curr = onTicks.get(0);
        onTicks.remove(0);
        for(int i = 0; i < offTicks.size(); i++) {
          if (offTicks.get(i)[1] == curr[1]) {
            
            int noteLength = (offTicks.get(i)[0]-curr[0]);
            noteLength = Math.round(noteLength/120);
            int a = noteLength;
              a =  a == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(a - 1);
            noteLength = (int)Math.pow(2,a);
            
            if (noteLength > 16) {
              noteLength = 16;
            }
            lengthList += noteLength +",";
            offTicks.remove(i);
            break;
          }
        }
      }
      lengthList = lengthList.substring(0,lengthList.length()-1);
      l.print(lengthList);
      l.close();
    }
}
