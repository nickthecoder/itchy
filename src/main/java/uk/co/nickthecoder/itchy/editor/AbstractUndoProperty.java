/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.property.AbstractProperty;

public abstract class AbstractUndoProperty<S> implements Undo
{
    protected AbstractProperty<S, ?> property;

    protected String oldValue;

    protected String newValue;

    public AbstractUndoProperty( AbstractProperty<S, ?> property, String oldValue, String newValue )
    {
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void undo()
    {
        try {
            SceneDesignerPropertiesForm<S> form = getForm();
            this.property.setValueByString(form.subject, this.oldValue);
            getForm().refresh(this.property);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void redo()
    {
        try {
            SceneDesignerPropertiesForm<S> form = getForm();
            this.property.setValueByString(form.subject, this.newValue);
            form.refresh(this.property);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean merge( Undo other )
    {
        return false;
    }

    public abstract SceneDesignerPropertiesForm<S> getForm();
}
