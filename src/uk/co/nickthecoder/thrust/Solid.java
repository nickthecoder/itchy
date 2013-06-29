package uk.co.nickthecoder.thrust;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Solid extends Behaviour {

	@Override
	public void init()
	{
		this.actor.addTag("solid");
	}
	
	@Override
	public void tick()
	{
		this.actor.deactivate();
	}
	
	public void hit( Actor other )
	{	
	}
}
