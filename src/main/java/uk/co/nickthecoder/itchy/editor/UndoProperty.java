/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.property.Property;

public class UndoProperty<S> extends AbstractUndoProperty<S>
{
    protected String formName;

    protected SceneDesigner sceneDesigner;

    public UndoProperty( SceneDesigner sd, String formName, Property<S, ?> property, String oldValue, String newValue )
    {
        super(property, oldValue, newValue);
        this.formName = formName;
        this.sceneDesigner = sd;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SceneDesignerPropertiesForm<S> getForm()
    {
        return this.sceneDesigner.getForm(this.formName);
    }

    @Override
    public boolean merge( Undo o )
    {
        if (o.getClass() == UndoProperty.class) {
            @SuppressWarnings("unchecked")
            UndoProperty<S> other = (UndoProperty<S>) o;
            if ((other.formName.equals(this.formName)) && (this.property.key.equals(other.property.key))) {
                this.newValue = other.newValue;
                return true;
            }
        }
        return false;
    }
}
