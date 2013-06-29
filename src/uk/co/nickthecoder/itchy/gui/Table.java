package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.util.ReverseComparator;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class Table extends Container
{
    private TableModel model;

    private final List<TableModelColumn> columns;

    private final Container headings;

    private final Container rows;

    private final RowLayout rowLayout;

    TableRow currentRow;

    private final VerticalScroll scroll;

    private int sortColumn = -1;

    private boolean reverseSort;

    private final List<TableListener> tableListeners = new ArrayList<TableListener>();


    public Table( TableModel model, List<TableModelColumn> columns )
    {
        this.focusable = true;

        this.model = model;
        this.columns = columns;

        this.layout = new VerticalLayout();

        this.rowLayout = new RowLayout( this.columns );

        this.headings = new Container();
        this.headings.setType( "tableHeadings" );
        this.headings.setLayout( this.rowLayout );
        // this.headings.setFill( true, true );

        this.rows = new Container();
        this.rows.setLayout( new VerticalLayout() );
        this.rows.setFill( true, true );

        this.scroll = new VerticalScroll( this.rows );
        this.scroll.setType( "tableData" );
        this.scroll.setExpansion( 1 ); // TODO MORE ???
        this.setFill( false, true ); // TODO MORE ???

        for ( int i = 0; i < this.columns.size(); i++ ) {
            Component heading = this.createHeading( i );
            this.headings.addChild( heading );
        }

        this.addChild( this.headings );
        this.addChild( this.scroll );

        this.reset();
    }

    private Component createHeading( final int columnIndex )
    {
        TableModelColumn column = this.columns.get( columnIndex );

        Component heading;
        Comparator<TableModelRow> comparator = column.rowComparator;

        if ( comparator != null ) {
            Button buttonHeading = new Button( column.getTitle() );
            buttonHeading.setXAlignment( 0 );
            buttonHeading.addActionListener( new ActionListener()
            {
                @Override
                public void action()
                {
                    Table.this.headingClick( columnIndex );
                }
            } );
            heading = buttonHeading;
        } else {
            heading = new Label( column.getTitle() );
        }
        heading.addStyle( "cell" );
        heading.addStyle( "heading" );
        if ( columnIndex == 0 ) {
            heading.addStyle( "firstColumn" );
        }

        return heading;
    }

    public void setTableModel( TableModel model )
    {
        this.model = model;
        this.reset();
        this.currentRow = null;
    }

    public void reset()
    {
        this.rows.clear();

        if ( this.sortColumn >= 0 ) {
            Comparator<TableModelRow> comparator = this.columns.get( this.sortColumn ).rowComparator;
            if ( this.reverseSort ) {
                comparator = new ReverseComparator<TableModelRow>( comparator );
            }
            this.model.sort( comparator );
        }

        for ( int i = 0; i < this.model.getRowCount(); i++ ) {
            TableModelRow row = this.model.getRow( i );
            this.addRow( row );
        }

    }

    private void addRow( TableModelRow row )
    {
        Container rowContainer = new TableRow( this, row, this.rows.getChildren().size() );
        rowContainer.setType( "tableRow" );
        rowContainer.setLayout( this.rowLayout );

        boolean first = true;
        for ( TableModelColumn column : this.columns ) {
            Component cell = column.createCell( row );
            cell.addStyle( "cell" );
            rowContainer.addChild( cell );
            if ( first ) {
                cell.addStyle( "firstColumn" );
                first = false;
            }
        }
        this.rows.addChild( rowContainer );

        if ( ( this.rows.getChildren().size() % 2 ) == 0 ) {
            rowContainer.addStyle( "even" );
        } else {
            rowContainer.addStyle( "odd" );
        }
    }

    private void headingClick( int columnIndex )
    {
        if ( this.sortColumn == columnIndex ) {
            this.sort( columnIndex, !this.reverseSort );
        } else {
            this.sort( columnIndex );
        }
    }

    public void sort( int columnIndex )
    {
        this.sort( columnIndex, false );
    }

    public void sort( int columnIndex, boolean reverse )
    {
        if ( this.columns.get(  columnIndex  ).rowComparator != null ) {
            this.sortColumn = columnIndex;
            this.reverseSort = reverse;
            this.reset();
        }
    }

    public TableRow getCurrentRow()
    {
        return this.currentRow;
    }

    public TableModelRow getCurrentTableModelRow()
    {
        if ( this.currentRow == null ) {
            return null;
        }
        return this.currentRow.getTableModelRow();
    }

    /**
     * Called when a row have been picked. This is either when a row has been
     * double clicked, or it has been selected, and then activated with SPACE or
     * RETURN. If you wish to use this, then it is normal to create an anonymous
     * sub class, and override this method.
     */
    void pickRow( TableRow row )
    {
        if ( row != this.currentRow ) {
            this.selectRow( row );
        }

        for ( TableListener listener : this.tableListeners ) {
            listener.onRowPicked( this.currentRow );
        }
    }

    void selectRow( TableRow row )
    {
        if ( row != this.currentRow ) {

            if ( this.currentRow != null ) {
                this.currentRow.removeStyle( "selected" );
            }
            row.addStyle( "selected" );

            this.currentRow = row;
            for ( TableListener listener : this.tableListeners ) {
                listener.onRowSelected( this.currentRow );
            }
            this.scroll.ensureVisible( this.currentRow );
        }
    }

    public void addTableListener( TableListener listener ) {
        this.tableListeners.add( listener );
    }

    public void updateRow( TableModelRow row )
    {
        for ( Component rowc : this.rows.getChildren() ) {
            TableRow tableRow = (TableRow) rowc;

            if ( tableRow.row == row ) {

                int i = 0;
                for ( TableModelColumn column : this.columns ) {
                    Component component = tableRow.getChildren().get( i );
                    column.updateComponent( component, row );
                    i++;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if ( this.currentRow != null ) {
            if ( ke.symbol == Keys.UP ) {
                if ( this.currentRow.getRowIndex() > 0 ) {
                    this.selectRow( (TableRow) this.rows.getChildren().get( this.currentRow.getRowIndex() - 1 ) );
                }
                return true;
            }
            if ( ke.symbol == Keys.DOWN ) {
                if ( this.currentRow.getRowIndex() < this.rows.getChildren().size() - 1 ) {
                    this.selectRow( (TableRow) this.rows.getChildren().get( this.currentRow.getRowIndex() + 1 ) );
                }
                return true;
            }
            if ( ke.symbol == Keys.RETURN ) {
                this.pickRow( this.currentRow );
                return true;
            }
        }

        return super.onKeyDown( ke );
    }
}
