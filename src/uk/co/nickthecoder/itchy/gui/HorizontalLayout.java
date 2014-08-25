/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class HorizontalLayout implements Layout
{
    private double sumExpansion;

    private int requiredSum;

    private int maxHeight;

    public HorizontalLayout()
    {
    }

    @Override
    public void calculateRequirements( PlainContainer container )
    {
        this.requiredSum = 0;
        this.sumExpansion = 0;
        this.maxHeight = 0;

        List<Component> children = container.getChildren();

        boolean first = true;
        for (Component child : children) {
            if (child.isVisible()) {

                this.requiredSum += child.getRequiredWidth() + child.getMarginLeft() + child.getMarginRight();
                this.sumExpansion += child.getExpansion();
                int tempHeight = child.getRequiredHeight() + child.getMarginTop() + child.getMarginBottom();
                if (tempHeight > this.maxHeight) {
                    this.maxHeight = tempHeight;
                }
                if (first) {
                    first = false;
                } else {
                    this.requiredSum += container.getXSpacing();
                }
            }
        }

        container.setNaturalWidth(this.requiredSum + container.getPaddingLeft() + container.getPaddingRight());
        container.setNaturalHeight(this.maxHeight + container.getPaddingTop() + container.getPaddingBottom());
    }

    @Override
    public void layout( PlainContainer container )
    {
        int x = container.getPaddingLeft();
        int spacing = container.getXSpacing();
        int extraWidth = container.getWidth() - container.getNaturalWidth();

        if (!container.getFillX()) {
            x += extraWidth * container.getXAlignment();
            extraWidth = 0;
        }
        int ySpace = container.getHeight() - container.getPaddingTop() - container.getPaddingBottom();

        List<Component> children = container.getChildren();

        for (Component child : children) {

            if (child.isVisible()) {

                int height = container.getFillY() ? container.getHeight() - container.getPaddingTop() - container.getPaddingBottom() -
                    child.getMarginTop() - child.getMarginBottom() : child.getRequiredHeight();

                double expansionRatio = (this.sumExpansion == 0) ? 1.0 / children.size() : child.getExpansion() / this.sumExpansion;

                int singleExtraWidth = (int) (extraWidth * expansionRatio);
                int width = child.getRequiredWidth() + singleExtraWidth;

                int ty = (int) (container.getYAlignment() * (ySpace - child.getRequiredHeight()) + container.getPaddingTop() + child
                    .getMarginTop());

                child.setPosition(x + child.getMarginLeft(), ty, width, height);

                x += child.getMarginLeft() + width + child.getMarginRight() + spacing;

            }
        }

    }

}
