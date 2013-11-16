/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.PlainBehaviour;
import uk.co.nickthecoder.itchy.util.Property;

/**
 * A behaviour, which can easily be found using {@link Actor#nearest(String)} or
 * {@link AbstractBehaviour#allByTag(String)}.
 */
public class Tagged extends PlainBehaviour
{
    @Property(label = "Tag")
    public String tag = "none";

    @Override
    public void onBirth()
    {
        addTag(this.tag);
    }
}
