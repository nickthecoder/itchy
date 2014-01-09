/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public interface Wrapped
{
    public void normalise( Actor actor );

    public int getTop();

    public int getRight();
    
    public int getBottom();
    
    public int getLeft();
    
    public int getWidth();
    
    public int getHeight();
    
    public boolean overlappingLeft( Actor actor );
    
    public boolean overlappingRight( Actor actor );
    
    public boolean overlappingBottom( Actor actor );
    
    public boolean overlappingTop( Actor actor );
}
