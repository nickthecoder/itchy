/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.lang.reflect.Constructor;

import uk.co.nickthecoder.itchy.script.ScriptManager;

/**
 * Solves a chicken and egg problem around creating a Game object. If a game is written in a
 * scripting language, then it needs a script manager to create the ScriptedGame object. However a
 * ScriptManager needs a Game object to be fully formed. So which object is created first? The
 * answer is to create a GameManager first, and let both Game and ScriptManager reference the
 * GameManager object while they are being created.
 */
public class GameManager
{
    public final Resources resources;

    public final ScriptManager scriptManager;

    private Game game;

    public GameManager( Resources resources )
    {
        this.resources = resources;
        this.scriptManager = new ScriptManager(resources);
    }

    public Game createGame()
        throws Exception
    {

        if (this.resources.isValidScript(this.resources.gameInfo.className.name)) {
            this.game = this.scriptManager.createGame(this, this.resources.gameInfo.className);

        } else {
            Class<?> klass = Class.forName(this.resources.gameInfo.className.name);
            Constructor<?> constructor = klass.getConstructor(GameManager.class);
            this.game = (Game) constructor.newInstance(this);
        }

        return this.game;
    }

    public Game getGame()
    {
        if (this.game == null) {
            throw new NullPointerException();
        }
        return this.game;
    }

}
