/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.AbstractDirector;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedDirector extends AbstractDirector implements ScriptedObject
{
    private ShimmedScriptLanguage language;

    private ClassName className;

    public Object directorScript;

    public ScriptedDirector( ClassName className, ShimmedScriptLanguage language, Object scriptInstance )
    {
        super();
        this.className = className;
        this.language = language;
        this.directorScript = scriptInstance;
    }

    public ClassName getClassName()
    {
        return this.className;
    }

    @Override
    public ScriptLanguage getLanguage()
    {
        return this.language;
    }

    @Override
    public Object getScriptedObject()
    {
        return this.directorScript;
    }

    /**
     * The javascript base class DirectorScript calls this as its default implementation. The game write can choose to call super to get the
     * default views and stages. Or they can not call super, and create there own stages and views.
     */
    public void superOnStarted()
    {
        super.onStarted();
    }

    @Override
    public void onStarted()
    {
        this.language.onStarted(this);
    }

    public void superOnActivate()
    {
        super.onActivate();
    }

    @Override
    public void onActivate()
    {
        this.language.onActivate(this);
    }

    public void superOnDeactivate()
    {
        super.onActivate();
    }

    @Override
    public void onDeactivate()
    {
        this.language.onDeactivate(this);
    }

    public void superOnQuit()
    {
        super.onQuit();
    }

    @Override
    public boolean onQuit()
    {
        return this.language.onQuit(this);
    }

    public boolean superOnKeyDown( KeyboardEvent event )
    {
        return super.onKeyDown(event);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {
        return this.language.onKeyDown(this, event);
    }

    public boolean superOnKeyUp( KeyboardEvent event )
    {
        return super.onKeyUp(event);
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return this.language.onKeyUp(this, ke);
    }

    public boolean superOnMouseDown( MouseButtonEvent event )
    {
        return super.onMouseDown(event);
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

    public boolean superOnMouseUp( MouseButtonEvent event )
    {
        return super.onMouseUp(event);
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        return this.language.onMouseUp(this, event);
    }

    public boolean superOnMouseMove( MouseMotionEvent event )
    {
        return super.onMouseMove(event);
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        return this.language.onMouseMove(this, event);
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

    public void superTick()
    {
        super.tick();
    }

    @Override
    public void tick()
    {
        this.language.tick(this);
    }

    public boolean superStartScene( String sceneName )
    {
        return super.startScene(sceneName);
    }

    @Override
    public boolean startScene( String sceneName )
    {
        return this.language.startScene(this, sceneName);
    }

}
