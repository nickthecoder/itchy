/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.PlainRole;
import uk.co.nickthecoder.itchy.property.Property;

/**
 * A role, which can easily be found using {@link Actor#nearest(String)} or {@link AbstractRole#allByTag(String)}.
 */
public class Tagged extends PlainRole
{
    @Property(label = "Tag")
    public String tag = "none";

    @Override
    public void onBirth()
    {
        addTag(this.tag);
    }
}
