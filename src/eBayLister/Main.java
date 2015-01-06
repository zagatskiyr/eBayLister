package eBayLister;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.*;

public class Main implements Runnable {
	
	// frame objects
	final static JFrame frame = new JFrame("TOP LEVEL FRAME");
	final TreeMap<String, JTextField> tfMap = new TreeMap<String, JTextField>(); // key -> textfield
	final JPanel keyPane = new JPanel();
	final JPanel labelPane = new JPanel();
	
	final JTextArea codeField = new JTextArea();
	// reader
	static BufferedReader br;
	
	// content objects
	static ArrayList<String> content;
	static ArrayList<String> keys;
	static ArrayList<String> labels;
	int[] contentKeyMap = new int[0];
	
	public Main(){
		// read in html content and keys
		try {
			content = readText("ebaytxt");
			keys = readText("ebaykeys");
			labels = readText("ebaylabels");
		} catch (IOException e) {
			System.out.println("some error occured");
		}
		
		// set up blank textfields that map to keys
		for (int x = 0; x < keys.size(); x++) {
			tfMap.put(keys.get(x), new JTextField());
		}
		
		// set up the code
		codeField.setRows(content.size());
		setCode();
		
		// determine the location of keys in content
		contentKeyMap = new int[content.size()];
		for (int x = 0; x < keys.size(); x++) {
			contentKeyMap[x] = content.indexOf(keys.get(x));
		}
	}
	
	// update html code
	public void setCode() {
		
		codeField.setText("");
		for (int x = 0; x < content.size(); x++) {
			codeField.append(content.get(x));
			codeField.append("\n");
		}
	}
	
	
	// method for reading in contents of a text file
	public ArrayList<String> readText(String filename) throws IOException {
		// read in the target text
		ArrayList<String> s = new ArrayList<String>();
		try {
			//open the file
			br = new BufferedReader (new FileReader(filename));
			
			//read in the text
			String nextElt = "";
			while (br.ready()){
				// Make sure you aren't adding blanks
				nextElt = br.readLine().trim().toLowerCase();
				boolean isEmpty = nextElt.isEmpty();
				if (!isEmpty && nextElt != null) s.add(nextElt);
			}
			
			//close reader
			br.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			throw new FileNotFoundException("");
		} catch (IOException ex){
			System.out.println("error reading line");
		}
		
		return s;
	}
	
	// update string that is going to be copied
	// with entries in the text field
	public void updateContents() {
		for (int x = 0; x < keys.size(); x++) {
			String tfText = tfMap.get(keys.get(x)).getText();
			//System.out.println(tfText);
			content.set(contentKeyMap[x], tfText);
		}
		
		setCode();
	}
	
	public void run() {
		// build the entry and label areas
		GridLayout oneColGrid = new GridLayout(0,1);
		GridLayout threeColGrid = new GridLayout(0,3);
		
		keyPane.setLayout(oneColGrid);
		labelPane.setLayout(oneColGrid);
		
		// add fields to the panes
		for (int x = 0; x < keys.size(); x++) {
			keyPane.add(tfMap.get(keys.get(x)));
			labelPane.add(new JLabel(labels.get(x)));
		}
		
		// add button to update fields
		JButton updateButton = new JButton("Update & Copy Code");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				updateContents();
				
				StringSelection stringSelection = new StringSelection (codeField.getText());
				System.out.println(stringSelection);
		        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		        clpbrd.setContents (stringSelection, null);
				}
		});

		
		// combine the label and entry areas
		JPanel mainPane = new JPanel();
		JPanel masterPane = new JPanel();
		JScrollPane scrollPaneCode = new JScrollPane(codeField);
		
		mainPane.setLayout(threeColGrid);
		mainPane.add(labelPane);
		mainPane.add(keyPane);
		mainPane.add(scrollPaneCode);
		
		masterPane.setLayout(new BoxLayout(masterPane, BoxLayout.PAGE_AXIS));
		masterPane.add(updateButton);
		masterPane.add(mainPane);
			
		
		// Put the frame on the screen
		frame.add(masterPane);
		frame.setLocation(300, 300);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(500,500);
		//frame.setResizable(true);s
	}
	
	public static void main(String[] args) {
		//initialize the GUI
		SwingUtilities.invokeLater(new Main());
	}
}
