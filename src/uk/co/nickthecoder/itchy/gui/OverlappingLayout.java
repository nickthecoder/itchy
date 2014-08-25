/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class OverlappingLayout implements Layout
{
    @Override
    public void calculateRequirements( PlainContainer container )
    {
        int width = 0;
        int height = 0;

        List<Component> children = container.getChildren();

        for (Component child : children) {
            if (width < child.getRequiredWidth()) {
                width = child.getRequiredWidth();
            }
            if (height < child.getRequiredHeight()) {
                height = child.getRequiredHeight();
            }
        }

        container.setNaturalWidth(width + container.getPaddingLeft() + container.getPaddingRight());
        container.setNaturalHeight(height + container.getPaddingTop() + container.getPaddingBottom());

    }

    @Override
    public void layout( PlainContainer container )
    {
        List<Component> children = container.getChildren();

        int fullWidth = container.getWidth() - container.getPaddingLeft() - container.getPaddingRight();
        int fullHeight = container.getHeight() - container.getPaddingTop() - container.getPaddingBottom();

        for (Component child : children) {
            int width = fullWidth;
            int height = fullHeight;

            if ((!container.getFillX()) && (child.getRequiredWidth() < fullWidth)) {
                width = child.getRequiredWidth();
            }
            if ((!container.getFillY()) && (child.getRequiredHeight() < fullHeight)) {
                height = child.getRequiredHeight();
            }

            child.setPosition(container.getPaddingLeft(), container.getPaddingTop(), width, height);
        }

    }

}
