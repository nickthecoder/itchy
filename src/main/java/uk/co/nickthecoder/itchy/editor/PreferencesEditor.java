/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.property.Property;

public class PreferencesEditor
{
    private Editor editor;

    public PreferencesEditor( Editor editor )
    {
        this.editor = editor;
    }

    public Container createPage()
    {
        Container page = new PlainContainer();
        page.setLayout(new VerticalLayout());
        page.setFill(true, false);

        PlainContainer form = new PlainContainer();
        GridLayout grid = new GridLayout(form, 2);
        form.setLayout(grid);

        EditorPreferences editorPreferences = this.editor.preferences;

        for (Property<EditorPreferences, ?> property : editorPreferences.getProperties()) {
            try {
                Component component = property.createComponent(editorPreferences, true);
                grid.addRow(property.label, component);
            } catch (Exception e) {
            }
        }

        PlainContainer buttonBar = new PlainContainer();
        GuiButton save = new GuiButton("Save");
        save.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                PreferencesEditor.this.editor.preferences.save();
            }

        });

        buttonBar.addChild(save);

        page.addChild(form);
        page.addChild(buttonBar);

        return page;
    }

}
