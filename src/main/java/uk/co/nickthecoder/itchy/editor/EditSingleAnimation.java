/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Animation;

/**
 * Edits the details of a single animation, such as MoveAnimation, AlphaAnimation etc.
 * See EditAnimation for editing the tree structure of a compound animation.
 */
public class EditSingleAnimation extends EditSubject<Animation>
{
    public EditSingleAnimation(Resources resources, Animation subject)
    {
        super(resources, null, subject, false);
    }

    @Override
    protected String getSubjectName()
    {
        return "Animation";
    }

    @Override
    protected void add()
    {
    }
}
