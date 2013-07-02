package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.util.Job;
import uk.co.nickthecoder.itchy.util.SkipMessage;

/**
 * Organises the game loop into a series of small tasks.
 * 
 * When writing the behaviour for a game's actors, it can be much easier to have a sleep method,
 * which will suspend the current thread. However, concurrent programming can be very difficult, and
 * itchy is designed to be useful for new programmers. This class allows behaviours to sleep, and
 * yet keep the game code running sequentially. It does this by breaking the game loop into a list
 * of small tasks. A thread starts at the front of the list, and runs that task. If that task ends
 * normally, it goes onto the next task. However, if the task chooses to sleep, then a new thread is
 * created, which iterates over the task list where the previous thread left off.
 * 
 * The old thread will not process any more tasks when it wakes up from the sleep. This is achieved
 * by keeping track of the "main thread" - i.e. the one thread that is responsible for iterating
 * over the remaining tasks. When sleep is called the main thread set to the newly created thead.
 */

class GameLoopJob extends Job
{
    /**
     * The list of active actors, whose tick method needs to be called.
     */
    private List<Actor> actors = new ArrayList<Actor>();

    /**
     * The next index of 'actors' array. -1 if the actors array has already been done.
     */
    private int actorsIndex;

    /**
     * The list of additional tasks to call.
     */
    private List<Task> tasks = new ArrayList<Task>();

    /**
     * The index into 'tasks'.
     */
    private int tasksIndex;

    public GameLoopJob()
    {
    }

    @Override
    public void start()
    {
        lock();
        try {
            this.actors.addAll(Actor.allByTag("active"));

            // this.tasks.add( Itchy.singleton.getGame() );

            this.actorsIndex = 0;
            this.tasksIndex = 0;
        } finally {
            unlock();
        }
        super.start();
    }

    public void add( Task task )
    {
        this.tasks.add(task);
    }

    public boolean finished()
    {
        //System.out.println( "Tasks  " + this.tasksIndex + " vs " + this.tasks.size() );
        //System.out.println( "Actors " + this.actorsIndex + " vs " + this.actors.size() );
        
        return (this.tasksIndex >= this.tasks.size()) && ((this.actorsIndex >= this.actors.size()));
    }

    @Override
    public boolean doTask()
    {

        if (this.tasksIndex < this.tasks.size()) {

            Task task = this.tasks.get(this.tasksIndex);
            this.tasksIndex++;

            if (!task.getSkip()) {
                try {
                    task.run();
                    if (task.getSkip()) {
                        task.setSkip(false);
                    }
                } catch (SkipMessage e) {
                    // Do nothing.
                }
            }

        } else {

            if (this.actorsIndex >= this.actors.size()) {
                // Finished!
                if ( this.actorsIndex != this.actors.size() )
                {
                    throw new RuntimeException( "actors not empty" );
                }
                if ( this.tasksIndex != this.tasks.size() )
                {
                    throw new RuntimeException( "actors not empty" );
                }
                this.actors.clear();
                this.tasks.clear();
                this.actorsIndex = 0;
                this.tasksIndex = 0;

                return false;
            }

            Actor actor = this.actors.get(this.actorsIndex);
            this.actorsIndex++;

            if (!actor.getSkip()) {
                try {
                    actor.tick();
                    if (actor.getSkip()) {
                        actor.setSkip(false);
                    }
                } catch (SkipMessage e) {
                    // Do nothing.
                }
            }

        }

        return true;
    }
}
