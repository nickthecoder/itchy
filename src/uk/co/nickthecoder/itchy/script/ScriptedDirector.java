/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.AbstractDirector;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedDirector extends AbstractDirector implements ScriptedObject
{
    private ScriptLanguage language;

    public Object directorScript;

    public ScriptedDirector( ScriptLanguage language, Object scriptInstance )
    {
        super();
        this.language = language;
        this.directorScript = scriptInstance;
    }

    @Override
    public Object getScriptedObject()
    {
        return this.directorScript;
    }

    @Override
    public Object getProperty( String name )
        throws ScriptException
    {
        return this.language.getProperty(this.directorScript, name);
    }

    /**
     * The javascript base class DirectorScript calls this as its default implementation.
     * The game write can choose to call super to get the default views and stages.
     * Or they can not call super, and create there own stages and views.
     */
    public void defaultOnStarted()
    {
        super.onStarted();
    }

    @Override
    public void onStarted()
    {
        this.language.onStarted(this);
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        this.language.onActivate(this);
    }

    @Override
    public void onDeactivate()
    {
        this.language.onDeactivate(this);
        super.onDeactivate();
    }

    @Override
    public boolean onQuit()
    {
        return this.language.onQuit(this);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (this.language.onKeyDown(this, ke)) {
            return true;
        } else {
            return super.onKeyDown(ke);
        }
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        if (this.language.onKeyUp(this, ke)) {
            return true;
        } else {
            return super.onKeyUp(ke);
        }
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        if (this.language.onMouseDown(this, event)) {
            return true;
        } else {
            return super.onMouseDown(event);
        }
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if (this.language.onMouseUp(this, event)) {
            return true;
        } else {
            return super.onMouseUp(event);
        }
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        if (this.language.onMouseMove(this, event)) {
            return true;
        } else {
            return super.onMouseMove(event);
        }
    }

    @Override
    public void onMessage( String message )
    {
        this.language.onMessage(this, message);
    }

    @Override
    public void tick()
    {
        super.tick();
        this.language.tick(this);
    }


    public boolean defaultStartScene( String sceneName )
    {
        return super.startScene(sceneName);
    }

    @Override
    public boolean startScene( String sceneName )
    {
        return this.language.startScene(this, sceneName);
    }

}
