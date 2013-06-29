package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class RowLayout implements Layout
{

    private List<TableModelColumn> columns;

    public RowLayout( List<TableModelColumn> columns )
    {
        this.columns = columns;
    }

    public void calculateRequirements( Container container )
    {
        int width = 0;
        int height = 0;

        int i = 0;
        for ( Component child : container.getChildren() ) {
            TableModelColumn column = this.columns.get( i++ );
            width += column.getWidth() + child.getMarginLeft() + child.getMarginRight();
            int temp = child.getRequiredHeight() + child.getMarginTop() + child.getMarginBottom();
            if ( temp > height ) {
                height = temp;
            }
        }

        container.setNaturalWidth( width + container.getPaddingLeft() + container.getPaddingRight() );
        container.setNaturalHeight( height + container.getPaddingTop() + container.getPaddingBottom() );
    }

    public void layout( Container container )
    {
        int c = 0;
        int x = container.getPaddingLeft();
        int y = container.getPaddingTop();

        for ( Component child : container.getChildren() ) {
            TableModelColumn column = this.columns.get( c++ );

            int width = column.getWidth() - child.getMarginLeft() - child.getMarginRight();
            int height = container.getHeight() - child.getMarginTop() - child.getMarginBottom();

            child.setPosition( x + child.getMarginLeft(), y + child.getMarginTop(), width, height );
            x += width + container.getSpacing();
        }
    }

}
