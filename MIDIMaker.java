

import java.io.*;
import java.util.*;
import javax.sound.midi.*; // package for all midi classes
public class MIDIMaker
{
  public static void main(String argv[]) {
   
    try
    {
      int timeStamp = 1;
      Scanner scnr = new Scanner(new File("song.txt"));
      Scanner velscnr = new Scanner(new File("songVels.txt"));
      Scanner lenscnr = new Scanner(new File("songLens.txt"));
      String noteString = scnr.nextLine();
      String velString = velscnr.nextLine();
      String lenString = lenscnr.nextLine();
      String[] notes = noteString.split(",");
      String[] vels = velString.split(",");
      String[] lens = lenString.split(",");
      
//****  Create a new MIDI sequence with 24 ticks per beat  ****
        Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);

//****  Obtain a MIDI track from the sequence  ****
        Track t = s.createTrack();

//****  General MIDI sysex -- turn on General MIDI sound set  ****
        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm,(long)0);
        t.add(me);

//****  set tempo (meta event)  ****
        MetaMessage mt = new MetaMessage();
        byte[] bt = {0x02, (byte)0x00, 0x00};
        mt.setMessage(0x51 ,bt, 3);
        me = new MidiEvent(mt,(long)0);
        t.add(me);

//****  set track name (meta event)  ****
        mt = new MetaMessage();
        String TrackName = new String("Song");
        mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
        me = new MidiEvent(mt,(long)0);
        t.add(me);

//****  set omni on  ****
        ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

//****  set poly on  ****
        mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

//****  set instrument to Piano  ****
        mm = new ShortMessage();
        mm.setMessage(0xC0, 0x00, 0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

//****  New notes are made from the parsed string
       
        for(int i = 0; i < notes.length; i++) {
          mm = new ShortMessage();
         int noteTimer = 0;
          mm.setMessage(0x90,Integer.parseInt(notes[i]),Integer.parseInt(vels[i]));
          me = new MidiEvent(mm,(long)(timeStamp+noteTimer));
          t.add(me);
          noteTimer += Integer.parseInt(lens[i]);
          mm = new ShortMessage();
          mm.setMessage(0x80,Integer.parseInt(notes[i]),Integer.parseInt(vels[i]));
          me = new MidiEvent(mm,(long)timeStamp+noteTimer);
          t.add(me);
          timeStamp += 120;
        }

//****  set end of track (meta event) 19 ticks later  ****
        mt = new MetaMessage();
        byte[] bet = {}; // empty array
        mt.setMessage(0x2F,bet,0);
        me = new MidiEvent(mt, (long)140);
        t.add(me);

//****  write the MIDI sequence to a MIDI file  ****
        File f = new File("song.mid");
        MidiSystem.write(s,1,f);
    } 
        catch(Exception e)
    {
        System.out.println("Exception caught " + e.toString());
    } 
    
  } 
} 
