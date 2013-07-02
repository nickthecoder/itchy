package uk.co.nickthecoder.itchy;

public abstract class ActorTask extends Task
{
    protected Actor actor;

    public ActorTask( Actor actor )
    {
        this.actor = actor;
    }

    @Override
    public boolean getAbort()
    {
        return this.actor.isDead();
    }

    @Override
    public abstract void run();

}
