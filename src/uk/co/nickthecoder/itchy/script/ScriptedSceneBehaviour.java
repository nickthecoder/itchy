/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedSceneBehaviour implements SceneBehaviour
{
    private String filename;

    private ScriptLanguage language;

    public final Object scriptBehaviour;

    public ScriptedSceneBehaviour( String name, ScriptLanguage language, Object scriptInstance )
    {
        this.filename = name;
        this.language = language;
        this.scriptBehaviour = scriptInstance;
    }

    // TODO Need this? @Override
    public String getClassName()
    {
        return this.filename;
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
            return this.language.onMouseDown(this,mbe);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent mbe )
    {
        try {
            return this.language.onMouseUp(this,mbe);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent mme )
    {
        try {
            return this.language.onMouseMove(this,mme);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        try {
            return this.language.onKeyDown(this,ke);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        try {
            return this.language.onKeyUp(this,ke);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    @Override
    public void onMessage( String message )
    {
        try {
            this.language.onMessage(this,message);
        } catch (Exception e) {
            handleException(e);
        }        
    }


}
