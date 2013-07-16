/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
