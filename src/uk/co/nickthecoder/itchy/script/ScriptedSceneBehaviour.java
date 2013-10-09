/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedSceneBehaviour implements SceneBehaviour
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

    // TODO Need this? @Override
    public ClassName getClassName()
    {
        return this.className;
    }

    private void handleException( Exception e )
    {
        e.printStackTrace();
    }

    @Override
    public void onActivate()
    {
        try {
            this.language.onActivate(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onDeactivate()
    {
        try {
            this.language.onDeactivate(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void tick()
    {
        try {
            this.language.tick(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        try {
            return this.language.onMouseDown(this, mbe);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        try {
            return this.language.onMouseUp(this, mbe);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mme )
    {
        try {
            return this.language.onMouseMove(this, mme);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        try {
            return this.language.onKeyDown(this, ke);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        try {
            return this.language.onKeyUp(this, ke);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public void onMessage( String message )
    {
        try {
            this.language.onMessage(this, message);
        } catch (Exception e) {
            handleException(e);
        }
    }

}
