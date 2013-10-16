/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedGame extends Game
{
    private ScriptLanguage language;

    public Object scriptGame;
    
    public ScriptedGame( Resources resources, ScriptLanguage language, Object scriptInstance)
        throws Exception
    {
        super( resources );
        this.language = language;
        this.scriptGame = scriptInstance;
    }

    private void handleException( Exception e )
    {
        e.printStackTrace();
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
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
    public boolean onQuit()
    {
        try {
            return this.language.onQuit(this);
        } catch (Exception e) {
            handleException(e);
            return super.onQuit();
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
    public void onMessage( String message )
    {
        try {
            this.language.onMessage(this, message);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        try {
            this.language.tick(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

}
