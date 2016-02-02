/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import java.io.File;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.FilenameComponent;

public class FileProperty<S> extends Property<S, File>
{
    /**
     * True if the file must exist.
     */
    public boolean mustExist = true;

    
    public FileProperty(String key)
    {
        super(key);
        this.defaultValue = new File("");
    }

    @Override
    public Component createUnvalidatedComponent(final S subject, boolean autoUpdate)
    {
        final FilenameComponent box = new FilenameComponent(Itchy.getGame().resources, this.getSafeValue(subject));
        if (autoUpdate) {
            box.addChangeListener(new ComponentChangeListener()
            {
                @Override
                public void changed()
                {
                    try {
                        FileProperty.this.updateSubject(subject, box);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return box;
    }

    @Override
    public void addChangeListener(Component component, ComponentChangeListener listener)
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        filenameComponent.addChangeListener(listener);
    }

    @Override
    public void addValidator(Component component, ComponentValidator validator)
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        filenameComponent.addValidator(validator);
    }

    @Override
    public File getValueFromComponent(Component component)
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        return new File(filenameComponent.getText());
    }

    @Override
    public void updateComponentValue(File value, Component component)
    {
        FilenameComponent filenameComponent = (FilenameComponent) component;
        filenameComponent.setText(value.getPath());
    }

    @Override
    public File parse(String value)
    {
        return new File(value);
    }

    public FileProperty<S> mustExist( boolean value )
    {
        this.mustExist = value;
        return this;
    }

    /**
     * When validating if the file is valid, this resolves relative Files into absolute files.
     * This implementation uses Resources.resolveFile, so relative paths are resolved relative to the
     * current game's resources folder.
     */
    protected File resolveFile( File file )
    {
        return Itchy.getGame().resources.resolveFile(file);
    }
    
    @Override
    public boolean isValid( Component component )
    {
        if ( this.mustExist ) {
            File file = resolveFile(this.getValueFromComponent(component));
            if (!file.exists() ) {
                return false;
            }
        }
        return super.isValid(component);
    }
}
