/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.itchy.property.Property;

/**
 * Creates a framework for most of the Notebook pages within the Editor. Each SubEditor has a table listing a set of resources (e.g.
 * PoseResource or AnimationResource etc). It also has an "Edit" window where one of the entries in the table can be edited (either by
 * double clicking a table column, or selecting the column, and then clicking the "Edit" button. The Edit window is used for both updating
 * and adding.
 * <p>
 * The Edit forms are build using a PropertiesForm, which automates much of the GUI work, such as creating the components and updating the
 * values.
 * <p>
 * The Edit pages use a non-autoupdate mechanism, and therefore have Ok and Cancel buttons.
 * 
 * @param <S>
 *        The type of resource (e.g. PoseResource).
 */
public abstract class SubEditor<S>
{
    protected Editor editor;

    protected Table table;

    protected Label message;

    protected Window editWindow;

    protected FileOpenDialog openDialog;

    protected PropertiesForm<S> form;

    protected S currentResource;

    /**
     * True iff the currently edited resource isn't in the resources yet, ie we are adding a new record.
     */
    protected boolean adding;

    public SubEditor( Editor editor )
    {
        this.editor = editor;
    }

    public Resources getResources()
    {
        return this.editor.resources;
    }

    public Container createPage()
    {
        Container page = new PlainContainer();
        page.setLayout(new VerticalLayout());
        page.setFill(true, true);

        addHeader(page);

        this.table = this.createTable();
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
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
     *        The container to place additional component on.
     */
    protected void addHeader( Container page )
    {
    }

    /**
     * Override if components are needed below the table.
     * 
     * @param page
     *        The container to place additional component on.
     */
    protected void addFooter( Container page )
    {
    }

    protected abstract Table createTable();

    protected abstract TableModel createTableModel();

    protected void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    private Container createListButtons()
    {
        PlainContainer buttonBar = new PlainContainer();
        buttonBar.addStyle("buttonBar");
        buttonBar.setXAlignment(0.5f);

        this.addListButtons(buttonBar);

        return buttonBar;
    }

    /**
     * Adds buttons which appear at the bottom of the page. Edit, Add and Remove are the default ones.
     * 
     * @param buttonBar
     *        The container which is the parent to the Button components
     */
    protected void addListButtons( Container buttonBar )
    {
        Button edit = new Button(new Label("Edit"));
        edit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onEdit();
            }
        });
        buttonBar.addChild(edit);

        Button add = new Button(new Label("Add"));
        add.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onAdd();
            }
        });
        buttonBar.addChild(add);

        Button remove = new Button(new Label("Remove"));
        remove.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onRemove();
            }
        });
        buttonBar.addChild(remove);

    }

    /**
     * Adds buttons which appear at the bottom of the "Edit" window. The defaults are Ok and Cancel.
     * 
     * @param buttonBar
     *        The parent of the Button components.
     */
    protected void addDetailButtons( Container buttonBar )
    {

        Button ok = new Button(new Label("Ok"));
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onOk();
            }
        });
        buttonBar.addChild(ok);

        Button cancel = new Button(new Label("Cancel"));
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onCancel();
            }
        });
        buttonBar.addChild(cancel);

    }

    /**
     * Adds a message to the Edit window - generally indicating an error.
     * 
     * @param message
     *        The message to be displayed.
     */
    protected void setMessage( String message )
    {
        if ((message == null) || (message.equals(""))) {
            this.message.setVisible(false);
        } else {
            this.message.setVisible(true);
            this.message.setText(message);
        }
    }

    public Component addOptionalScrollbars( AbstractComponent component, int maxWidth, int maxHeight )
    {
        AbstractComponent result = component;

        if (component.getRequiredHeight() > maxHeight) {
            VerticalScroll vs = new VerticalScroll(result);
            vs.setNaturalHeight(maxHeight);
            result = vs;
        }

        return result;
    }

    public void onEdit()
    {
        this.adding = false;
        if (this.table.getCurrentTableModelRow() == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        ReflectionTableModelRow<S> row = (ReflectionTableModelRow<S>) this.table.getCurrentTableModelRow();

        this.edit(row.getData(), false);
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

    protected void edit( S resource, boolean adding )
    {
        this.adding = adding;
        this.currentResource = resource;

        this.editWindow = new Window("Edit");
        this.editWindow.clientArea.setFill(true, true);
        this.editWindow.clientArea.setLayout(new VerticalLayout());

        this.form = new PropertiesForm<S>(this.currentResource, getProperties());
        
        this.editWindow.clientArea.addChild(createForm());

        this.message = new Label("");
        this.message.addStyle("error");
        this.message.setVisible(false);
        this.editWindow.clientArea.addChild(this.message);

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        this.addDetailButtons(buttons);

        this.editWindow.clientArea.addChild(buttons);
        this.editWindow.show();
    }

    protected Component createForm()
    {
        this.form.createForm();
        return this.form.container;
    }

    protected abstract List<Property<S, ?>> getProperties();

    protected void update() throws MessageException
    {
        try {
            this.form.update();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessageException("Error : " + e.toString());
        }
    }

    protected void onOk()
    {
        try {
            update();
            if (this.adding) {
                this.rebuildTable();
            } else {
                this.table.updateRow(this.table.getCurrentTableModelRow());
            }

            this.editWindow.hide();
        } catch (MessageException e) {
            setMessage(e.getMessage());
        }
    }

    protected void onCancel()
    {
        this.editWindow.hide();
    }

    protected abstract void remove( S resource );

    protected abstract void onAdd();

}
