/**
 * Andrew Higbee
 * A01648554
 * CS 2410
 * 
 * main method is in MidiSuite,java
 * 
 * See the readme.txt file for instructions on 
 * how to best test this device.
 **/

package MidiPiano;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequencer.SyncMode;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

public class PianoSequencer extends JPanel implements ActionListener {
	
	private static final int SeqWIDTH = 200;
	private static final int SeqHEIGHT = 100;
	
	JButton RecordB, StopB, PlayB;

	Piano piano; // = new Piano();
	
	static Sequencer seq;
	static Sequence mySeq;
	MidiEvent event;
    static Transmitter seqTransmitter;
    Track track;
    //Transmitter midiTransmitter;
    static Receiver synthReceiver;
    static Receiver seqReceiver;
    static MidiChannel channel;
    static int ChannelNum;
    static Synthesizer synth;
    static final int KeysTot = 29;
    static final int numWhiteKeysTot = 17;
    static final int numBlackKeysTot = 12;
    
    final int OCTAVES = 3;
    
    //A vector of all the keycodes used for key bindings (29 of them)
    static final int[] KeyCodes = {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C,
    	KeyEvent.VK_V, KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_M,
    	KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH, 
    	KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_G, KeyEvent.VK_H, 
    	KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_SEMICOLON, 
    	KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R, KeyEvent.VK_T,
    	KeyEvent.VK_Y, KeyEvent.VK_U, KeyEvent.VK_I, KeyEvent.VK_3,
    	KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_7, KeyEvent.VK_8};    
    
    
    public boolean[] isWhiteDown = new boolean[numWhiteKeysTot];
    public boolean[] isBlackDown = new boolean[numBlackKeysTot];
    
    
    ArrayList<MidiEvent> Events = new ArrayList<MidiEvent>();
	
	PianoSequencer()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		RecordB = new JButton("Record");
		RecordB.setActionCommand("Record");
		RecordB.addActionListener(this);
		StopB = new JButton("Stop");
		StopB.setActionCommand("Stop");
		StopB.addActionListener(this);
		PlayB = new JButton("Play");
		PlayB.setActionCommand("Play");
		PlayB.addActionListener(this);
		
		//Add the record button
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.ipady = 0;      //make this component tall
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		add(RecordB, c);
		
		//Add the stop button
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.ipady = 0;      
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		add(StopB, c);
		
		//Add the play button
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.ipady = 0;      
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		add(PlayB, c);
		
		
		setVisible(true);
		setPreferredSize(new Dimension(SeqWIDTH, SeqHEIGHT));
		
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Record")
		{
			//Events.clear();
			//setReceivers();
			//mySeq.deleteTrack(track);
			//track = mySeq.createTrack();
			/*
			try {
				seq.setSequence(mySeq);
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
			seq.setTickPosition(0);
			seq.recordEnable(track, ChannelNum);
			seq.startRecording();
		}
		
		else if(e.getActionCommand() == "Stop")
		{
			seq.stop();
			seq.setTickPosition(0);
			/*
			synth.close();
			try {
				synth.open();
			} catch (MidiUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setReceivers();
			*/
		}
		
