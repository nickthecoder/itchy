/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Util;

public abstract class ScriptLanguage
{
    public final ScriptManager manager;

    protected HashMap<File, Long> lastLoadedMap;

    public ScriptLanguage( ScriptManager manager )
    {
        this.manager = manager;
        // TODO Not using this for Jython?
        this.lastLoadedMap = new HashMap<File, Long>();
        
        //TO DO if this works, then I don't need ensureInitialise
        initialise();
    }

    protected abstract void initialise();

    public abstract String getExtension();

    public abstract void reload();

    protected abstract void loadScript( ClassName className )
        throws ScriptException;

    protected abstract void loadScript( String filename )
        throws ScriptException;


    public void handleException( Exception e )
    {
        e.printStackTrace();
        this.manager.resources.errorLog.log(e.getMessage());
    }

    public void handleException( String activity, Exception e )
    {
        e.printStackTrace();
        this.manager.resources.errorLog.log(activity + " : " + e.getMessage());
    }

    public void log( String message )
    {
        this.manager.resources.errorLog.log(message);
    }
    
    public boolean createScript( String templateName, ClassName className )
    {
        HashMap<String, String> subs = new HashMap<String, String>();
        String name = ScriptManager.getName(className);
        
        String name1stUpper = name.substring(0,1).toUpperCase() + name.substring(1);
        String name1stLower = name.substring(0,1).toLowerCase() + name.substring(1);

        subs.put("NAME", name);
        subs.put("Name", name1stUpper);
        subs.put("name", name1stLower);

        try {
            createFromTemplate(className, templateName, subs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void createFromTemplate( ClassName className, String templateName,
        Map<String, String> substitutions )
        throws IOException
    {
        // template file : resources/templates/EXTENSION/TEMPLATE.EXTENSION
        File templateFile = new File(Itchy.getResourcesDirectory(),
            "templates" + File.separator + getExtension() + File.separator + templateName + "." +
                this.getExtension());

        File destFile = this.manager.getScript(className.name);

        Util.template(templateFile, destFile, substitutions);
    }

    public String simpleMessage( ScriptException e, boolean includeFilename )
    {
        return e.getMessage();
    }

    /**
     * @param inst An instance, such as a ScriptedRole instance, or a PyObject instance
     * 
     * @return True if inst is a scripted object used by that scripting language.
     */
    public abstract boolean isInstance( Object inst );

    public abstract Object getAttribute( Object inst, String name )
        throws ScriptException;

    public abstract void setAttribute( Object inst, String name, Object value )
        throws ScriptException;

    // ===== DIRECTOR =====

    public abstract Director createDirector( ClassName className );

    // ===== ROLE =====

    public abstract Role createRole( ClassName className );

    // ===== SCENE DIRECTOR=====

    public abstract SceneDirector createSceneDirector( ClassName className );

    // ====== COSTUME PROPERTIES =====

    public abstract CostumeProperties createCostumeProperties( ClassName className );

}
