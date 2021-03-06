package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.InputInterface;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public abstract class EditSubject<S extends PropertySubject<S>>
{
    protected Resources resources;

    protected ListSubjects<S> listSubjects;
    
    protected Label message;

    protected Window editWindow;

    protected PropertiesForm<S> form;

    protected S subject;

    protected InputInterface inputCancel;
    
    protected InputInterface inputOk;

    /**
     * True iff the currently edited resource isn't in the resources yet, ie we are adding a new record.
     */
    protected boolean isNew;

    public EditSubject(Resources resources, ListSubjects<S> listSubjects, S subject, boolean isNew)
    {
        this.resources = resources;
        this.listSubjects = listSubjects;
        this.subject = subject;
        this.isNew = isNew;
        
        try {
            inputCancel = Input.parse("ESCAPE");
            inputOk = Input.parse("RETURN");
        } catch (Exception e) {
        }
    }

    protected abstract String getSubjectName();

    protected void show()
    {
        String title = (isNew ? "New " : "Edit ") + getSubjectName();

        this.editWindow = new Window(title) {
            public void keyDown( KeyboardEvent ke )
            {
                super.keyDown(ke);
                EditSubject.this.onKeyDown(ke);
            }
        };
        
        this.editWindow.clientArea.setFill(true, true);
        this.editWindow.clientArea.setLayout(new VerticalLayout());

        this.form = new PropertiesForm<S>(this.subject, this.subject.getProperties());
        this.form.autoUpdate = true;

        this.editWindow.clientArea.addChild(createForm());

        this.message = new Label("");
        this.message.addStyle("error");
        this.message.setVisible(false);
        this.editWindow.clientArea.addChild(this.message);

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        this.addButtons(buttons);

        this.editWindow.clientArea.addChild(buttons);
        this.editWindow.show();
    }

    protected void onKeyDown( KeyboardEvent ke )
    {        
        if (inputCancel.matches(ke)) {
            onCancel();
            ke.stopPropagation();
        }
        if (inputOk.matches(ke)) {
            onOk();
            ke.stopPropagation();
        }
    }

    /**
     * Adds buttons which appear at the bottom of the "Edit" window. The defaults are Ok and Cancel.
     * 
     * @param buttonBar
     *            The parent of the Button components.
     */
    protected void addButtons(Container buttonBar)
    {

        Button ok = new Button(new Label("Ok"));
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                EditSubject.this.onOk();
            }
        });
        buttonBar.addChild(ok);

        Button cancel = new Button(new Label("Cancel"));
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                EditSubject.this.onCancel();
            }
        });
        buttonBar.addChild(cancel);

    }

    /**
     * Adds a message to the Edit window - generally indicating an error.
     * 
     * @param message
     *            The message to be displayed.
     */
    protected void setMessage(String message)
    {
        if ((message == null) || (message.equals(""))) {
            this.message.setVisible(false);
        } else {
            this.message.setVisible(true);
            this.message.setText(message);
        }
    }

    protected Component createForm()
    {
        this.form.createForm();
        return this.form.container;
    }
    
    protected void onOk()
    {
        if (this.form.isOk()) {
            if (isNew) {
                add();
            }

            if (this.listSubjects != null) {
                this.listSubjects.update(this.subject, isNew);
            }

            if (this.form.hasChanged()) {
                this.resources.dirty();
            }
            this.editWindow.hide();
        }
    }

    protected void onCancel()
    {
        this.form.revert();
        this.editWindow.hide();
    }

    protected abstract void add();
}
