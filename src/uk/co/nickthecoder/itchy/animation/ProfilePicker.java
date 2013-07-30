/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class ProfilePicker extends Window
{
    public ProfilePicker()
    {
        super("Pick a Profile");

        Container container = new Container();
        VerticalScroll vs = new VerticalScroll(container);

        this.createProfiles(container);
        this.clientArea.addChild(vs);
        this.clientArea.addStyle("vScrolled");
    }

    private void createProfiles( Container container )
    {
        GridLayout gridLayout = new GridLayout(container, 5);
        container.setLayout(gridLayout);
        container.addStyle("pickGrid");

        HashMap<String, Profile> map = NumericAnimation.getProfiles();
        List<String> orderedNames = Resources.sortNames(map.keySet());
        
        for (String name : orderedNames) {
            Profile profile = map.get(name);

            Component component = this.createButton(name, profile);

            gridLayout.addChild(component);
        }
        gridLayout.endRow();
    }

    private Component createButton( final String name, final Profile profile )
    {
        Container container = new Container();

        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5f);

        ImageComponent img = new ImageComponent(profile.getThumbnail());
        Button button = new Button(img);
        button.addStyle("test");
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                ProfilePicker.this.destroy();
                ProfilePicker.this.pick(profile);
            }
        });

        Label label = new Label(name);

        container.addChild(button);
        container.addChild(label);

        return container;
    }

    public abstract void pick( Profile profile );

}