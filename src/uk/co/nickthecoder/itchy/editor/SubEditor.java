package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class SubEditor
{
    protected Editor editor;

    protected Table table;

    protected Label message;

    protected Window editWindow;

    protected FileOpenDialog openDialog;

    /**
     * True iff the currently edited resource isn't in the resources yet, ie we are adding a new
     * record.
     */
    protected boolean adding;

    public SubEditor( Editor editor )
    {
        this.editor = editor;
    }

    public Container createPage()
    {
        Container form = new Container();
        form.setLayout(new VerticalLayout());
        form.setFill(true, false);

        return form;
    }

    protected Container createListButtons()
    {
        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        this.addListButtons(buttons);

        return buttons;
    }

    protected void addListButtons( Container buttons )
    {
        Button edit = new Button(new Label("Edit"));
        edit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onEdit();
            }
        });
        buttons.addChild(edit);

        Button add = new Button(new Label("Add"));
        add.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onAdd();
            }
        });
        buttons.addChild(add);

        Button remove = new Button(new Label("Remove"));
        remove.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onRemove();
            }
        });
        buttons.addChild(remove);

    }

    protected void addDetailButtons( Container buttons )
    {

        Button ok = new Button(new Label("Ok"));
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onOk();
            }
        });
        buttons.addChild(ok);

        Button cancel = new Button(new Label("Cancel"));
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SubEditor.this.onCancel();
            }
        });
        buttons.addChild(cancel);

    }

    public void setMessage( String message )
    {
        if ((message == null) || (message.equals(""))) {
            this.message.setVisible(false);
        } else {
            this.message.setVisible(true);
            this.message.setText(message);
        }
    }

    public Component addOptionalScrollbars( Component component, int maxWidth, int maxHeight )
    {
        Component result = component;

        if (component.getRequiredHeight() > maxHeight) {
            VerticalScroll vs = new VerticalScroll(result);
            vs.setClientHeight(maxHeight);
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
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table
                .getCurrentTableModelRow();

        this.showDetails(row.getData());
    }

    public void onRemove()
    {
        this.adding = false;
        if (this.table.getCurrentTableModelRow() == null) {
            return;
        }
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table
                .getCurrentTableModelRow();

        this.remove(row.getData());
    }

    public void showDetails( Object resource )
    {
        Window window = new Window("Edit");
        window.clientArea.setFill(true, true);
        window.clientArea.setLayout(new VerticalLayout());

        Container form = new Container();
        form.addStyle("form");
        GridLayout grid = new GridLayout(form, 2);
        form.setLayout(grid);
        window.clientArea.addChild(form);

        this.edit(grid, resource);

        this.message = new Label("");
        this.message.addStyle("error");
        this.message.setVisible(false);
        window.clientArea.addChild(this.message);

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        this.addDetailButtons(buttons);

        window.clientArea.addChild(buttons);

        Itchy.singleton.showWindow(window);
        this.editWindow = window;
    }

    protected abstract void edit( GridLayout grid, Object resource );

    protected abstract void onOk();

    protected void onCancel()
    {
        Itchy.singleton.hideWindow(this.editWindow);
    }

    protected abstract void remove( Object resource );

    protected abstract void onAdd();

}
