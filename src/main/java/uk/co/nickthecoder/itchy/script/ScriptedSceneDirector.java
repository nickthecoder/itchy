/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedSceneDirector implements SceneDirector, ScriptedObject
{
    private ClassName className;

    private ShimmedScriptLanguage language;

    public final Object sceneDirectorScript;

    public ScriptedSceneDirector( ClassName className, ShimmedScriptLanguage language,
        Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.sceneDirectorScript = scriptInstance;
    }

    @Override
    public Object getScriptedObject()
    {
        return this.sceneDirectorScript;
    }

    @Override
    public ScriptLanguage getLanguage()
    {
        return this.language;
    }

    @Override
    public List<AbstractProperty<SceneDirector, ?>> getProperties()
    {
        return this.language.getProperties(this);
    }

    public ClassName getClassName()
    {
        return this.className;
    }

    @Override
    public void onLoaded()
    {
        this.language.onLoaded(this);
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

    @Override
    public CollisionStrategy getCollisionStrategy( Actor actor )
    {
        return this.language.getCollisionStrategy( this, actor);
    }

}
