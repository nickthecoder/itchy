/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.StringUtils;

public class SceneDesignerPropertiesForm<S> extends PropertiesForm<S>
{
    private Map<String, String> oldValues;

    private String name;

    private SceneDesigner sceneDesigner;

    public SceneDesignerPropertiesForm( String name, SceneDesigner sd, S subject, List<Property<S, ?>> properties )
    {
        super(subject, properties);
        this.name = name;
        this.sceneDesigner = sd;
        this.oldValues = new HashMap<String, String>();
    }

    public String getName()
    {
        return this.name;
    }

    public S getSubject()
    {
        return this.subject;
    }

    @Override
    protected Component createComponent( final Property<S, ?> property )
    {
        Component result = super.createComponent(property);
        try {
            this.oldValues.put(property.key, property.getStringValue(this.subject));

            property.addChangeListener(result, new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    if (SceneDesignerPropertiesForm.this.sceneDesigner.undoList.isUndoing()) {
                        // We are performing an undo/redo, so don't need to create a new Undo item.
                        return;
                    }
                    try {
                        String newValue = property.getStringValue(SceneDesignerPropertiesForm.this.subject);
                        String oldValue = SceneDesignerPropertiesForm.this.oldValues.get(property.key);
                        SceneDesignerPropertiesForm.this.oldValues.put(property.key, newValue);
                        if (! StringUtils.equals(newValue,oldValue)) {

                            Undo undo = new UndoActorProperty<S>(
                                SceneDesignerPropertiesForm.this.sceneDesigner, SceneDesignerPropertiesForm.this.name,
                                SceneDesignerPropertiesForm.this.sceneDesigner.getCurrentActor(), property, oldValue, newValue);

                            SceneDesignerPropertiesForm.this.sceneDesigner.undoList.add(undo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void refresh( Property<S, ?> property )
    {
        try {
            Component component = getComponent(property.key);
            component.focus();
            property.refresh(this.subject, component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
