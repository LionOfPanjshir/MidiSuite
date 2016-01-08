/**
 * MidiSuite - By Andrew Higbee
 * CS 2410
 * 
 * The main method is at the bottom of this file.
 * 
 * The MidiSuite program will allow you to view the MIDI device information
 * on your system, and if a soundbank is available, you will be able to 
 * choose from the soundbank instruments.
 * 
 * Recording capability is limited. After recording, click "Stop" and then "Play".
 * Wait momentarily for playback to start.
 */

package MidiPiano;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import javax.sound.midi.*;
import javax.swing.JFrame;

import MidiPiano.PianoSequencer.Piano;

public class MidiSuite extends JFrame implements ActionListener
{
	
    PianoSequencer pianoSeq;
	
	private static final int WIDTH = 660;
	private static final int HEIGHT = 420;
	
	// For the GridBagLayout
	final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    
	static private final String newline = "\n";
	
	JFileChooser fc;
	
	File MidiTestFile;
	FileInputStream iFile;
	public JMenuBar menuMB = new JMenuBar();
	public JMenu FileM, MidiM, HelpM;
	public JMenuItem OpenI, SaveI, ExitI, ShowMidiI, MidiTestI, GuideI;
	
	//Status updates sent to the text area
	public static JTextArea textArea;
	JComboBox midiOuts;
	JComboBox midiIns;
	JComboBox midiInstr;
	JComboBox midiChans;
	JButton testB;
	public TestButtonHandler TestH;
	Container pane;
	
	MidiDevice inPort;
	MidiDevice outPort;
	
	Receiver recTrans;
	
	@SuppressWarnings("unchecked")
	MidiSuite()
	{ 
		setJMenuBar(menuMB);
		setMenu();
		
		textArea = new JTextArea(5,5);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText("");
		
		pianoSeq = new PianoSequencer();
		pianoSeq.piano = pianoSeq.new Piano();
		
		pane = getContentPane();
		if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
		c.fill = GridBagConstraints.HORIZONTAL;
		}

        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		JPanel labelPanel = new JPanel(new GridLayout(1,4));
		JPanel CBPanel = new JPanel(new GridLayout(1,4));
		
		JLabel inL = new JLabel("Midi Input Devices:", SwingConstants.CENTER);
		JLabel outL = new JLabel("Midi Output Devices:", SwingConstants.CENTER);
		JLabel instrL = new JLabel("Midi Instruments:", SwingConstants.CENTER);
		JLabel chanL = new JLabel("Channels:", SwingConstants.CENTER);
		
		
		String[] resOuts = getMidiPorts(false);
		midiOuts = new JComboBox(resOuts);
		if (resOuts.length != 0)
			midiOuts.setSelectedIndex(0);

		String[] resIns = getMidiPorts(true);
		midiIns = new JComboBox(resIns);
		if (resIns.length != 0)
			midiIns.setSelectedIndex(0);
		
		String[] resInstr = getMidiInstr();
		midiInstr = new JComboBox(resInstr);
		//if (resInstr.length != 0)
			//midiInstr.setSelectedIndex(0);
		midiInstr.addActionListener(new instrCBHandler());
		
		String[] resChans = getMidiChans();
		midiChans = new JComboBox(resChans);
		//if (resInstr.length != 0)
			//midiInstr.setSelectedIndex(0);chanCBHandler
		midiChans.addActionListener(new chanCBHandler());
		
		
		testB = new JButton("Test");
		testB.setActionCommand("Test");
		testB.addActionListener(TestH);

		labelPanel.add(inL);
		labelPanel.add(outL);
		labelPanel.add(instrL);
		labelPanel.add(chanL);
		
		CBPanel.add(midiIns);
		CBPanel.add(midiOuts);
		CBPanel.add(midiInstr);
		CBPanel.add(midiChans);
		//CBPanel.add(testB);
		
		//Add label panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(1,1,0,1);
		c.ipady = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(labelPanel, c);
		
