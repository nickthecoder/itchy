/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public interface SequenceOrParallel extends Cloneable
{
    public void start( Actor actor );

    public void startExceptFirst( Actor actor );

    public boolean tick( Actor actor );

    public boolean isFinished();

    public abstract SequenceOrParallel copy();
}
