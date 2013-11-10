/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.GameManager;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.util.ClassName;

/**
 * Allows games to use script languages, such as javascript, for their game logic.
 * 
 * Subclasses of Behaviour, SceneBehaviour, CostumeProperties and Game can all be coded in a
 * scripting language.
 * 
 * All scripts are read from a "scripts" folder relative to the game's resources file. (i.e.
 * resources/MY_GAME/scripts/). No further sub-directories are supported.
 * 
 * At present, only Javascript is supported, but it should be simple to add others by subclassing
 * ScriptLanguage and calling ScriptManager.registerLanguage.
 */
public class ScriptManager
{
    public Resources resources;
    
    private static HashMap<String, Class<ScriptLanguage>> languageClassMap = new HashMap<String, Class<ScriptLanguage>>();

    private HashMap<String, ScriptLanguage> languages = new HashMap<String, ScriptLanguage>();

    @SuppressWarnings("unchecked")
    public ScriptManager( Resources resources )
    {
        this.resources = resources;

        registerLanguage("js",
            (Class<ScriptLanguage>) (JavascriptLanguage.class.asSubclass(ScriptLanguage.class)));
    }

    public static void registerLanguage( String extension, Class<ScriptLanguage> class1 )
    {
        languageClassMap.put(extension, class1);
    }

    public ScriptLanguage getLanguage( ClassName className )
    {
        return getLanguage( getExtension( className.name ));
    }

    public ScriptLanguage getLanguage( String extension )
    {
        ScriptLanguage result = this.languages.get(extension);
        if (result == null) {

            Class<ScriptLanguage> klass = languageClassMap.get(extension);
            try {
                Constructor<ScriptLanguage> constructor = klass.getConstructor(ScriptManager.class);
                result = constructor.newInstance(this);
                this.languages.put(extension, result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return result;
    }

    /**
     * Strips the file extension from the filename. (eg from Player.js to Player).
     */
    public static String getName( ClassName className )
    {
        int dot = className.name.lastIndexOf('.');
        if (dot >= 0) {
            return className.name.substring(0, dot);
        } else {
            return className.name;
        }
    }

    public static String getExtension( String filename )
    {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(dot + 1);
        } else {
            return "";
        }
    }

    public static boolean isScript( String filename )
    {
        String extension = getExtension(filename);

        for (String registeredExtension : languageClassMap.keySet()) {
            if (extension.equals(registeredExtension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isScript( ClassName className )
    {
        return isScript( className.name );
    }

    public File getScript( String filename )
    {
        File relative = new File(new File("scripts"), filename);
        File absolute = this.resources.resolveFile(relative);
        return absolute;
    }

    public boolean isValidScript( ClassName className )
    {
        return isValidScript(className.name);
    }
    
    public boolean isValidScript( String name )
    {

        if (name.endsWith(".js")) {
            if (getScript(name).exists()) {
                return true;
            }
        }

        return false;
    }

    public void loadScript( String filename )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript(filename);
    }

    public boolean createScript( String templateName, ClassName className )
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        return language.createScript(templateName, className);        
    }
    
    public Game createGame( GameManager gameManager, ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        language.loadScript(className.name);

        return language.createGame(gameManager, className);
    }

    public Behaviour createBehaviour( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        language.loadScript(className.name);

        return language.createBehaviour(className);
    }

    public SceneBehaviour createSceneBehaviour( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        language.loadScript(className.name);

        return language.createSceneBehaviour(className);
    }
    
    public CostumeProperties createCostumeProperties( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        language.loadScript(className.name);

        return language.createCostumeProperties(className);
    }
    
    
}
