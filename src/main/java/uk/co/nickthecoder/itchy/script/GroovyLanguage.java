/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the GNU Public License v3.0 which accompanies this distribution,
 * and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;

public class GroovyLanguage extends ScriptLanguage
{

	GroovyClassLoader groovyClassLoader;

    private Map<String, Class<?>> classes;

    public GroovyLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    protected void initialise()
    {
    	this.classes = new HashMap<String, Class<?>>();
        ClassLoader parent = getClass().getClassLoader();
        this.groovyClassLoader = new GroovyClassLoader(parent);
    }
    
    ScriptException wrapException( Exception e )
    {        
        return new ScriptException( e );
    }
    
    @Override
    public void reload()
    {
    	this.initialise();
    }
    
    @Override
    public void loadScript( String filename )
        throws ScriptException
    {
        throw new ScriptException("Not Implemented");
    }

    @Override
    public void loadScript( ClassName className )
        throws ScriptException
    {
        try {
        	Class<?> groovyClass = groovyClassLoader.parseClass(new File(this.manager.getScriptDirectory(),className.name));
        	
            this.classes.put(className.name, groovyClass);            
        } catch (Exception e) {
            throw wrapException(e);
        }
    }

    @Override
    public String getExtension()
    {
        return "groovy";
    }

    /**
     * As groovy instances act just like regular java instances, we don't need to treat them as special, so return false.
     */
    @Override
    public boolean isInstance( Object inst )
    {
        return false;
    }

    /**
     * As groovy instances act just like regular java instances, we don't need to treat them as special, so this should never be called.
     * (because isInstance always returns false) 
     */
    @Override
    public Object getAttribute( Object inst, String name ) throws ScriptException
    {
    	throw new ScriptException( "Groovy objects should not be treated as special; do not call getAttribute");
    }
    
    /**
     * As groovy instances act just like regular java instances, we don't need to treat them as special, so this should never be called.
     * (because isInstance always returns false) 
     */
    @Override
    public void setAttribute( Object inst, String name, Object value ) throws ScriptException
    {
    	throw new ScriptException( "Groovy objects should not be treated as special; do not call setAttribute");
    }

    private Class<?> getClass( ClassName className )
    {
        return this.classes.get(className.name);
    }

    protected Object createInstance( ClassName className )
    	throws Exception
    {
        this.manager.loadScript(className);
        Class<?> klass = getClass(className);
        return klass.newInstance();
    }
    
    @Override
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

    @Override
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

    @Override
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

    @Override
    public CostumeProperties createCostumeProperties( ClassName className )
    {
        try {
        	CostumeProperties costumeProperties = (CostumeProperties) createInstance( className );
            return costumeProperties;

        } catch (Exception e) {
            handleException("Creating CostumeProperties", e);
            return new CostumeProperties();
        }
    }

}
