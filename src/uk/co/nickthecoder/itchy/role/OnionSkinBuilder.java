/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;

public class OnionSkinBuilder extends OnionSkin.AbstractOnionSkinBuilder<OnionSkin, OnionSkinBuilder>
{
    public OnionSkinBuilder( Actor actor )
    {
        companion = new OnionSkin( actor );
    }
    
    public OnionSkinBuilder getThis()
    {
        return this;
    }
}
