/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedSceneBehaviour implements SceneBehaviour, ScriptedObject
{
    private final static HashMap<String, List<AbstractProperty<SceneBehaviour, ?>>> allProperties = new HashMap<String, List<AbstractProperty<SceneBehaviour, ?>>>();

    private ClassName className;

    private ScriptLanguage language;

    public final Object sceneBehaviourScript;

    public final ScriptProperties propertyValues;

    public ScriptedSceneBehaviour( ClassName className, ScriptLanguage language,
        Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.sceneBehaviourScript = scriptInstance;
        this.propertyValues = new ScriptProperties(language, scriptInstance);
    }

    @Override
    public Object getScriptedObject()
    {
        return this.sceneBehaviourScript;
    }

    @Override
    public Object getProperty( String name )
        throws ScriptException
    {
        return this.language.getProperty(this.sceneBehaviourScript, name);
    }

    @Override
    public List<AbstractProperty<SceneBehaviour, ?>> getProperties()
    {
        String name = ScriptManager.getName(this.className);

        List<AbstractProperty<SceneBehaviour, ?>> result = allProperties.get(name);
        if (result == null) {
            result = new ArrayList<AbstractProperty<SceneBehaviour, ?>>();
            allProperties.put(name, result);
        }
        return result;
    }

    public static void addProperty(
        String sceneBehaviourName, String propertyName, String label, Class<?> klass )
    {
        List<AbstractProperty<SceneBehaviour, ?>> properties = allProperties
            .get(sceneBehaviourName);
        if (properties == null) {
            properties = new ArrayList<AbstractProperty<SceneBehaviour, ?>>();
            allProperties.put(sceneBehaviourName, properties);
        }

        AbstractProperty<SceneBehaviour, ?> property = AbstractProperty.createProperty(
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
    public void onActivate()
    {
        this.language.onActivate(this);
    }

    @Override
    public void onDeactivate()
    {
        this.language.onDeactivate(this);
    }

    @Override
    public void tick()
    {
        this.language.tick(this);
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        return this.language.onMouseDown(this, mbe);
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        return this.language.onMouseUp(this, mbe);
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mme )
    {
        return this.language.onMouseMove(this, mme);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        return this.language.onKeyDown(this, ke);
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return this.language.onKeyUp(this, ke);
    }

    @Override
    public void onMessage( String message )
    {
        this.language.onMessage(this, message);
    }

}
