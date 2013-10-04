/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.Game;

public class ScriptedGame extends Game
{
    private ScriptLanguage language;

    public final Object scriptGame;

    public ScriptedGame( ScriptLanguage language, Object scriptInstance )
        throws Exception
    {
        super();

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
        try {
            this.language.onActivate(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public String getInitialSceneName()
    {
        try {
            return this.language.getInitialSceneName(this);
        } catch (Exception e) {
            handleException(e);
            return "start";
        }
    }

}
