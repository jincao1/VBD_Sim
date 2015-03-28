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

import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JPanel;
/**
 * This is the base class for agents in the simulation 
 * has a unique ID (UID), maintains the current row and column in the environment lattice
 * where the agent is currently located, and a string describing the agent. 
 * An agent can have zero or more strains of a disease.
 *
 */
abstract class Agent extends JPanel implements MouseListener {
	private static long NEXT_UID = 1;
	/**
	 * gets the next UID
	 * @return a long integer representing the next UID (unique identifier) which
	 * will uniquely identify each agent in the system
	 */
	private static long getNextUID()
	{
		return NEXT_UID++;
	}
	
	protected Vector<Disease> infections = new Vector<Disease>();
	private int currRow;
	private int currCol;
	private String type = "BASIC_AGENT";
	private long UID = getNextUID();
	
	/**
	 * 
	 * @param type String representing what type of agent this is
	 * @param currRow this agent's current position
	 * @param currCol this agent's current position
	 */
	protected Agent(String type, int currRow, int currCol)
	{
		this.currRow = currRow;
		this.currCol = currCol;
		this.type = type;
	}

	/**
	 * 
	 * @return the strain currently infecting the agent; returns empty string if 
	 * this agent has never been infected
	 */
	public String getStrain() {
		String recoveredStrain = "";
		if(infections.size() == 0){
			return "";
		}
		else 
		{
			for(Disease disease : infections)
			{
				if(disease.getState() == Disease.State.INFECTED)
				{
					return disease.getStrain();
				}
				if(disease.getState() == Disease.State.RECOVERED)
				{
					return disease.getStrain();
				}
			}
		}
		return recoveredStrain;
	}
	/**
	 * 
	 * @return true if this agent is currently infected
	 */
	public boolean isInfected()
	{
		for(Disease disease : infections)
		{
			if(disease.getState() == Disease.State.INFECTED) return true;
		}
		return false;
	}
	/**
	 * 
	 * @return true if this agent was previously infected, now recovered
	 */
	public boolean isRecovered()
	{
		return (isInfected() == false && getStrain() != "");
	}
	/**
	 * 
	 * @return returns true if this agent has never been infected
	 */
	public boolean isSusceptible()
	{
		return getStrain() == "";
	}
	
	/**
	 * 
	 * @param newStrain infects the agent with the new strain
	 */
	public void recieveDisease(String newStrain)
	{
		boolean susceptible = true;
		for(Disease disease : infections)
		{
			if(disease.getStrain() == newStrain){
				susceptible = false;
			}
		}
		if(susceptible){
			Disease tempDisease = new Disease(newStrain);
			tempDisease.setState(Disease.State.INFECTED);
			infections.add(tempDisease);
			System.out.println(this.getType()+ " " + this.getUID() +" became infectious");

		}
			

	}
	
	/**
	 * simulate the agent's behavior for this time period
	 * @param deltaTime time that the simulation has advanced
	 */
	public void tick(double deltaTime)
	{
		for(Disease disease : infections)
		{
			disease.tick(deltaTime);
		}
	}
	
/**
 * Will cause the agent to move to the new location before the next time tick() is called
 *  note: you should do this last during the Agent's tick() since the agent won't be at 
 *  the new location until the next tick()
 * @param row the destination row
 * @param col the destination column
 */
	protected void moveTo(int row, int col)
	{
		if(this.currCol != col || this.currRow != row){
			World.getLocation(currRow, currCol).exit(this);
			Environment temp = World.getLocation(row, col);
			temp.enter(this);
			this.currCol = temp.getColumn();
			this.currRow = temp.getRow();
		}
	}
	
	/**
	 * causes the agent to die
	 */
	protected void die()
	{
		World.getLocation(currRow, currCol).exit(this);
		System.out.println("agent" + getUID() + " " + getType() + " died");
	}
	/**
	 * 
	 * @return a string representing the type of agent this is
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * 
	 * @return this agent's UID
	 */
	public long getUID()
	{
		return UID;
	}
	
	/**
	 * 
	 * @return the agent's current row in the lattice environment
	 */
	public int getRow()
	{
		return currRow;
	}
	/**
	 * 
	 * @return the agent's current column in the lattice environment
	 */
	public int getColumn()
	{
		return currCol;
	}
	
	/**
	 * stochastically jump to a (probably) nearby location
	 * google wikipedia levy flight
	 * @param startingPosition the agent's current row or column
	 * @return the agent's new row or column
	 */
	public static int levyFlight(int startingPosition)
	{
		int count = 0;
		int displacement;
		double randomSample;
		double test = 0.5;
		//handle direction
		if(Math.random() < 0.5)
		{
			displacement = 1;
		}
		else
		{
			displacement = -1;
		}
		randomSample = Math.random();
		while(randomSample < test)
		{
			count++;
			test = test /2;
		}
		return count*displacement + startingPosition;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
