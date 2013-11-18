/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class VerticalLayout implements Layout
{
    private double sumExpansion;

    private int requiredSum;

    private int maxWidth;

    public VerticalLayout()
    {
    }

    @Override
    public void calculateRequirements( Container container )
    {
        this.requiredSum = 0;
        this.sumExpansion = 0;
        this.maxWidth = 0;

        List<Component> children = container.getChildren();

        for (Component child : children) {
            if (child.isVisible()) {

                this.requiredSum += child.getRequiredHeight() + child.getMarginTop() +
                        child.getMarginBottom();
                this.sumExpansion += child.getExpansion();
                int tempWidth = child.getRequiredWidth() + child.getMarginLeft() +
                        child.getMarginRight();
                if (tempWidth > this.maxWidth) {
                    this.maxWidth = tempWidth;
                }
            }
        }

        this.requiredSum += container.getYSpacing() * (children.size() - 1);

        container.setNaturalHeight(this.requiredSum + container.getPaddingTop() +
                container.getPaddingBottom());
        container.setNaturalWidth(this.maxWidth + container.getPaddingLeft() +
                container.getPaddingRight());

    }

    @Override
    public void layout( Container container )
    {
        int y = container.getPaddingTop();

        int extraHeight = container.getHeight() - container.getNaturalHeight();

        if (!container.getFillY()) {
            y += extraHeight * container.getYAlignment();
            extraHeight = 0;
        }
        
        int xSpace = container.getWidth() - container.getPaddingLeft() - container.getPaddingRight();

        List<Component> children = container.getChildren();

        for (Component child : children) {

            if (child.isVisible()) {

                int width = container.getFillX() ?
                    xSpace - child.getMarginLeft() - child.getMarginRight() :
                    child.getRequiredWidth();

                double expansionRatio = (this.sumExpansion == 0) ? 
                    1.0 / children.size() :
                    child.getExpansion() / this.sumExpansion;
                    
                int singleExtraHeight = (int) (extraHeight * expansionRatio);
                int height = child.getRequiredHeight() + singleExtraHeight;

                int xDiff = xSpace - (width + child.getMarginLeft() + child.getMarginRight());
                
                int x = (int) (container.getXAlignment() * xDiff) + container.getPaddingLeft() + child.getMarginLeft();

                child.setPosition(x, y + child.getMarginTop(), width, height);

                y += child.getMarginTop() + height + child.getMarginBottom() + container.getYSpacing();

            }
        }

    }

}
