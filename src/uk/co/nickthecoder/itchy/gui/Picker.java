/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class Picker<T> extends Window
{
    public Picker( String title, HashMap<String, T> hashmap )
    {
        this(title, hashmap, null);
    }

    public Picker( String title, HashMap<String, T> hashmap, T selected )
    {
        super(title);

        Container container = new Container();
        container.setLayout(new VerticalLayout());
        container.setFill(true, true);

        VerticalScroll vs = new VerticalScroll(container);
        this.clientArea.addChild(vs);

        ArrayList<String> keys = new ArrayList<String>(hashmap.keySet());
        Collections.sort(keys);

        for (String key : keys) {

            T object = hashmap.get(key);

            Component component = this.createButton(key, object);
            container.addChild(component);

            if (((object == null) && (selected == null)) ||
                    ((object != null) && object.equals(selected))) {
                component.addStyle("selected");
                component.focus();
            }

        }

    }

    private Component createButton( final String label, final T object )
    {

        Button button = new Button(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Picker.this.destroy();
                Picker.this.pick(label, object);
            }
        });

        return button;
    }

    public abstract void pick( String label, T object );

}
