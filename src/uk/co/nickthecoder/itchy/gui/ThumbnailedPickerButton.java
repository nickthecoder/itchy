/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class ThumbnailedPickerButton<T extends Thumbnailed> extends PickerButton<T>
{
    private ImageComponent thumbnail;

    public ThumbnailedPickerButton( String title, T current, HashMap<String, T> hashMap )
    {
        this(title, current, hashMap, false);
    }

    public ThumbnailedPickerButton( String title, T current, HashMap<String, T> hashMap, boolean vertical )
    {
        super(title, current, hashMap);

        this.clear();

        Surface surface = (current == null) ? null : current.getThumbnail();
        this.thumbnail = new ImageComponent(surface);

        this.addChild(this.thumbnail);
        this.addChild(this.label);

        if (vertical) {
            this.setLayout(new VerticalLayout());
            this.setXAlignment(0.5);
            this.setYSpacing(5);
        } else {
            this.setLayout(new HorizontalLayout());
            this.setYAlignment(0.5);
            this.setXSpacing(10);
        }
    }

    @Override
    public void onClick( final MouseButtonEvent e )
    {
        ThumbnailedPicker<T> picker = new ThumbnailedPicker<T>(this.title, this.hashMap, this.getValue()) {
            @Override
            public void pick( String label, T object )
            {
                ThumbnailedPickerButton.this.label.setText(label);
                ThumbnailedPickerButton.this.thumbnail.setImage(object.getThumbnail());
                ThumbnailedPickerButton.this.value = object;
                fireChangeEvent();
            }
        };
        picker.show();
    }
}
