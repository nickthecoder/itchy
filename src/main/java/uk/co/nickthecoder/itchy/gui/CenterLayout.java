/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class CenterLayout implements ContainerLayout
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
        int midX = container.getWidth() / 2;
        int midY = container.getHeight() / 2;

        for (Component child : container.getChildren()) {

            int width = child.getRequiredWidth();
            int height = child.getRequiredHeight();

            child.setPosition(midX - width / 2, midY - height / 2, width, height);

        }
    }

}
