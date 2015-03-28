import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 *	This file is part of DiseaseSim version 0.3 -  an agent based modeling research tool	*
 *	Copyright (C) 2012 Marek Laskowski				*
 *											*
 *	This program is free software: you can redistribute it and/or modify		*
 *	it under the terms of the GNU General Public License as published by		*
 *	the Free Software Foundation, either version 3 of the License, or		*
 *	(at your option) any later version.						*
 *											*
 *	This program is distributed in the hope that it will be useful,			*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of			*
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			*
 *	GNU General Public License for more details.					*
 *											*
 *	You should have received a copy of the GNU General Public License		*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.		*
 *											*
 *	email: mareklaskowski@gmail.com							*
 ****************************************************************************************/
/**
 * 
 * a class that implements a GUI for simulating vector borne disease spread
 */

public class DiseaseGUI extends JPanel implements ActionListener{
	private static final long serialVersionUID = 8832885560545657000L;
	
	private static Canvas canvas;
	
	private static ActionListener listener = null;
	private static Timer displayTimer = null;
	
	public static World theWorld;
	
	private JButton start = new JButton("START");
	private JButton stop = new JButton("STOP");
	private JButton reset = new JButton("RESET");
	private JTextArea delay = new JTextArea();
	
	/**
	 * default constructor
	 */
	public DiseaseGUI(){
		this.setLayout(new GridBagLayout());
		theWorld = new World(50, 50, 3600.0, 0.5, 2, new Rectangle(0,0,800,800));
		//add one or more infected agents
		Environment groundZero = World.getRandomLocation();
		Disease newInfection = new Disease("ACGT");
		Human patientZero = new Human(groundZero.getRow(), groundZero.getColumn());
		patientZero.recieveDisease(newInfection.getStrain());
		groundZero.enter(patientZero);

		//making a smaller window for now
		theWorld.setPreferredSize(new Dimension(800,800));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 0.9;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		add(theWorld, c );
		
		c.weighty = 0;
		c.weightx = 0.1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		add(start, c);
		start.setActionCommand("start");
		start.addActionListener(this);
		
		c.gridy = 1;
		add(stop, c);
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				displayTimer.stop();
				// advance the time'
				theWorld.tick();
				int infectionCount = theWorld.countInfections();
				if (infectionCount <= 0) {
					displayTimer.stop();
					System.out.println("Simulation finished");
				} else {
					theWorld.repaint();
					System.out.println("time: " + World.getTime().getTime()
							+ "number of infections: " + infectionCount);
					displayTimer.restart();
				}

			}
		};
		displayTimer = new Timer(1000, listener);
		displayTimer.start();
		c.gridy = 2;
		add(reset, c);
		reset.setActionCommand("reset");
		reset.addActionListener(this);

	}
	
	@Override
	/**
	 * a handler method for any gui events.. not currently used
	 */
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand() == "any")
		{
			System.out.println("something happened!");
		}
		if (e.getActionCommand() == "start") {
			displayTimer.start();
			System.out.println("Simulation starting/re-starting");
		}
		if (e.getActionCommand().equals("stop")) {
			
			displayTimer.stop();
			System.out.println("Simluation Stopping");
		}
		if (e.getActionCommand().equals("reset")) {
			System.out.println("Resetting Simulation");
		}
	}
	
	/**
	 * your typical main function
	 * @param args currently ignored
	 */
	public static void main(String[] args) {
		
		/* Use an appropriate Look and Feel */
        try {
           
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Vector Disease Model");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Create and set up the content pane.
        JComponent newContentPane = new DiseaseGUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
