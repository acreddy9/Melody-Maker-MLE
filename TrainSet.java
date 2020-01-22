import java.io.File;
import java.io.PrintWriter;
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
    
    public static String velList = "";
    public static ArrayList<Integer> VTrainingSet = new ArrayList<Integer>();
    public static ArrayList<int[]> onTicks = new ArrayList<int[]>();
    public static ArrayList<int[]> offTicks = new ArrayList<int[]>();
    public static String lengthList = "";

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
    public static void main(String[] args) throws Exception {
      final File folder = new File("trainsongs");
        
      PrintWriter p = new PrintWriter("train.txt");
      PrintWriter v = new PrintWriter("veltrain.txt");
      PrintWriter l = new PrintWriter("lentrain.txt");
      ArrayList<String> names = listFilesForFolder(folder);
      
      for(String fileName:names) {
        Sequence sequence = MidiSystem.getSequence(new File("trainsongs"+File.separator+fileName));
        
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
                    
                    
                   if (sm.getCommand() == NOTE_ON && (sm.getData2() != 0)) {
                       
                        int key = sm.getData1();
                       
                        // Took off -1, check later if this should have been there
                        int octave = (key / 12);
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        // only middle C and higher (octave > 0 gets the base clef)
                        if (octave > 4) {
                          
                          //System.out.print(key);
                          noteList = noteList + key+",";
                          velocity = (Math.round(velocity/5));
                          velList = velList + velocity +",";
                          trainingSet.add(key);
                          onTicks.add(new int[]{(int) event.getTick(), key});
                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        //System.out.println(noteName + octave + " key=" + key + " velocity: " + velocity);
                          
                        }
                        else {
                          //System.out.print(key);
                         // System.out.println();
                        }
                    } else if (sm.getCommand() == NOTE_OFF || ((sm.getData2() == 0) && (sm.getCommand() == NOTE_ON)))  {
                        int key = sm.getData1();
                        int octave = (key / 12);
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        if (octave > 4) {
                        offTicks.add(new int[]{(int) event.getTick(), key});
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
        
         
        if (names.indexOf(fileName) == names.size()-1) {
          noteList = noteList.substring(0,noteList.length()-1);
          velList = velList.substring(0,velList.length()-1);
        }
        p.print(noteList);
        v.print(velList); 
      }
      p.close();
         v.close();
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
