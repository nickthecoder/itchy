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

import uk.co.nickthecoder.itchy.util.ClassName;

public class GroovyLanguage extends ScriptLanguage
{

	GroovyClassLoader groovyClassLoader;

    private Map<ClassName, Class<?>> classes;

    public GroovyLanguage( ScriptManager manager )
    {
        super(manager);
        initialise();
    }

    private final void initialise()
    {
    	this.classes = new HashMap<ClassName, Class<?>>();
        
        ClassLoader parent = getClass().getClassLoader();        
        this.groovyClassLoader = new GroovyClassLoader(parent);
        this.groovyClassLoader.addClasspath( this.manager.getScriptDirectory().getPath() );
        this.groovyClassLoader.addClasspath( this.manager.getIncludeDirectory(this).getPath() );
    }

    @Override
    public String getExtension()
    {
        return "groovy";
    }
    
    @Override
    public void loadScript( ClassName className, File file )
		throws ScriptException
    {
        try {
        	Class<?> groovyClass = groovyClassLoader.parseClass(file);
        	
            this.classes.put(className, groovyClass);            
        } catch (Exception e) {
            throw wrapException(e);
        }
    }

    @Override
    public void unload()
    {
    	super.unload();
    	this.initialise();
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

    protected Object createInstance( ClassName className )
    	throws Exception
    {
    	
        if ( ! this.classes.containsKey(className) ) {
        	this.loadScript(className);
        }
        
        Class<?> klass = this.classes.get(className);
        return klass.newInstance();
    }
    
}
