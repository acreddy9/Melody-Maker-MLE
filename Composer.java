import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * Driver class for Melody Maker application
 * 
 * @author atulyareddy
 *
 */
public class Composer {

  public static void main(String[] args) {
    // For future use
    // setSongLength(42);
    // setKeySignature(7); // G Major/E Minor
    // setTimeSignature(4,4); // common time
    // setTempo(96,4); // 96 quarter notes per minute
    // setDynamicLevel(64); // mezzo forte

    // Prompts the user to input the number of notes that they want
    try {
      System.out.println("How many notes would you like to generate?");
      Scanner input = new Scanner(System.in);
      int notes = 0;
      if (input.hasNextInt()) {
        notes = input.nextInt();
      }
      String[] noteArray = new String[] {"" + notes};
      input.close();

      // Calls classes in order to generate a train set, run MLE on each aspect of the song and
      // generate a new song
      TrainSet.main(noteArray);
      PitchMLE.main(noteArray);
      VelocityMLE.main(noteArray);
      LengthMLE.main(noteArray);
      MIDIMaker.main(noteArray);
      try {
        // Get the default Sequencer
        Sequencer sequencer = MidiSystem.getSequencer();
        if (sequencer == null) {
          System.err.println("Sequencer device not supported");
          return;
        }
        // Open device
        sequencer.open();
        // Create sequence, the File must contain MIDI file data.
        Sequence sequence = MidiSystem.getSequence(new File("song.mid"));
        // load it into sequencer
        sequencer.setSequence(sequence);
        // start the playback
        sequencer.start();
      } catch (MidiUnavailableException | InvalidMidiDataException | IOException ex) {
        ex.printStackTrace();
      }
    } catch (Exception E) {
      E.printStackTrace();
    }
  }

  // Further methods are for future use
  public static void generateNotes() {

  }

  /**
   * 
   * @param length The number of notes to generate
   */
  public static void setSongLength(int length) {
    PitchMLE.numPitchesToGenerate = length;
  }

  /**
   * 
   * @param key A key 0 to 12 representing C Major/A Minor to B Major/G# Minor
   */
  public static void setKeySignature(int key) {

  }

  /**
   * 
   * @param beatsPerMeasure The top number in the time signature
   * @param lengthOfBeat The bottom number in the time signature
   */
  public static void setTimeSignature(int beatsPerMeasure, int lengthOfBeat) {

  }

  /**
   * 
   * @param beatsPerMinute
   * @param lengthOfBeat
   */
  public static void setTempo(int beatsPerMinute, int lengthOfBeat) {

  }

  /**
   * Sets intensity at which keys are struck. This equates to volume for basic synthesizers, and
   * additionally affects timber for sophisticated synthesizers.
   * 
   * @param velocity An intensity level 42 to 80 representing piano to forte
   */
  public static void setDynamicLevel(int velocity) {

  }


}
