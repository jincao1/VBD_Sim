import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

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


public class Mosquito extends Agent {
	
	public static final double lifespan_max = 28.0*3600*24;
	public static final double encounter_survival_rate = 0.5;
	double age;
	public static final String type = "MOSQUITO";
	//TODO: better functional description of biting behavior
	public static final double bite_rate = 0.01;
	
	public Mosquito(int currRow, int currCol){
		super(type, currRow, currCol);
		//TODO: determine age at time of instantiation
		age = Math.random() * lifespan_max;
	}
	/*
	 * @param deltaTime in seconds
	 */
	@Override
	public void tick(double deltaTime)
	{
		super.tick(deltaTime);
		//TODO: lay eggs? do we care?
		//TODO: disease progress!
		//for now infectious until death, so no change in state
		//TODO: more realistic function to die
		age+=deltaTime;
		if(age > lifespan_max)
		{
			die();
		} else if(World.getLocation(getRow(), getColumn()).hasAny(Human.type)){
			//possibly bite someone
			if(Math.random() < bite_rate){
				//choose a random human
				Vector<Agent> humans = World.getLocation(getRow(), getColumn()).getAll(Human.type);
				int random_idx = (int)(Math.random() * humans.size());
				if(humans.get(random_idx) instanceof Human){
					Human victim = (Human)humans.get(random_idx);
					victim.recieveBite(this);
					if(Math.random() > encounter_survival_rate)
					{
						System.out.println("MOSQUITO "+getUID() + " was killed while feeding");
						die();
					}
				}
			}
		}else{
			//no humans present
			moveTo(levyFlight(getRow()),levyFlight(getColumn()));
		}
		
	}
	@Override
	public void draw(Graphics g, Rectangle drawArea) {
		g.setColor(new Color(0, 0, 0));
		g.fillRect(drawArea.x, drawArea.y, 3, 3);
	}
}
