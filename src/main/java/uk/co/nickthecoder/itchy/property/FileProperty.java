/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import java.io.File;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.FilenameComponent;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class FileProperty<S> extends Property<S, File>
{
    public FileProperty( String key )
    {
        super(key);
    }

    @Override
    public File getDefaultValue()
    {
        return new File("");
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final FilenameComponent box = new FilenameComponent(Itchy.getGame().resources, this.getSafeValue(subject));
        if (autoUpdate) {
            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        FileProperty.this.update(subject, box);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return box;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        TextBox textBox = (TextBox) component;
        textBox.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        try {
            this.setValue(subject, new File(filenameComponent.getText()));
            filenameComponent.removeStyle("error");
        } catch (Exception e) {
            filenameComponent.addStyle("error");
            throw e;
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        filenameComponent.setText(getValue(subject).getPath());
    }

    @Override
    public File parse( String value )
    {
        return new File(value);
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
