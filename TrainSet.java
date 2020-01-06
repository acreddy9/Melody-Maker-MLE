import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class TrainSet {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static String noteList = "";

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
System.out.print("}");
    }
}