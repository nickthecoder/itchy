/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.ViewMouseListener;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedBehaviour extends AbstractBehaviour implements ViewMouseListener
{
    private final static HashMap<String, List<AbstractProperty<Behaviour, ?>>> allProperties = new HashMap<String, List<AbstractProperty<Behaviour, ?>>>();

    private ClassName className;

    private ScriptLanguage language;

    public final Object behaviourScript;

    public final ScriptProperties propertyValues;

    public ScriptedBehaviour( ClassName className, ScriptLanguage language, Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.behaviourScript = scriptInstance;
        this.propertyValues = new ScriptProperties(this.language, scriptInstance);
    }

    @Override
    public List<AbstractProperty<Behaviour, ?>> getProperties()
    {
        String name = ScriptManager.getName(this.className);

        List<AbstractProperty<Behaviour, ?>> result = allProperties.get(name);
        if (result == null) {
            result = new ArrayList<AbstractProperty<Behaviour, ?>>();
            allProperties.put(name, result);
        }
        return result;
    }

    public static void addProperty(
        String behaviourName, String propertyName, String label, Class<?> klass )
    {
        List<AbstractProperty<Behaviour, ?>> properties = allProperties.get(behaviourName);

        if (properties == null) {
            properties = new ArrayList<AbstractProperty<Behaviour, ?>>();
            allProperties.put(behaviourName, properties);

        } else {
            // If the property was previously defined, remove it.
            for (Iterator<AbstractProperty<Behaviour, ?>> i = properties.iterator(); i.hasNext();) {
                AbstractProperty<Behaviour, ?> property = i.next();
                if (property.key.equals(propertyName)) {
                    i.remove();
                }
            }
        }

        AbstractProperty<Behaviour, ?> property = AbstractProperty.createProperty(
            klass, "propertyValues." + propertyName, propertyName, label, true, false, true);
        if (property != null) {
            properties.add(property);
        }

    }

    public ClassName getClassName()
    {
        return this.className;
    }

    @Override
    public void onMessage( String message )
    {
        this.language.onMessage(this, message);
    }

    @Override
    public void onBirth()
    {
        this.language.onBirth(this);
    }

    @Override
    public void onDeath()
    {
        this.language.onDeath(this);
    }

    @Override
    public void tick()
    {
        this.language.tick(this);
    }

    @Override
    public boolean onMouseDown( MouseListenerView view, MouseButtonEvent event )
    {
        return this.language.onMouseDown(this, view, event);
    }

    @Override
    public boolean onMouseUp( MouseListenerView view, MouseButtonEvent event )
    {
        return this.language.onMouseUp(this, view, event);
    }

    @Override
    public boolean onMouseMove( MouseListenerView view, MouseMotionEvent event )
    {
        return this.language.onMouseMove(this, view, event);
    }

}
