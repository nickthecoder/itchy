/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.AbstractProperty;

public class UndoActorProperty<S> extends UndoProperty<S>
{
    protected Actor actor;

    public UndoActorProperty( SceneDesigner sd, String formName, Actor actor, AbstractProperty<S, ?> property,
        String oldValue, String newValue )
    {
        super(sd, formName, property, oldValue, newValue);
        this.actor = actor;
    }

    @Override
    public void undo()
    {
        System.out.println("Undo Select Actor " + this.actor);
        this.sceneDesigner.selectActor(this.actor);
        super.undo();
    }

    @Override
    public void redo()
    {
        System.out.println("Redo Select Actor " + this.actor);
        this.sceneDesigner.selectActor(this.actor);
        super.undo();
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
