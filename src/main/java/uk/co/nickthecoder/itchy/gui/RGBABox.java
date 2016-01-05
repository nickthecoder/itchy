/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.RGBA;

public class RGBABox extends PlainContainer
{
    private TextBox textBox;

    private boolean includeAlpha;

    private boolean allowNull;

    public RGBABox( RGBA color, boolean allowNull, boolean includeAlpha )
    {
        addStyle("combo");
        this.includeAlpha = includeAlpha;
        this.allowNull = allowNull;

        this.textBox = new TextBox(color == null ? "" :
            this.includeAlpha ?
                color.getRGBACode() : color.getRGBCode());

        this.textBox.setBoxWidth(8);

        GuiButton button = new GuiButton("...");
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                RGBAPicker picker = new RGBAPicker(RGBABox.this.includeAlpha, RGBABox.this.textBox);
                picker.show();
            }
        });

        addChild(this.textBox);
        addChild(button);

        this.textBox.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                try {
                    getValue();
                    RGBABox.this.textBox.removeStyle("error");
                } catch (Exception e) {
                    RGBABox.this.textBox.addStyle("error");
                }
            }
        });
    }

    public RGBA getValue() throws Exception
    {
        return RGBA.parse(this.textBox.getText(), this.allowNull, this.includeAlpha);
    }

    public void setValue( RGBA value )
    {
        this.textBox.setText(this.includeAlpha ? value.getRGBACode() : value.getRGBCode());
    }

    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.textBox.addChangeListener(ccl);
    }

    public void removeChangeListener( ComponentChangeListener ccl )
    {
        this.textBox.removeChangeListener(ccl);
    }
}
