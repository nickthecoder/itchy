package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.AbstractTextPose;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class TextBehaviour extends Behaviour
{
    public AbstractTextPose textPose;

    public TextBehaviour( AbstractTextPose pose )
    {
        this.textPose = pose;
    }

    public void setText( String text )
    {
        this.textPose.setText(text);
    }

    @Override
    public void tick()
    {
    }

    public Actor createActor()
    {
        Actor actor = new Actor(this.textPose);
        actor.setBehaviour(this);
        
        return actor;
    }

}
