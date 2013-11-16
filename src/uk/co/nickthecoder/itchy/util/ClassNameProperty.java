/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.script.ScriptManager;

public class ClassNameProperty<S> extends AbstractProperty<S, ClassName>
{
    /**
     * The required type for the property, either Role, SceneDirector, CostumeProperty or
     * Game.
     */
    private Class<?> baseClass;

    public ClassNameProperty( Class<?> klass, String label, String access, String key )
    {
        super(label, access, key);
        this.baseClass = klass;
    }

    @Override
    public ClassName getDefaultValue()
    {
        return new ClassName("");
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        ClassName className = this.getSafeValue(subject);
        ScriptManager scriptManager = Resources.getScriptManager();

        final ClassNameBox classNameBox = new ClassNameBox(scriptManager, className, this.baseClass);

        if (autoUpdate) {

            classNameBox.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        ClassNameProperty.this.update(subject, classNameBox);
                        classNameBox.removeStyle("error");
                    } catch (Exception e) {
                        e.printStackTrace();
                        classNameBox.addStyle("error");
                    }
                }
            });
        }

        return classNameBox;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        ClassNameBox classNameBox = (ClassNameBox) component;
        classNameBox.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        ClassNameBox classNameBox = (ClassNameBox) component;

        try {
            this.setValue(subject, classNameBox.getClassName());
        } catch (Exception e) {
        }
    }

    @Override
    public ClassName parse( String value )
    {
        return new ClassName(value);
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        return getValue(subject).name;
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
