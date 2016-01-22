/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridLayout implements ContainerLayout
{
    private final Container container;

    private final int columnCount;

    private int[] widthsInc;

    private final List<Component[]> rows;

    private Component[] currentRow;

    private int currentIndex;

    private Set<GridLayout> group;

    public GridLayout( PlainContainer container, int columns )
    {
        container.addStyle("grid");
        this.rows = new ArrayList<Component[]>();
        this.widthsInc = new int[columns];

        this.container = container;
        this.columnCount = columns;
        this.group = new HashSet<GridLayout>();
        this.group.add(this);
        container.setLayout(this);
        container.setYAlignment(0.5);
    }

    /**
     * Groups a GridLayout to another GridLayout such that their columns line up. The number of columns must be the same in both, and this
     * GridLayout must not already be grouped.
     * <p>
     * More than 2 GridLayouts can be grouped together, just group a third one to either of the other two (not both!)
     * 
     * @param other
     */
    public void groupWith( GridLayout other )
    {
        if (other.columnCount != this.columnCount) {
            throw new RuntimeException("Column counts differ");
        }

        if (this.group.size() != 1) {
            throw new RuntimeException("Already part of an alignment group");
        }

        other.group.add(this);
        this.group = other.group;

        for (GridLayout member : this.group) {
            if (member.container != null) {
                member.container.forceLayout();
            }
        }

        this.container.setXSpacing(other.container.getXSpacing());
        this.container.setYSpacing(other.container.getYSpacing());
    }

    public void ungroup()
    {
        this.group = new HashSet<GridLayout>();
        this.group.add(this);
    }

    public void addRow( String a, Component b )
    {
        this.addRow(a == null ? new NullComponent() : new Label(a), b);
    }

    public void addRow( Component a, Component b )
    {
        if (a == null) {
            a = new NullComponent();
        }
        if (b == null) {
            b = new NullComponent();
        }
        Component[] row = new Component[] { a, b };
        this.addRow(row);
    }

    public void addRow( String a, Component b, Component c )
    {
        this.addRow(a == null ? new NullComponent() : new Label(a), b, c);
    }

    public void addRow( Component a, Component b, Component c )
    {
        if (a == null) {
            a = new NullComponent();
        }
        if (b == null) {
            b = new NullComponent();
        }
        if (c == null) {
            c = new NullComponent();
        }
        Component[] row = new Component[] { a, b, c };
        this.addRow(row);
    }

    public void addRow( String a, Component b, Component c, Component d )
    {
        this.addRow(a == null ? new NullComponent() : new Label(a), b, c, d);
    }

    public void addRow( Component a, Component b, Component c, Component d )
    {
        if (a == null) {
            a = new NullComponent();
        }
        if (b == null) {
            b = new NullComponent();
        }
        if (c == null) {
            c = new NullComponent();
        }
        if (d == null) {
            d = new NullComponent();
        }
        Component[] row = new Component[] { a, b, c, d };
        this.addRow(row);
    }

    public void addRow( Component[] row )
    {
        assert (this.columnCount == row.length);
        for (Component child : row) {
            this.container.addChild(child);
        }
        this.rows.add(row);
    }

    public void addChild( Component component )
    {
        if (this.currentRow == null) {
            this.currentRow = new AbstractComponent[this.columnCount];
            for (int i = 0; i < this.columnCount; i++) {
                this.currentRow[i] = new NullComponent();
            }
            this.currentIndex = 0;
        }
        this.currentRow[this.currentIndex] = component;
        this.currentIndex++;
        if (this.currentIndex >= this.columnCount) {
            this.endRow();
        }
    }

    public void endRow()
    {
        if (this.currentRow != null) {
            this.currentIndex = 0;
            this.addRow(this.currentRow);
            this.currentRow = null;
        }
    }

    public void clear()
    {
        this.container.clear();
        this.rows.clear();
    }

    /*
    * The current algorithm is inefficient for grouped GridLayouts, as it calculates the column widths for each GridLayouts
    * independently, even though it should be possible to calculate them only once.
    */
    @Override
    public void calculateRequirements( PlainContainer container )
    {
        int requiredWidthInc = 0;
        int requiredHeight = 0;

        for (int i = 0; i < this.columnCount; i++) {

            int maxWidthInc = 0;
            for (GridLayout grid : this.group) {
                for (Component[] row : grid.rows) {

                    Component component = row[i];
                    int widthInc = component.getRequiredWidth() + component.getMarginLeft() + component.getMarginRight();
                    if (widthInc > maxWidthInc) {
                        maxWidthInc = widthInc;
                    }
                }
            }

            this.widthsInc[i] = maxWidthInc;
            requiredWidthInc += maxWidthInc;

        }
        requiredWidthInc += container.getXSpacing() * (this.columnCount - 1);
        requiredWidthInc += container.getPaddingLeft() + container.getPaddingRight();

        container.setNaturalWidth(requiredWidthInc);

        for (Component[] row : this.rows) {

            int maxHeightInc = 0;
            for (Component component : row) {
                int heightInc = component.getRequiredHeight() + component.getMarginTop() + component.getMarginBottom();
                if (heightInc > maxHeightInc) {
                    maxHeightInc = heightInc;
                }
            }
            requiredHeight += maxHeightInc;
        }

        requiredHeight += container.getYSpacing() * (this.rows.size() - 1);
        requiredHeight += container.getPaddingTop() + container.getPaddingBottom();

        container.setNaturalHeight(requiredHeight);
    }

    @Override
    public void layout( PlainContainer container )
    {

        int y = container.getPaddingTop();

        for (Component[] row : this.rows) {
            int x = container.getPaddingLeft();

            int maxHeightInc = 0;
            for (Component component : row) {
                int heightInc = component.getRequiredHeight() + component.getMarginTop() + component.getMarginBottom();
                if (heightInc > maxHeightInc) {
                    maxHeightInc = heightInc;
                }
            }

            int i = 0;
            for (Component component : row) {

                int height = container.getFillY() ? maxHeightInc - component.getMarginTop() - component.getMarginBottom() : component
                    .getRequiredHeight();

                int width = container.getFillX() ? this.widthsInc[i] - component.getMarginLeft() - component.getMarginRight() : component
                    .getRequiredWidth();

                int yDiff = maxHeightInc - (height + component.getMarginTop() + component.getMarginBottom());
                int extraY = (int) (yDiff * container.getYAlignment());
                component.setPosition(x + component.getMarginLeft(), y + component.getMarginTop() + extraY, width, height);

                x += this.widthsInc[i] + container.getXSpacing();
                i++;
            }
            y += maxHeightInc + container.getYSpacing();

        }
    }

}
