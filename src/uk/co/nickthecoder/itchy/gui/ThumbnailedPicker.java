/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.jame.Surface;

public abstract class ThumbnailedPicker<T extends Thumbnailed> extends Window
{
    public ThumbnailedPicker( String title, Map<String, T> map )
    {
        this(title, map, null);
    }

    public ThumbnailedPicker( String title, Map<String, T> map, T selected )
    {
        super(title);

        this.clientArea.setFill(true, false);
        this.clientArea.setLayout(new VerticalLayout());

        Container container = new Container();
        container.setFill(true, true);
        GridLayout grid = new GridLayout(container, 6);
        container.setLayout(grid);
        container.setFill(true, true);
        container.setXSpacing(10);
        container.setYSpacing(10);

        VerticalScroll vs = new VerticalScroll(container);
        this.clientArea.addChild(vs);

        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);

        for (String key : keys) {

            T object = map.get(key);

            Component component = this.createButton(key, object);
            grid.addChild(component);

            if (((object == null) && (selected == null)) ||
                ((object != null) && object.equals(selected))) {
                // component.addStyle("selected");
                component.focus();
            }

        }
        grid.endRow();

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                ThumbnailedPicker.this.hide();
            }

        });
        buttons.addChild(cancelButton);

        this.clientArea.addChild(buttons);
    }

    private Component createButton( final String label, final T object )
    {
        Container container = new Container();
        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5);
        container.setYAlignment(1);
        container.setFill(false, true);

        Button button;
        Surface surface = object.getThumbnail();
        if (surface == null) {
            button = new Button("");
        } else {
            button = new Button(new ImageComponent(surface));
        }
        button.setExpansion(1.0);

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                ThumbnailedPicker.this.hide();
                ThumbnailedPicker.this.pick(label, object);
            }
        });
        container.addChild(button);
        container.addChild(new Label(label));

        return container;
    }

    public abstract void pick( String label, T object );

}
