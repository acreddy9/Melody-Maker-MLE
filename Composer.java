/**
 * Driver class for Melody Maker application
 * 
 * @author atulyareddy
 *
 */
public class Composer {

	public static void main(String[] args) {
		// TODO: get input values from user, convert to hex
		setSongLength(42);
		setKeySignature(7); // G Major/E Minor
		setTimeSignature(4,4); // common time
		setTempo(96,4); // 96 quarter notes per minute
		setDynamicLevel(64); // mezzo forte
		
		// TODO: rhythm? repeated or varies throughout song?
		// TODO: volume?
		// TODO: chords, chord progression?
		// TODO: changing dynamic levels?
		
		generateNotes();
	}
	
	public static void generateNotes() {
		
	}
	
	/**
	 * 
	 * @param length The number of notes to generate
	 */
	public static void setSongLength(int length) {
		MLE.numNotesToGenerate = length;
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
	 * Sets intensity at which keys are striked. This equates to volume for basic synthesizers, 
	 * and additionally affects timber for sophisticated synthesizers.
	 * 
	 * @param velocity An intensity level 42 to 80 representing piano to forte
	 */
	public static void setDynamicLevel(int velocity) {
		
	}
	

}
