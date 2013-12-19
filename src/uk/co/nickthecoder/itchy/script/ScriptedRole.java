/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.ViewMouseListener;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedRole extends AbstractRole implements ViewMouseListener
{
    private final static HashMap<String, List<AbstractProperty<Role, ?>>> allProperties = new HashMap<String, List<AbstractProperty<Role, ?>>>();

    private ClassName className;

    private ScriptLanguage language;

    public final Object roleScript;

    public final ScriptProperties propertyValues;

    public final boolean isMouseListener;

    public ScriptedRole( ClassName className, ScriptLanguage language, Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.roleScript = scriptInstance;
        this.propertyValues = new ScriptProperties(this.language, scriptInstance);
        this.isMouseListener = this.language.isMouseListener(this);
    }

    @Override
    public List<AbstractProperty<Role, ?>> getProperties()
    {
        String name = ScriptManager.getName(this.className);

        List<AbstractProperty<Role, ?>> result = allProperties.get(name);
        if (result == null) {
            result = new ArrayList<AbstractProperty<Role, ?>>();
            allProperties.put(name, result);
        }
        return result;
    }

    public static void addProperty(
        String roleName, String propertyName, String label, Class<?> klass )
    {
        List<AbstractProperty<Role, ?>> properties = allProperties.get(roleName);

        if (properties == null) {
            properties = new ArrayList<AbstractProperty<Role, ?>>();
            allProperties.put(roleName, properties);

        } else {
            // If the property was previously defined, remove it.
            for (Iterator<AbstractProperty<Role, ?>> i = properties.iterator(); i.hasNext();) {
                AbstractProperty<Role, ?> property = i.next();
                if (property.key.equals(propertyName)) {
                    i.remove();
                }
            }
        }

        AbstractProperty<Role, ?> property = AbstractProperty.createProperty(
            klass, "propertyValues." + propertyName, propertyName, label, true, false, true);
        if (property != null) {
            properties.add(property);
        }

    }

    public ClassName getClassName()
    {
        return this.className;
    }

    public void superOnMessage( String message )
    {
        super.onMessage(message);
    }

    @Override
    public void onMessage( String message )
    {
        this.language.onMessage(this, message);
    }

    public void superOnBirth()
    {
        super.onBirth();
    }

    @Override
    public void onBirth()
    {
        this.language.onBirth(this);
    }

    public void superOnAttach()
    {
        super.onAttach();
    }

    @Override
    public void onAttach()
    {
        this.language.onAttach(this);
    }

    public void superOnDetach()
    {
        super.onDetach();
    }

    @Override
    public void onDetach()
    {
        this.language.onDetach(this);
    }

    public void superOnDeath()
    {
        super.onDeath();
    }

    @Override
    public void onDeath()
    {
        this.language.onDeath(this);
    }

    public void superTick()
    {
        super.tick();
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

    @Override
    public boolean isMouseListener()
    {
        return this.isMouseListener;
    }
}
