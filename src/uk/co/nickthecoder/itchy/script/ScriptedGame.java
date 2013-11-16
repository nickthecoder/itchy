/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.GameManager;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedGame extends Game implements ScriptedObject
{
    private ScriptLanguage language;

    public Object gameScript;

    public ScriptedGame( GameManager gameManager, ScriptLanguage language, Object scriptInstance )
    {
        super(gameManager);
        this.language = language;
        this.gameScript = scriptInstance;
    }

    @Override
    public Object getScriptedObject()
    {
        return this.gameScript;
    }

    @Override
    public Object getProperty( String name )
        throws ScriptException
    {
        return this.language.getProperty(this.gameScript, name);
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

    @Override
    public boolean startScene( String sceneName )
    {
        return this.language.startScene(this, sceneName);
    }

    public boolean normalStartScene( String sceneName )
    {
        return super.startScene(sceneName);
    }

}
