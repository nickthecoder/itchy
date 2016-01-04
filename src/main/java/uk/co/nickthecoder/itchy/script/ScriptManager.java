/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.util.HashMap;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.util.ClassName;

/**
 * Allows games to use script languages, such as python and groovy, for their game logic.
 * 
 * Subclasses of Director, SceneDirector, Role, CostumeProperties can all be coded in a scripting language.
 * 
 * All scripts are read from a "scripts" folder relative to the game's resources file. (i.e. resources/MY_GAME/scripts/).
 * Sub-directories are not supported.
 * 
 * At present, only python and groovy are supported, but adding others jvm based languages should be easy, and
 * non-jvm languages, such as Javascript is possible, but will be slower and more of a kludge. 
 */
public class ScriptManager
{
    public Resources resources;

    private static HashMap<String, ScriptLanguageFactory> registeredLanguages; 

    private HashMap<String, ScriptLanguage> languages = new HashMap<String, ScriptLanguage>();

    public ScriptManager( Resources resources )
    {
        this.resources = resources;
    }

    private static HashMap<String, ScriptLanguageFactory> getRegisteredLanguages()
    {
        if (registeredLanguages == null) {
        	registeredLanguages = new HashMap<String, ScriptLanguageFactory>();
        	registeredLanguages.put("py", new PythonFactory() );
        	registeredLanguages.put("groovy", new GroovyFactory() );
        }
        return registeredLanguages;
    }

    public ScriptLanguage findLanguage( ClassName className )
    {
        return findLanguageByExtension(getExtension(className.name));
    }

    public ScriptLanguage findLanguageByExtension( String extension )
    {
        ScriptLanguage result = this.languages.get(extension);
        if (result == null) {
        	
            ScriptLanguageFactory factory = getRegisteredLanguages().get(extension);
            if (factory == null) {
                return null;
            }

        	result = factory.create( this );
        	this.languages.put(extension, result);

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

    /**
     * Strips the file extension from the filename. (eg from Player.js to Player).
     */
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

        for (String registeredExtension : getRegisteredLanguages().keySet()) {
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
        ScriptLanguage language = findLanguage( className );
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
        ScriptLanguage language = findLanguage(className);
        language.loadScript(className);
    }

    public boolean createScript( String templateName, ClassName className )
    {
        ScriptLanguage language = findLanguage(className);
        return language.createScript(templateName, className);
    }

    public Director createDirector( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = findLanguage(className);

        return language.createDirector(className);
    }

    public Role createRole( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = findLanguage(className);

        return language.createRole(className);
    }

    public SceneDirector createSceneDirector( ClassName className )
        throws ScriptException
    {
        ScriptLanguage language = findLanguage(className);

        return language.createSceneDirector(className);
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
