/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.util.BeanHelper;

public class TextBoxSynchronizer implements ComponentChangeListener
{
    private final TextBox textBox;
    private final Object subject;
    private final String propertyName;

    public TextBoxSynchronizer( TextBox textBox, Object subject, String propertyName )
    {
        this.textBox = textBox;
        this.subject = subject;
        this.propertyName = propertyName;

        textBox.addChangeListener(this);
        try {
            textBox.setEntryText((String) BeanHelper.getProperty(subject, propertyName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changed()
    {
        try {
            BeanHelper.setProperty(this.subject, this.propertyName, this.textBox.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
