/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.property.AbstractProperty;

public class MakeupPipeline implements Makeup
{
    public List<Makeup> pipeline= new LinkedList<Makeup>();
    
    private int changeId = 0;
    
    @Override
    public List<AbstractProperty<Makeup, ?>> getProperties()
    {
        return Collections.emptyList();
    }

    @Override
    public OffsetSurface apply( OffsetSurface src )
    {
        /*
        // TODO Remove after testing
        if ( src instanceof Pose ) {
            Pose pose = (Pose) src;
            System.out.println( "Pose name : " + Itchy.getGame().resources.getPoseName(pose));
        }
        if ( src instanceof TextPose ) {
            TextPose pose = (TextPose) src;
            System.out.println( "Pose text : " + pose.getText());
        }
        */
        TransformationData data = new TransformationData();
        data.set(src.getSurface().getWidth(), src.getSurface().getHeight(), src.getOffsetX(), src.getOffsetY());
        
        OffsetSurface result = src;
        
        for ( Makeup child : pipeline) {
            // System.out.println( "Applying " + child.getClass().getName() );
            child.applyGeometry(data);
            // System.out.println( "Resulted width : " + data.width);
            
            result = child.apply(src);
            /*
            if ( data.width != result.getSurface().getWidth()) {
                System.out.println( "*** Wrong width " + data.width + ", " + result.getSurface().getWidth());
            }
            
            if ( data.height != result.getSurface().getHeight()) {
                System.out.println( "*** Wrong height " + data.height + ", " + result.getSurface().getHeight());
            }
            
            if ( data.offsetX != result.getOffsetX()) {
                System.out.println( "*** Wrong offsetX " + data.offsetX + ", " + result.getOffsetX());
            }
            
            if ( data.offsetY != result.getOffsetY()) {
                System.out.println( "*** Wrong offsetY " + data.offsetY + ", " + result.getOffsetY());
            }
            */
            if ((!src.isShared()) && (src.getSurface() != result.getSurface())) {
                src.getSurface().free();
            }
            
            src = result;
        }
        
        return result;
    }

    public void add(Makeup makeup)
    {
        this.pipeline.add(makeup);
        this.changeId += 1;
    }
    
    public void remove(Makeup makeup)
    {
        if (this.pipeline.remove(makeup) ) {
            // We don't want the id to go down ever, so when we remove an item, the final tally from 
            // this.getChangeId() is greater than before.
            this.changeId += makeup.getChangeId() + 1;
        }
    }
    
    @Override
    public int getChangeId()
    {
        int id = this.changeId;
        for ( Makeup child: pipeline) {
            id += child.getChangeId();
        }
        return id;
    }

    @Override
    public void applyGeometry( TransformationData src )
    {
        for ( Makeup child: pipeline) {
            child.applyGeometry(src);
        }
    }

}
