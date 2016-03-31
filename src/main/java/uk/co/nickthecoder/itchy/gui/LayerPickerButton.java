/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;
import java.util.Map;

import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class LayerPickerButton extends PickerButton<Layer>
{
    private Layout layout;
    
    private static Map<String, Layer> createPickMap(Layout layout)
    {       
        HashMap<String, Layer> result = new HashMap<String, Layer>();

        for (Layer layer : layout.getLayersByZOrder()) {
            result.put(layer.getName(), layer);
        }
        return result;
    }

    public LayerPickerButton(Layout layout, Layer layer)
    {
        super("Layer", layer, createPickMap(layout));
        this.layout = layout;
    }

    @Override
    public boolean matches(Layer a, Layer b)
    {
        return a.getName().equals(b.getName());
    }
    
    public void onClick( final MouseButtonEvent e )
    {
        this.map = createPickMap(this.layout);
        super.onClick(e);
    }

}