		//Add ComboBox panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(0,1,1,1);
		c.ipady = 0;
		//c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 2;
		pane.add(CBPanel, c);
		
		//Add the TextArea
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.ipady = 80;      //add this many pixels to the components height
		//c.weightx = 0.0;
		c.insets = new Insets(5,5,5,5);
		c.gridwidth = 1;
		c.gridheight = 2;
		c.gridx = 2;
		c.gridy = 3;
		//pane.add(textArea, c);
		pane.add(areaScrollPane, c);
		
		//Add the Piano
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(100,2,2,2);
		c.anchor = GridBagConstraints.PAGE_END;
		c.ipady = 0;      //make this component tall
		//c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.gridx = 2;
		c.gridy = 4;
		pane.add(pianoSeq.piano, c);
		
		//Add the Sequencer
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(5,5,5,5);
		c.ipady = 0;      //make this component tall
		//c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		pane.add(pianoSeq, c);
		
		pack();
		setVisible(true);
		
	}
	
	//Instrument combobox handler
	public class instrCBHandler implements ActionListener
	{	
		@Override
		public void actionPerformed(ActionEvent ie) {
			String Instrument;
			int index;
			JComboBox cb = (JComboBox)ie.getSource();
			Instrument = String.valueOf(cb.getSelectedItem());//protects against nulls
			index = cb.getSelectedIndex();
			if (Instrument != null)
			{
				PianoSequencer.channel.programChange (index);
				out("Instrument set to: " + Instrument);
			}
			pane.requestFocus();
		}

	}
	
	//Channel ComboBox Handler
	public class chanCBHandler implements ActionListener
	{	
		@Override
		public void actionPerformed(ActionEvent ie) {
			String chan;
			int index;
			MidiChannel channels[] = PianoSequencer.synth.getChannels ();
			JComboBox cb = (JComboBox)ie.getSource();
			chan = String.valueOf(cb.getSelectedItem());//protects against nulls
			index = cb.getSelectedIndex();
			if (chan != null)
			{
				PianoSequencer.channel = channels[index];
				PianoSequencer.ChannelNum = index; 
				out("Channel set to: " + index + ".\n");
				if(index == 0)
				{
					out("Channel 0 is the default percussion channel. For best results when recording, " +
							"set channel to something other than 0 if possible.\n");
				}
			}
			pane.requestFocus();
		}

	}
	
	public static void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem mItem = (JMenuItem) e.getSource();
		
		if (e.getSource() == OpenI)
		{
			int returnVal = fc.showOpenDialog(MidiSuite.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                out("Opening: " + file.getName() + "." + newline);
            } else {
                out("Open command cancelled by user." + newline);
            }
            textArea.setCaretPosition(textArea.getDocument().getLength());
			
		}
		
		else if (e.getSource() == SaveI) {
            int returnVal = fc.showSaveDialog(fc);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                out("Saving: " + file.getName() + "." + newline);
            } else {
                out("Save command cancelled by user." + newline);
            }
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
		
		else if (mItem == ExitI)
		{
			System.exit(0);
		}
		
		else if (mItem == GuideI)
		{
			out("Welcome to MidiSuite!\n -Use your mouse or your keyboard to play the piano.\n" +
					" -Keys 'Z' through '/' play the lower 10 white keys.\n" +
					" -Keys 'S' & 'D' correspond to the black piano keys above" +
					" the 'Z', 'X', & 'C' white keys, 'G', 'H', and 'J' correspond to the black piano" +
					" keys above 'V', 'B', 'N', and 'M', and so forth.\n" +
					" -Keys 'W' through 'I' correspond to the upper 7 white keys.\n" +
					" -Keys '3' & '4' correspond to the black keys above 'W', 'E', & 'R', " +
					"and so forth.\n\n" +
					" -Select different instruments from the Midi Instruments drop down box.\n" +
					"(if your system doesn't have a soundbank, this may be unavailable).\n\n" +
					" -Select the Channel to record on. Channel 0 is typically used for percussion," +
					" so you may want to choose a channel other than 0 for recording an instrument.\n\n" +
					" -The Midi Input and Output Dropdown boxes will show your system's Midi Devices, " +
					"if there are any.\n");
		}
		
	}
	
	
	protected void setMenu()
	{
		FileM = new JMenu("File");
		MidiM = new JMenu("Midi");
		HelpM = new JMenu("Help");
		menuMB.add(FileM);
		//Functionality to be added later
		//menuMB.add(MidiM);
		menuMB.add(HelpM);
		
		OpenI = new JMenuItem("Open Midi FIle");
		SaveI = new JMenuItem("Save Midi FIle");
		ExitI = new JMenuItem("Exit");
		
		ShowMidiI = new JMenuItem("System Midi Ioformation");
		MidiTestI = new JMenuItem("Test Your Midi");
		
		GuideI = new JMenuItem("Guide");
		
		FileM.add(OpenI);
		FileM.add(SaveI);
		FileM.add(ExitI);
		HelpM.add(GuideI);
		//MidiM.add(ShowMidiI);
		//MidiM.add(MidiTestI); 
		
		OpenI.addActionListener(this);
		SaveI.addActionListener(this);
		ExitI.addActionListener(this);
		ShowMidiI.addActionListener(this);
		MidiTestI.addActionListener(this);
		GuideI.addActionListener(this);
	}
	
	public class TestButtonHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent be) {
			if (be.getActionCommand() == "Test")
			{
				try {
				    Sequence mySeq = MidiSystem.getSequence(MidiTestFile);
				    PianoSequencer.seq.setSequence(mySeq);
				} catch (Exception e1) {
	
				}
				
				PianoSequencer.seq.start();
			}
			
		}
		
	}
	
	public static void out(String s)
	{
		textArea.setText(textArea.getText()+"\n"+s);
	}
    
	 
	    
	 public void listDevices(boolean bForInput, boolean bForOutput)
	 {
	    	listDevices(bForInput, bForOutput, true);
	 }
    
    public void listDevices(boolean bForInput,
		      boolean bForOutput,
		      boolean bAllinfo)
    {
    	if (bForInput && !bForOutput)
    	{
    		out("Available MIDI IN Devices:");
    	}
    	else if (!bForInput && bForOutput)
    	{
    		out("Available MIDI OUT Devices:");
    	}
    	else
    	{
    		out("Available MIDI Devices:");
    	}

    	MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
    	for (int i = 0; i < aInfos.length; i++)
    	{
    		try
    		{
    			MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
    			boolean		bAllowsInput = (device.getMaxTransmitters() != 0);
    			boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
    			if ((bAllowsInput && bForInput) ||
    					(bAllowsOutput && bForOutput))
    			{
    				if (bAllinfo)
    				{
    					out("" + i + "  "
    							+ (bAllowsInput?"IN ":"   ")
    							+ (bAllowsOutput?"OUT ":"    ")
    							+ aInfos[i].getName() + ", "
    							+ aInfos[i].getVendor() + ", "
    							+ aInfos[i].getVersion() + ", "
    							+ aInfos[i].getDescription());
    				}
    				else
    				{
    					out("" + i + "  " + aInfos[i].getName());
    				}
    			}
    		}
    		catch (MidiUnavailableException e)
    		{
    		}
    	}
    	if (aInfos.length == 0)
    	{
    		out("[No devices available]");
    	}
    }
    
    public static String[] getMidiPorts(boolean bIns)
	{
		List<String> lst = new LinkedList<String>();
		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			try
			{
				MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
				boolean		bAllowsInput = (device.getMaxTransmitters() != 0);
				boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
				if (bAllowsInput && bIns && !bAllowsOutput) {
					lst.add(aInfos[i].getName());
				}
				else if (bAllowsOutput && !bIns && !bAllowsInput)
					lst.add(aInfos[i].getName());
				
				out("" + i + "  " + (bAllowsInput?"IN ":"   ") + 
						(bAllowsOutput?"OUT ":"    ")+ aInfos[i].getName());
			}
			catch (MidiUnavailableException e)
			{
				out("device " + aInfos[i].getName() + " is unavailable");
			}
		}
		String[] results = new String[lst.size()];
		for (int i = 0; i<results.length;i++)
			results[i]=lst.get(i);
		return results;
	}
    
    
    
    public static String[] getMidiChans() {
    	List<String> lst = new LinkedList<String>();
    	MidiChannel channels[] = PianoSequencer.synth.getChannels ();
    	for (int i = 0; i < channels.length; i++)
		{
			lst.add(channels[i].toString());
			//System.out.println("" + i + ": " + channels[i].toString());
		}
    	String[] results = new String[lst.size()];
		for (int i = 0; i<results.length;i++)
			results[i]="" + i + "";
		return results;
	}
    
    
    
    public static String[] getMidiInstr()
	{
		List<String> lst = new LinkedList<String>();
		Instrument [] instr = PianoSequencer.synth.getLoadedInstruments ();
		for (int i = 0; i < instr.length; i++)
		{
			lst.add(instr[i].getName());
			//System.out.println("" + i + ": " + instr[i].getName());
		}
		String[] results = new String[lst.size()];
		for (int i = 0; i<results.length;i++)
			results[i]=lst.get(i);
		return results;
	}
    
    public static MidiDevice.Info getMidiDeviceInfo(String strDeviceName, boolean bForOutput)
	{
		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			if (aInfos[i].getName().equals(strDeviceName))
			{
				try
				{
					MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
					boolean	bAllowsInput = (device.getMaxTransmitters() != 0);
					boolean	bAllowsOutput = (device.getMaxReceivers() != 0);
					if ((bAllowsOutput && bForOutput) || (bAllowsInput && !bForOutput))
					{
						return aInfos[i];
					}
				}
				catch (MidiUnavailableException e)
				{
					// TODO:
				}
			}
		}
		return null;
	}
    
    void connectPorts(){
		MidiDevice.Info mInInfo = getMidiDeviceInfo((String)midiIns.getSelectedItem(), false);
		MidiDevice.Info mOutInfo = getMidiDeviceInfo((String)midiOuts.getSelectedItem(), true);
		try {
			if (mInInfo != null)
				inPort = MidiSystem.getMidiDevice(mInInfo);
			if (mOutInfo != null)
				outPort = MidiSystem.getMidiDevice(mOutInfo);
			
			Transmitter tIn;
			if(mInInfo != null)
			{
				if (!inPort.isOpen()){out("input port "+mInInfo+" closed, but I open it"); inPort.open();}
				tIn = inPort.getTransmitter();
			}
			else
			{
				out("No IN device");
				tIn = null;
			}
				

			if (!outPort.isOpen()){out("output port "+mOutInfo+" closed, but I open it"); outPort.open();}
			Receiver rOut=outPort.getReceiver();
			
		
			if(tIn != null)
				tIn.setReceiver(recTrans);
			out(":: connected to receiver: "+recTrans.toString());
			
			if(mInInfo != null)
				out("Connecting " + mInInfo + " to "+ mOutInfo);
			

		} catch (MidiUnavailableException e) {
			out("unavailable device: "+mInInfo.getName());
		}
	}
    
    //ShutDownHook to ensure MIDI resources are freed upon exit
    public void attachShutDownHook(){
  	  Runtime.getRuntime().addShutdownHook(new Thread() {
  	   @Override
  	   public void run() {
  	    PianoSequencer.seq.close();
  	    PianoSequencer.synth.close();
  	   }
  	  });
  	 }
    
    public static void main(String[] args) throws MidiUnavailableException
    {
      
    	MidiSuite MSframe = new MidiSuite();
    	MSframe.attachShutDownHook();
        
    }
    
}