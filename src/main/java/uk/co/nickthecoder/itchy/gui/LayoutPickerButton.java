/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;
import java.util.Map;

import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;

public class LayoutPickerButton extends PickerButton<Layout>
{
    
    private static Map<String, Layout> createPickMap(Resources resources)
    {
        HashMap<String,Layout> result = new HashMap<String,Layout>();
        
        for (String name : resources.layoutNames() ) {
            result.put(name, resources.getLayout(name));
        }
        return result;
    }
    
    public LayoutPickerButton( Resources resources, Layout layout )
    {
        super("Layout", layout, createPickMap(resources));
    }

}
