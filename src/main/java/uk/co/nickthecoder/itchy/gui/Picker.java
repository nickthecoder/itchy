/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public abstract class Picker<T> extends Window
{
    public Picker( String title, Map<String, T> map )
    {
        this(title, map, null);
    }

    public Picker( String title, Map<String, T> map, T selected )
    {
        super(title);

        this.clientArea.setFill(true, false);
        this.clientArea.setLayout(new VerticalLayout());

        PlainContainer container = new PlainContainer();
        container.setLayout(new VerticalLayout());
        container.setFill(true, true);
        container.addStyle("picker");

        VerticalScroll vs = new VerticalScroll(container);
        this.clientArea.addChild(vs);

        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);

        for (String key : keys) {

            T object = map.get(key);

            Component component = this.createButton(key, object);
            container.addChild(component);

            if (((object == null) && (selected == null)) || ((object != null) && object.equals(selected))) {
                // component.addStyle("selected");
                component.focus();
            }

        }

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Picker.this.hide();
            }

        });
        buttons.addChild(cancelButton);

        this.clientArea.addChild(buttons);
    }

    private Component createButton( final String label, final T object )
    {

        Button button = new Button(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Picker.this.hide();
                Picker.this.pick(label, object);
            }
        });

        return button;
    }

    public abstract void pick( String label, T object );

}
