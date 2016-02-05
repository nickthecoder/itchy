package uk.co.nickthecoder.itchy.editor;

import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;

/**
 * Creates a framework for most of the Notebook pages within the Editor. Each SubEditor has a table listing a set of
 * resources (e.g.
 * PoseResource or AnimationResource etc). It also has an "Edit" window where one of the entries in the table can be
 * edited (either by
 * double clicking a table column, or selecting the column, and then clicking the "Edit" button. The Edit window is used
 * for both updating
 * and adding.
 * <p>
 * The Edit forms are build using a PropertiesForm, which automates much of the GUI work, such as creating the
 * components and updating the values.
 * <p>
 * The Edit pages use a non-autoupdate mechanism, and therefore have Ok and Cancel buttons.
 * 
 * @param <S>
 *            The type of resource (e.g. PoseResource).
 */
public abstract class ListSubjects<S>
{
    protected Resources resources;

    protected Table table;

    /**
     * True iff the currently edited resource isn't in the resources yet, ie we are adding a new record.
     */
    protected boolean adding;
    
    public boolean buttonsBelow = true;

    public ListSubjects(Resources resources)
    {
        this.resources = resources;
    }

    public Container createPage()
    {
        Container page = new PlainContainer();
        if ( buttonsBelow ) {
            page.setLayout(new VerticalLayout());
        } else {
            page.setXSpacing(20);
            page.setLayout(new HorizontalLayout());
        }
        page.setFill(true, true);

        addHeader(page);

        this.table = this.createTable();
        this.table.addTableListener(new AbstractTableListener()
        {
            @Override
            public void onRowPicked(TableRow tableRow)
            {
                onEdit();
            }
        });

        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        this.table.sort(0);

        page.addChild(this.table);

        addFooter(page);

        page.addChild(this.createListButtons());

        return page;
    }

    /**
     * Override if components are needed above the table.
     * 
     * @param page
     *            The container to place additional component on.
     */
    protected void addHeader(Container page)
    {
    }

    /**
     * Override if components are needed below the table.
     * 
     * @param page
     *            The container to place additional component on.
     */
    protected void addFooter(Container page)
    {
    }

    protected Table createTable()
    {
        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, createTableColumns());

        return table;
    }

    protected abstract List<TableModelColumn> createTableColumns();

    protected abstract TableModel createTableModel();

    protected void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    protected TableModelRow findRow( S subject )
    {
        for (int i = 0; i < this.table.getTableModel().getRowCount(); i ++ ) {
            TableModelRow row = this.table.getTableModel().getRow(i);
            if (rowMatches(row, subject)) {
                return row;
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    protected boolean rowMatches( TableModelRow row, S subject )
    {
        return ((ReflectionTableModelRow<S>) row).getData() == subject;
    }
    
    protected void update(S subject, boolean isNew)
    {
        if (isNew) {
            this.rebuildTable();
        } else {
            TableModelRow row = this.findRow(subject);
            if ( row == null ) {
                this.rebuildTable();
            } else {
                this.table.updateRow(row);
            }
        }
    }

    private Container createListButtons()
    {
        PlainContainer buttonBar = new PlainContainer();
        buttonBar.addStyle("buttonBar");
        
        if ( buttonsBelow) {
            buttonBar.setXAlignment(0.5f);
        } else {
            buttonBar.setXAlignment(0.5f);
            buttonBar.setYAlignment(0.5f);
            buttonBar.setLayout(new VerticalLayout());
        }

        this.addListButtons(buttonBar);

        return buttonBar;
    }

    /**
     * Adds buttons which appear at the bottom of the page. Edit, Add and Remove are the default ones.
     * 
     * @param buttonBar
     *            The container which is the parent to the Button components
     */
    protected void addListButtons(Container buttonBar)
    {
        GuiButton edit = new GuiButton(new Label("Edit"));
        edit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                ListSubjects.this.onEdit();
            }
        });
        buttonBar.addChild(edit);

        GuiButton add = new GuiButton(new Label("Add"));
        add.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                ListSubjects.this.onAdd();
            }
        });
        buttonBar.addChild(add);

        GuiButton remove = new GuiButton(new Label("Remove"));
        remove.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                ListSubjects.this.onRemove();
            }
        });
        buttonBar.addChild(remove);

    }

    public void onAdd()
    {
        this.addOrEdit(null);
    }

    public void onEdit()
    {
        this.adding = false;
        if (this.table.getCurrentTableModelRow() == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        ReflectionTableModelRow<S> row = (ReflectionTableModelRow<S>) this.table.getCurrentTableModelRow();

        this.addOrEdit((S) row.getData());
    }

    public void onRemove()
    {
        this.adding = false;
        if (this.table.getCurrentTableModelRow() == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        ReflectionTableModelRow<S> row = (ReflectionTableModelRow<S>) this.table.getCurrentTableModelRow();

        remove(row.getData());
        rebuildTable();
    }

    /**
     * 
     * @param subject
     *            The subject to be edited, or null to add a new subject.
     */
    protected abstract void addOrEdit(S subject);

    protected abstract void remove(S subject);

}
