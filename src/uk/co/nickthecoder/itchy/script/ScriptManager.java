/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.util.HashMap;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneBehaviour;

/**
 * Allows games to use script languages, such as javascript, for their game logic.
 *
 * Subclasses of Behaviour, SceneBehaviour, CostumeProperties and Game can all be coded
 * in a scripting language.
 * 
 * All scripts are read from a "scripts" folder relative to the game's resources file.
 * (i.e. resources/MY_GAME/scripts/). No further sub-directories are supported.
 * 
 * At present, only Javascript is supported, but it should be simple to add others by subclassing
 * ScriptLanguage and calling ScriptManager.registerLanguage.
 */
public class ScriptManager
{
    public Resources resources;

    private HashMap<String, ScriptLanguage> languages;

    public ScriptManager( Resources resources )
    {
        this.resources = resources;
        this.languages = new HashMap<String, ScriptLanguage>();

        registerLanguage( new JavascriptLanguage(this));
    }

    public void registerLanguage( ScriptLanguage language )
    {
        this.languages.put(language.getExtension(), language);
    }

    public File getScript( String filename )
    {
        File relative = new File(new File("scripts"), filename);
        File absolute = this.resources.resolveFile(relative);
        return absolute;
    }

    public ScriptLanguage getLanguage( String extension )
    {
        return this.languages.get(extension);
    }

    /**
     * Strips the file extension from the filename. (eg from Player.js to Player).
     */
    public String getName( String filename )
    {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(0, dot);
        } else {
            return filename;
        }
    }

    public String getExtension( String filename )
    {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(dot + 1);
        } else {
            return "";
        }
    }

    public boolean isValidScript( String filename )
    {

        if (filename.endsWith(".js")) {
            if (getScript(filename).exists()) {
                return true;
            }
        }

        return false;
    }

    public void loadScript( String filename )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript( filename );
    }

    
    
    public Game createGame( String filename )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript(filename);

        return language.createGame(this.resources, filename);
    }
    

    public Behaviour createBehaviour( String filename )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript(filename);

        return language.createBehaviour(filename);
    }
    
    public SceneBehaviour createSceneBehaviour( String filename )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript(filename);

        return language.createSceneBehaviour(filename);
    }
}
