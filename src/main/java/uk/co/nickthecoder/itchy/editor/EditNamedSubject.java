package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.NamedSubject;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.TextBox;

public abstract class EditNamedSubject<S extends NamedSubject<S>> extends EditSubject<S>
{

    public EditNamedSubject(Resources resources, ListSubjects<S> listSubjects, S subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    protected abstract S getSubjectByName( String name );

    protected abstract void rename();

    @Override
    protected Component createForm()
    {
        super.createForm();

        final TextBox nameBox = (TextBox) form.getComponent("name");

        nameBox.addValidator(new ComponentValidator()
        {
            @Override
            public boolean isValid()
            {
                return isValidName(nameBox.getText());
            }
        });

        nameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (!nameBox.hasStyle("error")) {
                    rename();
                }
            }
        });

        return form.container;
    }
    

    protected boolean isValidName(String name)
    {
        try {
            S found = getSubjectByName(name);
            if (found == null) {
                // No resource with that name
                return true;
            }
            // Valid, if the resource is the one that is found, otherwise the name is already in use.
            return found == subject;
        } catch (Exception e) {
            return false;
        }
    }
}
