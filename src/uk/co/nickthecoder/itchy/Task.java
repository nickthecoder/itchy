package uk.co.nickthecoder.itchy;

public abstract class Task
{
    public void sleep( double seconds )
    {
        if (! Itchy.singleton.gameLoopJob.hasLock()) {
            throw new RuntimeException( "Haven't got the lock can't perform a delay" );
        }
        Itchy.singleton.gameLoopJob.sleep((long) (1000 * seconds));
        if (! Itchy.singleton.gameLoopJob.hasLock()) {
            throw new RuntimeException( "Haven't got the lock after a delay" );
        }
    }

    public abstract void run();


}