		else if(e.getActionCommand() == "Play")
		{
			/*
			setReceivers();
			mySeq.deleteTrack(track);
			track = mySeq.createTrack();
			addEvents(track);
			*/
			MidiSuite.out("\n**Wait momentarily for playback to start.**\n");
			seq.setTickPosition(0);
			seq.start();
		}
		
	}
	
	

	interface Key {
        int WD = 16;
        int HT = (WD * 9) / 2;
        int baseNote = 48;
        int getNote ();
    }
	
	public void setReceivers()
	{
		try {
			synthReceiver = synth.getReceiver();
			seqTransmitter = seq.getTransmitter();
			seqTransmitter.setReceiver(synthReceiver);
			seqReceiver = seq.getReceiver();
		} catch (MidiUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void showReceiverInfo()
	{
		MidiSuite.out("The sequencer has " + seq.getMaxReceivers() + " receivers and "
				+ seq.getMaxTransmitters() + " transmitters.\nThe synthesizer has " +
				synth.getMaxReceivers() + " receivers and " + synth.getMaxTransmitters() +
				" transmitters.\n");
	}
	
	//When the piano key is pressed, depending on whether the sequencer
	//is recording, a MidiEvent is created
	//and adds it to the ArrayList of MidiEvents (Events).
	//If the sequencer is not recording, the note is played but the midi message isn't created.
	public void CreateOnEvent(Key key)
    {
		if(seq.isRecording())
        {
			ShortMessage myMsg = new ShortMessage();
	        try {
				myMsg.setMessage(ShortMessage.NOTE_ON, ChannelNum, key.getNote(), 127);
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        long timeStamp = synth.getMicrosecondPosition();
	        seq.setMicrosecondPosition(synth.getMicrosecondPosition());
	        long tick = seq.getTickPosition();
	        event = new MidiEvent(myMsg, tick);
	        seqReceiver.send(myMsg, timeStamp);
        	Events.add(event);
        	//track.add(event);
        	MidiSuite.out("noteOn ShortMessage sent to: " + seqReceiver.toString() + "\n" + 
                	"MidiEvent added to Events list. Event list contains " + Events.size() 
                	+ " MidiEvents. \n" + "Synth Time Stamp: " + timeStamp + 
                	"\n" + "Sequencer Tick Position: " + seq.getTickPosition() + "\n");
        }
    }
    
    public void CreateOffEvent(Key key)
    {
    	if(seq.isRecording())
    	{
    		ShortMessage myMsg = new ShortMessage();
            try {
    			myMsg.setMessage(ShortMessage.NOTE_OFF, ChannelNum, key.getNote(), 127);
    		} catch (InvalidMidiDataException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
            long timeStamp = synth.getMicrosecondPosition();
            seq.setMicrosecondPosition(synth.getMicrosecondPosition());
            long tick = seq.getTickPosition();
            //seq.setTickPosition(seq.getTickPosition() + seq.getTickLength());
            event = new MidiEvent(myMsg, tick);
            seqReceiver.send(myMsg, timeStamp);
            Events.add(event);
            //track.add(event);
            MidiSuite.out("noteOff ShortMessage sent to: " + seqReceiver.toString() + "\n" + 
                	"MidiEvent added to Events list. Event list contains " + Events.size() 
                	+ " MidiEvents. \n" + "Synth Time Stamp: " + timeStamp + 
                	"\n" + "Sequencer Tick Position: " + seq.getTickPosition() + "\n");
    	}
    
    }
    
    public void addEvents(Track strack)
    {
    	MidiEvent[] EventsArray = new MidiEvent[Events.size()];
    	for (int i = 0; i<EventsArray.length;i++)
			EventsArray[i]=Events.get(i);
    	for(int i = 0; i<EventsArray.length; i++)
    	{
    		strack.add((MidiEvent) EventsArray[i]);
    	}
    	
    	MidiSuite.out("" + EventsArray.length + " MidiEvents added to the track.");
    }
    
    
	/** 
	 * This is where the music is made. The piano makes use of the default
	 * MIDI synthesizer, and can use any of the sounds in the system's 
	 * soundbank, if there is one. Most systems have a soundbank.
	 * 
	 * The WhiteKey & BlackKey class implement the Key interface, from which
	 * they obtain the baseNote. Using the baseNote and the respective index
	 * in the array, a note is calculated and assigned to each key.
     *
     **/
	class Piano extends JLayeredPane implements MouseListener {
	    
		public static final int PianoHEIGHT = 160;
		public static final int PianoWIDTH = 380;
		
		JButton[] WhiteKeys = new JButton[numWhiteKeysTot];
	    JButton[] BlackKeys = new JButton[numBlackKeysTot];
	    InputMap im;
	    
	    Piano() {

	        setLayout(null);

	        try {
	            synth = MidiSystem.getSynthesizer ();
	            synth.open ();
	            synth.loadAllInstruments (synth.getDefaultSoundbank ());
	            Instrument [] insts = synth.getLoadedInstruments ();
	            MidiChannel channels[] = synth.getChannels ();
	            for (int i = 0; i < channels.length; i++) {
	                if (channels [i] != null) {
	                    channel = channels [i];
	                    MidiSuite.out("Channel set to: " + i + ": " + 
	                    channels[i].toString() + "\n");
	                    break;
	                }
	            }
	            
	            for (int i = 0; i < insts.length; i++) {
	                if (insts [i].toString ()
	                        .startsWith ("Instrument MidiPiano")) {
	                    channel.programChange (i);
	                    MidiSuite.out("Instrument set to: " + insts[i].getName() + "\n");
	                    break;
	                }
	            }
	        } catch (MidiUnavailableException ex) {
	            ex.printStackTrace ();
	        }
	        
	     // Get default sequencer.
	     		try {
	     			seq = MidiSystem.getSequencer();
	     			//midiTransmitter = MidiSystem.getTransmitter();
	                //seqReceiver = seq.getReceiver();
	                //midiTransmitter.setReceiver(seqReceiver);
	     		} catch (MidiUnavailableException e) {
	     			// TODO Auto-generated catch block
	     			e.printStackTrace();
	     		} 
	     		if (seq == null) {
	     		    // Error -- sequencer device is not supported.
	     		    // Inform user and return...
	     		} else {
	     		    // Acquire resources and make operational.
	     		    try {
	     				seq.open();
	     			} catch (MidiUnavailableException e) {
	     				// TODO Auto-generated catch block
	     				e.printStackTrace();
	     			}
	     		}
	     		
	     	// get transmitter of sequencer
	     		
	            try {
	            	seqReceiver = seq.getReceiver();
					synthReceiver = synth.getReceiver();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}

	            if ( synthReceiver == null )
	               System.err.println(
	                  "Receiver unavailable for sequencer" );
	     
	            try {
					seqTransmitter = seq.getTransmitter();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
	            // set receiver for transmitter to send MidiMessages
	            seqTransmitter.setReceiver( synthReceiver );
	     		
	     		try{
	     	          mySeq = new Sequence(Sequence.PPQ, 10);
	     	          track = mySeq.createTrack();
	     	          seq.setSequence(mySeq);
	     	          seq.recordEnable(track, -1);
	     	          
	     	      } catch (Exception ex) { 
	     	          ex.printStackTrace(); 
	     	      }
	     		
	     	long synthpos = synth.getMicrosecondPosition();
	        //seq.setMicrosecondPosition(synthpos);
	        seq.setMasterSyncMode(Sequencer.SyncMode.MIDI_SYNC);
	        showReceiverInfo();
	     		
	        InitKeys();
	        
	        
	        // Adds the keys to the JLayered Pane using ZOrder
	        for (int i = 0; i < 17; i++) {
	        	
	            WhiteKeys[i].setBackground(Color.WHITE);
	            WhiteKeys[i].setLocation(i * 20, 0);
	            WhiteKeys[i].setSize(20, 120);
	            WhiteKeys[i].addMouseListener(this);
	            add(WhiteKeys[i], 0, -1);
	        }

	        for (int i = 0; i < 2; i++) {

	        	BlackKeys[i].setBackground(Color.BLACK);
	        	BlackKeys[i].setLocation(i * 20 + 12, 0);
	        	BlackKeys[i].setSize(16, 80);
	        	BlackKeys[i].addMouseListener(this);
	            add(BlackKeys[i], 1, -1);   
	    
	        }
	        
	        for (int i = 2; i < 5; i++) {
	           
	        	BlackKeys[i].setBackground(Color.BLACK);
	        	BlackKeys[i].setLocation((i+1) * 20 + 12, 0);
	        	BlackKeys[i].setSize(16, 80);
	        	BlackKeys[i].addMouseListener(this);
	            add(BlackKeys[i], 1, -1);
	        }
	        
	        for (int i = 5; i < 7; i++) {

	        	BlackKeys[i].setBackground(Color.BLACK);
	        	BlackKeys[i].setLocation((i+2) * 20 + 12, 0);
	        	BlackKeys[i].setSize(16, 80);
	        	BlackKeys[i].addMouseListener(this);
	            add(BlackKeys[i], 1, -1);   
	    
	        }
	        
	        for (int i = 7; i < 10; i++) {
	            
	        	BlackKeys[i].setBackground(Color.BLACK);
	        	BlackKeys[i].setLocation((i+3) * 20 + 12, 0);
	        	BlackKeys[i].setSize(16, 80);
	        	BlackKeys[i].addMouseListener(this);
	            add(BlackKeys[i], 1, -1);
	        }
	        
	        for (int i = 10; i < 12; i++) {

	            JButton key = new BlackKey(i);
	            key.setBackground(Color.BLACK);
	            key.setLocation((i+4) * 20 + 12, 0);
	            key.setSize(16, 80);
	            key.addMouseListener(this);
	            add(key, 1, -1);   
	    
	        }
		
	        
	        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();
            mapKeyboard(im, am);
            
            setMinimumSize(new Dimension(PianoWIDTH, PianoHEIGHT));
            setVisible(true);
	    }
	    
	    // Maps the keyboard to piano layout
	    public void mapKeyboard(InputMap im, ActionMap am)
	    {
	    	int count = 0;
	    	for(int j = 0; j<10; j++)
	    	{
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, false), "KeyDown" + count + "");
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, true), "KeyUp" + count + "");
	    		
	    	    am.put("KeyDown" + count + "", new WhiteKeyDown(count, j));
	    	    am.put("KeyUp" + count + "", new WhiteKeyUp(count, j));
	    	    count++;
	    	}
	    	
	    	for(int j = 0; j<7; j++)
	    	{
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, false), "KeyDown" + count + "");
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, true), "KeyUp" + count + "");
	    		
	    	    am.put("KeyDown" + count + "", new BlackKeyDown(count, j));
	    	    am.put("KeyUp" + count + "", new BlackKeyUp(count, j));
	    	    count++;
	    	}
	    	
	    	for(int j = 10; j<17; j++)
	    	{
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, false), "KeyDown" + count + "");
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, true), "KeyUp" + count + "");
	    		
	    	    am.put("KeyDown" + count + "", new WhiteKeyDown(count, j));
	    	    am.put("KeyUp" + count + "", new WhiteKeyUp(count, j));
	    	    count++;
	    	}
	    	
	    	for(int j = 7; j<12; j++)
	    	{
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, false), "KeyDown" + count + "");
	    		im.put(KeyStroke.getKeyStroke(KeyCodes[count], 0, true), "KeyUp" + count + "");
	    		
	    	    am.put("KeyDown" + count + "", new BlackKeyDown(count, j));
	    	    am.put("KeyUp" + count + "", new BlackKeyUp(count, j));
	    	    count++;
	    	}
	    	
	    }
	    
	    // Action classes for White & Black Key Up & Down Events
	    
	    class WhiteKeyDown extends AbstractAction
		{
			int index;
	    	
			public WhiteKeyDown(int i, int j)
			{
				super("KeyDown" + i + "");
				index = j;
				putValue(Action.NAME, "KeyDown" + i + "");
				putValue(ACTION_COMMAND_KEY, "KeyDown" + i + "");
			}

			@Override
			public void actionPerformed(ActionEvent ke) {
				if(isWhiteDown[index] == false)
				{
					channel.noteOn (((WhiteKey) WhiteKeys[index]).getNote (), 127);
					isWhiteDown[index] = true;
					WhiteKeys[index].setBackground(Color.LIGHT_GRAY);
					Key key = (Key) WhiteKeys[index];
					CreateOnEvent(key);
				}
			}
		}
	    
	    class WhiteKeyUp extends AbstractAction
		{
			int index;
	    	
			public WhiteKeyUp(int i, int j)
			{
				super("KeyUp" + i + "");
				index  = j;
				putValue(Action.NAME, "KeyUp" + i + "");
				putValue(ACTION_COMMAND_KEY, "KeyUp" + i + "");
			}

			@Override
			public void actionPerformed(ActionEvent ke) {
				if(isWhiteDown[index] == true)
				{
					channel.noteOff (((WhiteKey) WhiteKeys[index]).getNote (), 127);
					isWhiteDown[index] = false;
					WhiteKeys[index].setBackground(Color.WHITE);
					Key key = (Key) WhiteKeys[index];
					CreateOffEvent(key);
				}
			}
		}
	    
	    class BlackKeyDown extends AbstractAction
		{
			int index;
	    	
			public BlackKeyDown(int i, int j)
			{
				super("KeyDown" + i + "");
				index = j;
				putValue(Action.NAME, "KeyDown" + i + "");
				putValue(ACTION_COMMAND_KEY, "KeyDown" + i + "");
			}

			@Override
			public void actionPerformed(ActionEvent ke) {
				if(isBlackDown[index] == false)
				{
					channel.noteOn (((BlackKey) BlackKeys[index]).getNote (), 127);
					isBlackDown[index] = true;
					BlackKeys[index].setBackground(Color.DARK_GRAY);
					Key key = (Key) BlackKeys[index];
					CreateOnEvent(key);
				}
			}
		}
	    
	    class BlackKeyUp extends AbstractAction
		{
			int index;
	    	
			public BlackKeyUp(int i, int j)
			{
				super("KeyUp" + i + "");
				index = j;
				putValue(Action.NAME, "KeyUp" + i + "");
				putValue(ACTION_COMMAND_KEY, "KeyUp" + i + "");
			}

			@Override
			public void actionPerformed(ActionEvent ke) {
				if(isBlackDown[index] == true)
				{
					channel.noteOff (((BlackKey) BlackKeys[index]).getNote (), 127);
					isBlackDown[index] = false;
					BlackKeys[index].setBackground(Color.BLACK);
					Key key = (Key) BlackKeys[index];
					CreateOffEvent(key);
				}
			}
		}
	    
	    // Initialize the JButtons to Keys
	    public void InitKeys()
	    {
	    	for(int i = 0; i<17; i++)
	    	{
	    		WhiteKeys[i] = new WhiteKey(i);
	    	}
	    	
	    	for(int i = 0; i<12; i++)
	    	{
	    		BlackKeys[i] = new BlackKey(i);
	    	}
	    }
	    
	    public void mousePressed (MouseEvent e) {
	        Key key = (Key) e.getSource ();
	        channel.noteOn (key.getNote (), 127);
	        CreateOnEvent(key);
	    }
	    
	    public void mouseReleased (MouseEvent e) {
	        Key key = (Key) e.getSource ();
	        channel.noteOff (key.getNote ());
	        CreateOffEvent(key);
	    }
	    
	    public void mouseClicked (MouseEvent e) { }
	    public void mouseEntered (MouseEvent e) { }
	    public void mouseExited (MouseEvent e) { }
	     
	     // Key classes - calculates note from base note
	    class BlackKey extends JButton implements Key {
	        
	        final int note;
	        
	        public BlackKey (int pos) {
	            note = baseNote + 1 + 2 * pos + (pos + 3) / 5 + pos / 5;
	            int left = 10 + WD
	                    + ((WD * 3) / 2) * (pos + (pos / 5)
	                    + ((pos + 3) / 5));
	            setBackground (Color.BLACK);
	            setBounds (left, 10, WD, HT);
	        }
	        
	        public int getNote () {
	            return note;
	        }
	    }
	     
	     
	    class WhiteKey  extends JButton implements Key {
	        
	        static final int WWD = (WD * 3) / 2;
	        static final int WHT = (HT * 3) / 2;
	        final int note;
	        
	        public WhiteKey (int pos) {
	            
	            note = baseNote + 2 * pos
	                    - (pos + 4) / 7
	                    - pos / 7;
	            int left = 10 + WWD * pos;
	            setBounds (left, 10, WWD, WHT);
	            setBackground (Color.WHITE);
	            setBorder(BorderFactory.createLineBorder(Color.black));
	        }
	        
	        public int getNote () {
	            return note;
	        }
	    
	    }
	}
	
	
}