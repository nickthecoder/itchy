package uk.co.nickthecoder.itchy.remote;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.SoundManager;

public class RemoteSoundManager implements SoundManager
{
    private Server server;
    
    public RemoteSoundManager( Server server )
    {
        System.out.println("Created RemoteSoundManager");
        this.server = server;
    }
    
    @Override
    public void tick()
    {
    }

    @Override
    public void play(Actor actor, String eventName, ManagedSound ms)
    {
        // System.out.println( "Sending sound event " + eventName );
        server.send( "playSound", actor.getCostume().getName(), eventName );
    }

    @Override
    public void end(Actor actor, String eventName)
    {
        server.send( "stopSound", eventName );
    }

    @Override
    public void stopAll()
    {
        server.send( "stopSounds" );
    }

}
