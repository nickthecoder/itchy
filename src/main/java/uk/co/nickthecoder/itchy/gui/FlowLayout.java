/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class FlowLayout implements ContainerLayout
{
    @Override
    public void calculateRequirements( PlainContainer container )
    {
        int accumulatedWidth = container.getPaddingLeft() + container.getPaddingRight();
        int maxHeight = 0;

        List<Component> children = container.getChildren();

        for (Component child : children) {
            if (child.isVisible()) {
                accumulatedWidth += child.getRequiredWidth() + container.getXSpacing();
                int tempHeight = child.getRequiredHeight() + child.getMarginTop() + child.getMarginBottom();
                if (tempHeight > maxHeight) {
                    maxHeight = tempHeight;
                }
            }
        }
        container.setNaturalWidth(accumulatedWidth);
        container.setNaturalHeight(maxHeight + container.getPaddingTop() + container.getPaddingBottom());        
    }

    @Override
    public void layout( PlainContainer container )
    {
        int left = container.getPaddingLeft();
        int right = container.getWidth() - container.getPaddingRight();

        int x = left;
        int y = container.getPaddingTop();

        int maxHeight = 0;

        List<Component> children = container.getChildren();

        for (Component child : children) {
            int width = child.getRequiredWidth();
            int height = child.getRequiredHeight();

            if ((x != left) && (x + width > right)) {
                x = left;
                y += container.getYSpacing() + maxHeight;
                maxHeight = 0;
            }

            child.setPosition(x, y, width, height);
            x += width + container.getXSpacing();

            if (height > maxHeight) {
                maxHeight = height;
            }
        }

        container.setNaturalHeight(y + maxHeight + container.getPaddingBottom());
    }

}
