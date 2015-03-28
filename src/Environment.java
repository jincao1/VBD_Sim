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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JPanel;
/**
 * 
 * a class that represents one location as part of a lattice
 * holds a list of Agents that are at this location
 * and manages lists of agents entering and leaving
 * has an associated row, column, and mosquito density
 */
public class Environment extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	private Vector<Agent> inhabitants = new Vector<Agent>();
	private Vector<Agent> entrantList = new Vector<Agent>();
	private Vector<Agent> exitantList = new Vector<Agent>();
	private double mosquitoDensity;
	private int column;
	private int row;
	private Mosquito temp_mosquito;
	
	//width and height of this environment
	private int width;
	private int height; 
	/**
	 * basic constructor
	 * @param row the agent's initial row
	 * @param col the agent's initial column
	 * @param mosquitoDensity the mosquito density at this location
	 * @param width the width of the environment
	 * @param height the height of the environment
	 */
	public Environment(int row, int col, double mosquitoDensity, int width, int height){
		this.mosquitoDensity = mosquitoDensity;
		this.row = row;
		this.column = col;
		this.width = width;
		this.height = height;
		//make this component the size of the rectangle size it's given
		this.setSize(width, height);
		temp_mosquito = new Mosquito(row,col);
		this.addMouseListener(this);
	}
	
	/**
	 * buffer Agents that will enter this location
	 * @param e the agent entering
	 */
	public void enter(Agent e){
		entrantList.add(e);
	}
	
	/**
	 * buffer agents leaving this location
	 * @param e the agent leaving the location
	 */
	public void exit(Agent e)
	{
		exitantList.add(e);
	}
	/**
	 * call when it's time for the agents entering this location to be processed
	 */
	public void doEntrances()
	{
		//purge entrantList
		for(Agent agent : entrantList)
		{
			inhabitants.add(agent);
		}
		entrantList.clear();
	}
	/**
	 * call when it's time for agents leaving this location to be processed
	 */
	public void doExits()
	{
		//purge exitantList
		
		for(Agent agent : exitantList)
		{
			inhabitants.remove(agent);
		}
		exitantList.clear();
	}
	
	/**
	 * simulate the agent's behavior for this time period
	 * @param deltaTime time that the simulation has advanced
	 */
	public void tick(double deltaTime)
	{
		
		//the mysterious for-each loop read: "for each agent in inhabitants"
		Vector<Agent> newMosquitoes = new Vector<Agent>();
		for(Agent agent : inhabitants)
		{
			if(agent instanceof Human)
			{
				Human victim = (Human)agent;
				//TODO: calibrate rate here - what is a reasonable bite rate?
				if(Math.random() < Mosquito.bite_rate)
				{
					victim.recieveBite(temp_mosquito);
					if(temp_mosquito.isInfected())
					{
						this.enter(temp_mosquito);
						//have another in reserve
						temp_mosquito = new Mosquito(row,column);
					}
				}
			}
			agent.tick(deltaTime);
		}
		
	}
	/**
	 * 
	 * @return this agent's row
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * 
	 * @return this agent's column
	 */
	public int getColumn()
	{
		return column;
	}
	
	/**
	 * check to see whether this location contains any Agents of a certain type
	 * @param type the type of agent to find
	 * @return true if this location has an Agent of the given type
	 */
	public boolean hasAny(String type) {

		for(Agent agent : inhabitants)
		{
			if(agent.getType() == type) return true;
		}
		return false;
	}
	/**
	 * returns a vector of Agents that match the given type
	 * @param type the type of Agents to return in the Vector
	 * @return a Vector of Agents of the given type
	 */
	public Vector<Agent> getAll(String type)
	{
		Vector<Agent> temp = new Vector<Agent>();
		for(Agent agent : inhabitants)
		{
			if(agent.getType() == type)
			{
				temp.add(agent);
			}
		}
		return temp;
	}
	/**
	 * 
	 * @return the number of infected agents at this location
	 */
	public int countInfections()
	{
		int count = 0;
		for(Agent agent : inhabitants)
		{
			if(agent.isInfected() == true)
			{
				count++;
			}
		}
		return count;
	}
	/**
	 * 
	 * @return count the number of susceptible agents at this location
	 */
	public int countSusceptible()
	{
		int count = 0;
		for(Agent agent : inhabitants)
		{
			if(agent.isSusceptible())
			{
				count++;
			}
		}
		return count;
	}
	/**
	 * 
	 * @return the number of recovered agents
	 */
	public int countRecovered()
	{
		int count = 0;
		for(Agent agent : inhabitants)
		{
			if(agent.isRecovered() )
			{
				count++;
			}
		}
		return count;
	}
	/**
	 * draw this lattice location within the given render area on the provided graphics context
	 * @param g the graphics context
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// call updateInhabitants
		updateInhabitants();
		//check if any infections
		if(countInfections() > 0)
		{
			g.setColor(Color.red);
			g.fillRect(0,0,width, height);
		//then check if any susceptible
		}else if(countSusceptible() > 0)
		{
			g.setColor(Color.green);
			g.fillRect(0,0, width, height);
		//then check for any recovered
		}else if(countRecovered() > 0)
		{
			g.setColor(Color.blue);
			g.fillRect(0,0,width,height);
		}//otherwise set to white
		else
		{
			if (World.getTime().get(Calendar.HOUR_OF_DAY) < 8) {
				// gray environment for night
				g.setColor(Color.GRAY);
			} else {
				g.setColor(Color.white);
			}
			g.fillRect(0,0,width,height);
		}
		// TODO fix this for paintComponents
	}

	/**
	 * updates the inhabitants in each environment
	 */
	public void updateInhabitants() {
		this.removeAll(); //remove previous inhabitants
		int x = 0;
		int y = 0;
		for (Agent a : inhabitants) {
			this.add(a);
			a.setLocation(x,y);
			if (x < getWidth() ) {
				x += 4;
			} else if (y < getHeight()) {
				y += 4;
				x = 0;
			}
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
}
