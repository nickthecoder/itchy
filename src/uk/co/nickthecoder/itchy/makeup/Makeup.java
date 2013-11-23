/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.OffsetSurface;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public interface Makeup extends PropertySubject<Makeup>
{
    /**
     * Transform the source surface, which may or may not change the offsets
     * @param src The source to be transformed.
     */
    public OffsetSurface apply( OffsetSurface src );
    
    /**
     * Updates the geometric that would occur if this makeup would actually be applied.
     * @param src
     * @return If the geometry is unchanged, then will be the save as the source.
     */
    public void applyGeometry( TransformationData src );
    
    public int getChangeId();
}
