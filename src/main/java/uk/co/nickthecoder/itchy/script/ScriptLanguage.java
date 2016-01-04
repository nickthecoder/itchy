/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Util;

public abstract class ScriptLanguage
{
    public final ScriptManager manager;

    protected HashMap<ClassName, Long> lastLoadedMap;

    public ScriptLanguage( ScriptManager manager )
    {
        this.manager = manager;
        this.lastLoadedMap = new HashMap<ClassName, Long>();        
    }

    public abstract String getExtension();

    /**
     * Unloads all classes.
     */
    public void unload()
    {
    	this.lastLoadedMap.clear();
    }
    
    public void loadScript( ClassName className )
        throws ScriptException
    {
		File file = new File(this.manager.getScriptDirectory(), className.name);
		
		if ( ! file.exists() ) {
			throw new ScriptException( "Script not found " + file );
		}
		
		Long lastLoaded = lastLoadedMap.get( className );
		
		// Don't even TRY to reload while the game is running - bad things are bound to happen!
		if ( ! this.manager.resources.game.isRunning() ) {
			long lastModified = file.lastModified();
			
			if ( (lastLoaded != null) && ( lastModified > lastLoaded ) ) {
				// The game isn't running, and the script has been changed, so lets unload everything
				// so that we pick up the changes to the script.
				unload();
				lastLoaded = null;
			}
		}
		
		if ( lastLoaded == null ) {
			loadScript( className, file );
			lastLoadedMap.put( className, new Date().getTime() );
		}
    }

    /**
     * Load a script from the given file
     * @param file
     * @param name The name without the file extension. e.g. "PacMan" for groovy files, or "pacMan" for python files
     * @throws ScriptException
     */
    public abstract void loadScript( ClassName className, File file ) throws ScriptException;
    

    protected ScriptException wrapException( Exception e )
    {        
        return new ScriptException( e );
    }
    
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


    protected abstract Object createInstance( ClassName className ) throws Exception;

	
    public Director createDirector( ClassName className )
    {
        try {
            Director director = (Director) createInstance( className );
            return director;
            
        } catch (Exception e) {
            handleException("Creating Director", e);
            return new PlainDirector();
        }
    }

    public Role createRole( ClassName className )
    {
        try {
            Role role= (Role) createInstance( className );
            return role;

        } catch (Exception e) {
            handleException("Creating Role", e);
            return new PlainRole();
        }
    }

    public SceneDirector createSceneDirector( ClassName className )
    {
        try {
        	SceneDirector sceneDirector = (SceneDirector) createInstance( className );
            return sceneDirector;

        } catch (Exception e) {
            handleException("Creating SceneDirector", e);
            return new PlainSceneDirector();
        }
    }

}
