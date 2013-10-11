/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;

public class EditorPreferences
{
    @Property(label = "Text Editor")
    public String textEditor;

    public EditorPreferences()
    {
        Editor.singleton.getPreferences().node("editorPreferences")
            .load(this, AbstractProperty.findAnnotations(this.getClass()));
    }

    public void save()
    {
        Editor.singleton.getPreferences().node("editorPreferences")
            .save(this, AbstractProperty.findAnnotations(this.getClass()));
    }

}