/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.Behaviour;

public class ScriptedBehaviour extends Behaviour
{
    private String filename;

    private ScriptLanguage language;

    public final Object scriptBehaviour;

    public ScriptedBehaviour( String name, ScriptLanguage language, Object scriptInstance )
    {
        this.filename = name;
        this.language = language;
        this.scriptBehaviour = scriptInstance;
    }

    @Override
    public String getClassName()
    {
        return this.filename;
    }

    private void handleException( Exception e )
    {
        e.printStackTrace();
    }

    @Override
    public void onAttach()
    {
        try {
            this.language.onAttach(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onDetach()
    {
        try {
            this.language.onDetach(this);
        } catch (Exception e) {
            handleException(e);
        }
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
    public void onMessage( String message )
    {
        try {
            this.language.onMessage(this, message);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onKill()
    {
        try {
            this.language.onKill(this);
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

}
