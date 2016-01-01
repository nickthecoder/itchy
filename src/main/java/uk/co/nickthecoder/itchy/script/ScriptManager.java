/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.util.ClassName;

/**
 * Allows games to use script languages, such as javascript, for their game logic.
 * 
 * Subclasses of Role, SceneDirector, CostumeProperties and Game can all be coded in a scripting language.
 * 
 * All scripts are read from a "scripts" folder relative to the game's resources file. (i.e. resources/MY_GAME/scripts/). No further
 * sub-directories are supported.
 * 
 * At present, only Javascript is supported, but it should be simple to add others by subclassing ScriptLanguage and calling
 * ScriptManager.registerLanguage.
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

        registerLanguage("py", (Class<ScriptLanguage>) (PythonLanguage.class.asSubclass(ScriptLanguage.class)));
        registerLanguage("groovy", (Class<ScriptLanguage>) (GroovyLanguage.class.asSubclass(ScriptLanguage.class)));
    }

    public static void registerLanguage( String extension, Class<ScriptLanguage> class1 )
    {
        languageClassMap.put(extension, class1);
    }

    public ScriptLanguage getLanguage( ClassName className )
    {
        return getLanguage(getExtension(className.name));
    }

    public ScriptLanguage getLanguage( String extension )
    {
        ScriptLanguage result = this.languages.get(extension);
        if (result == null) {

            Class<ScriptLanguage> klass = languageClassMap.get(extension);
            if (klass == null) {
                return null;
            }

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
        return getName(className.name);
    }

    public static String getName( String filename )
    {
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            return filename.substring(0, dot);
        } else {
            return filename;
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

    public static boolean isScript( ClassName className )
    {
        return isScript(className.name);
    }

    public static boolean isScript( String name )
    {
        String extension = getExtension(name);

        for (String registeredExtension : languageClassMap.keySet()) {
            if (extension.equals(registeredExtension)) {
                return isValidName(getName(name));
            }
        }
        return false;
    }

    public static boolean isValidName( String name )
    {
        return name.matches("[a-zA-Z0-9]*");
    }

    public File getScriptDirectory()
    {
        File relative = new File("scripts");
        File absolute = this.resources.resolveFile(relative);
        return absolute;
    }
    
    public File getIncludeDirectory( ScriptLanguage language )
    {
        File directory = new File(Itchy.getResourcesDirectory(),
            "scripts" + File.separator + language.getExtension());
        
        return directory;
    }

    
    public File getScript( String filename )
    {
        File relative = new File(new File("scripts"), filename);
        File absolute = this.resources.resolveFile(relative);
        return absolute;
    }

    public boolean isValidScript( ClassName className )
    {
        ScriptLanguage language = getLanguage( className );
        if (language == null) {
            return false;
        }
        
        if (getScript(className.name).exists()) {
            return true;
        }

        return false;
    }
    
    public void loadScript( ClassName className )
        throws ScriptException
    {
        String filename = className.name;
        
        ScriptLanguage language = getLanguage(getExtension(filename));
        language.loadScript(className);
    }

    public boolean createScript( String templateName, ClassName className )
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));
        return language.createScript(templateName, className);
    }

    public Director createDirector( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));

        return language.createDirector(className);
    }

    public Role createRole( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));

        return language.createRole(className);
    }

    public SceneDirector createSceneDirector( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));

        return language.createSceneDirector(className);
    }

    public CostumeProperties createCostumeProperties( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = getLanguage(getExtension(className.name));

        return language.createCostumeProperties(className);
    }

    public Object getAttribute( Object inst, String name ) throws ScriptException
    {
        for (ScriptLanguage language : this.languages.values()) {
            if (language.isInstance( inst )) {
                Object result = language.getAttribute(inst, name);
                return result;
            }
        }
        throw new ScriptException( "Not a scripted instance" );
    }

    public void setAttribute( Object inst, String name, Object value ) throws ScriptException
    {
        for (ScriptLanguage language : this.languages.values()) {
            if (language.isInstance( inst )) {
                language.setAttribute(inst, name, value);
                return;
            }
        }
        throw new ScriptException( "Not a scripted instance" );
    }

}
