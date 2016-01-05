/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class EditorPreferences implements PropertySubject<EditorPreferences>
{
    protected static final List<Property<EditorPreferences, ?>> properties = new ArrayList<Property<EditorPreferences, ?>>();

    static {
        properties.add(new StringProperty<EditorPreferences>("textEditor"));
    }
    
    public String textEditor;

    public EditorPreferences()
    {
        Editor.instance.getPreferences().node("editorPreferences")
            .load(this, this.getProperties());
    }

    public void save()
    {
        Editor.instance.getPreferences().node("editorPreferences")
            .save(this, this.getProperties());
    }

    @Override
    public List<Property<EditorPreferences, ?>> getProperties()
    {
        return properties;
    }
}
