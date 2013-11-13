/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.MouseEvent;

public interface ScrollableView extends View
{
    public void ceterOn( Actor actor );

    public void centerOn( double x, double y );

    public void scrollTo( double x, double y );

    public void scrollBy( double dx, double dy );

    public void adjustMouse( MouseEvent event );

}
