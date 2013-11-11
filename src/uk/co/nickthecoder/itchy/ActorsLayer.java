/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;

import uk.co.nickthecoder.jame.Rect;

public interface ActorsLayer extends Layer
{

    public boolean isLocked();

    public boolean getYAxisPointsDown();
    
    public Rect getAbsolutePosition();
    
    public Iterator<Actor> iterator();

    public void add( Actor actor );

    public boolean remove( Actor actor );

    public void addBottom( Actor actor );

    public void addTop( Actor actor );

    public void addBelow( Actor actor, Actor other );

    public void addAbove( Actor actor, Actor other );

    @Override
    public void clear();

    public void zOrderUp( Actor actor );

    public void zOrderDown( Actor actor );

    public void enableMouseListener( Game game );

    public void disableMouseListener( Game game );

    public void captureMouse( MouseListener owner );

    public void releaseMouse( MouseListener owner );

}
