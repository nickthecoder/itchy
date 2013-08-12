/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.Collection;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Picker;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class ComboBox extends Container
{
    private TextBox name;

    private Button button;

    public String pickTitle = "Pick";

    private HashMap<String, String> map;

    public ComboBox( String defaultValue, Collection<String> values )
    {
        super();
        
        this.type="comboBox";
        this.addStyle("combo");
        this.name = new TextBox(defaultValue);
        this.map = new HashMap<String, String>();
        for (String value : values) {
            this.map.put(value, value);
        }
        this.button = new Button("...");
        this.button.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                Picker<String> picker = new Picker<String>(
                    ComboBox.this.pickTitle,
                    ComboBox.this.map,
                    ComboBox.this.getText())
                {
                    @Override
                    public void pick( String label, String object )
                    {
                        setText(label);
                    }
                };
                picker.show();
            }

        });
        this.addChild(this.name);
        this.addChild(this.button);
    }

    public String getText()
    {
        return this.name.getText();
    }

    public void setText( String text )
    {
        this.name.setText(text);
    }
    
    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.name.addChangeListener( ccl );
    }
}
