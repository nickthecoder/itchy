/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.ViewMouseListener;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class ScriptedRole extends AbstractRole implements ScriptedObject, ViewMouseListener
{
    private ClassName className;

    private ShimmedScriptLanguage language;

    public final Object roleScript;

    public final boolean isMouseListener;

    public ScriptedRole( ClassName className, ShimmedScriptLanguage language, Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.roleScript = scriptInstance;
        this.isMouseListener = this.language.isMouseListener(this);
    }

    @Override
    public ScriptLanguage getLanguage()
    {
        return this.language;
    }

    @Override
    public List<AbstractProperty<Role, ?>> getProperties()
    {
        return this.language.getProperties(this);
    }

    @Override
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

    @Override
    public Object getScriptedObject()
    {
        return this.roleScript;
    }
}
