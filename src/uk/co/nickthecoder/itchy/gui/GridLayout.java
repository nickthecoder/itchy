/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

public class GridLayout implements Layout
{
    private final Container container;

    private final int columnCount;

    private final List<Component[]> rows;

    private Component[] currentRow;

    private int currentIndex;

    public GridLayout( Container container, int columns )
    {
        this.rows = new ArrayList<Component[]>();

        this.container = container;
        this.columnCount = columns;
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
            this.currentRow = new Component[this.columnCount];
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

    @Override
    public void calculateRequirements( Container container )
    {
        int requiredWidth = 0;
        int requiredHeight = 0;

        for (int i = 0; i < this.columnCount; i++) {

            int maxWidth = 0;
            for (Component[] row : this.rows) {
                Component component = row[i];
                int width = component.getRequiredWidth() + component.getMarginLeft() +
                        component.getMarginRight();
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            requiredWidth += maxWidth;

        }
        requiredWidth += container.getSpacing() * (this.columnCount - 1);
        requiredWidth += container.getPaddingLeft() + container.getPaddingRight();

        container.setNaturalWidth(requiredWidth);

        for (Component[] row : this.rows) {

            int maxHeight = 0;
            for (Component component : row) {
                int height = component.getRequiredHeight() + component.getMarginTop() +
                        component.getMarginBottom();
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
            requiredHeight += maxHeight;
        }

        requiredHeight += container.getSpacing() * (this.rows.size() - 1);
        requiredHeight += container.getPaddingTop() + container.getPaddingBottom();

        container.setNaturalHeight(requiredHeight);
    }

    @Override
    public void layout( Container container )
    {
        int[] widths = new int[this.columnCount];

        for (int i = 0; i < this.columnCount; i++) {

            int maxWidth = 0;
            for (Component[] row : this.rows) {
                Component component = row[i];
                int width = component.getRequiredWidth() + component.getMarginLeft() +
                        component.getMarginRight();
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            widths[i] = maxWidth;
        }

        int y = container.getPaddingTop();

        for (Component[] row : this.rows) {

            int x = container.getPaddingLeft();

            int maxHeight = 0;
            for (Component component : row) {
                int height = component.getRequiredHeight() + component.getMarginTop() + component.getMarginBottom();
                if ( height > maxHeight) {
                    maxHeight = height;
                }
            }
        
            int i = 0;
            for (Component component : row
                ) {
                int height = component.getRequiredHeight();
                int width = component.getRequiredWidth();

                int heightInc = height + component.getMarginTop() + component.getMarginBottom();
                if (heightInc > maxHeight) {
                    maxHeight = heightInc;
                }
                int centerY = maxHeight - (component.getRequiredHeight() + component.getMarginTop() + component.getMarginBottom());
                component.setPosition(x + component.getMarginLeft(), y + centerY / 2, width, height);

                x += component.getMarginLeft() + widths[i] + component.getMarginRight() +
                        container.getSpacing();
                i++;
            }
            y += maxHeight + container.getSpacing();
        }
    }

}
