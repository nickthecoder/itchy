package uk.co.nickthecoder.itchy.util;

import java.text.NumberFormat;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.TextPose;

public class FPSBehaviour
	extends Behaviour
{
	public NumberFormat format;
	
	public FPSBehaviour()
	{
		format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(1);
		format.setMinimumFractionDigits(1);
	}
	
	public static Actor createActor( Font font, int size )
	{
		TextPose tp = new TextPose( "0.0", font, size );
		Actor actor = new Actor(tp );
		actor.setBehaviour( new FPSBehaviour() );
		return actor;
	}
	
	@Override
	public void tick()
	{
		double fps = Itchy.singleton.getFrameRate();
		String str = format.format( fps );
		TextPose textPose = (TextPose) (getActor().getAppearance().getPose());
		textPose.setText( str );
	}

}
