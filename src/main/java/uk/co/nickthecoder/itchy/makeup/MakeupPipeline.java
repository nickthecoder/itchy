/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.property.Property;

public class MakeupPipeline implements Makeup
{
    public List<Makeup> pipeline = new LinkedList<Makeup>();

    private int changeId = 0;

    @Override
    public List<Property<Makeup, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        OffsetSurface result = src;

        for (Makeup child : this.pipeline) {
            result = child.apply(src);

            if ((!src.isShared()) && (src.getSurface() != result.getSurface())) {
                src.getSurface().free();
            }

            src = result;
        }

        return result;
    }

    public void add( Makeup makeup )
    {
        this.pipeline.add(makeup);
        this.changeId += 1;
    }

    public void remove( Makeup makeup )
    {
        if (this.pipeline.remove(makeup)) {
            // We don't want the id to go down ever, so when we remove an item, the final tally from
            // this.getChangeId() is greater than before.
            this.changeId += makeup.getChangeId() + 1;
        }
    }

    @Override
    public int getChangeId()
    {
        int id = this.changeId;
        for (Makeup child : this.pipeline) {
            id += child.getChangeId();
        }
        return id;
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        for (Makeup child : this.pipeline) {
            child.applyGeometry(src);
        }
    }

}
