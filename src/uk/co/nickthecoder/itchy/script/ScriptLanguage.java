/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public abstract class ScriptLanguage
{
    public ScriptManager manager;

    private boolean initialised = false;

    protected ScriptEngine engine;

    protected HashMap<File, Long> lastLoadedMap;

    public ScriptLanguage( ScriptManager manager )
    {
        this.manager = manager;
        this.lastLoadedMap = new HashMap<File, Long>();
    }

    protected abstract void initialise();

    public abstract String getExtension();

    private void ensureInitialised()
        throws ScriptException
    {
        if (!this.initialised) {
            this.initialised = true;
            initialise();

            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("game", this.manager.resources.game);
            bindings.put("language", this.engine);
        }

        String path = ".." + File.separator + "scripts" + File.separator + this.getExtension() +
            File.separator;
        File directory = this.manager.resources.resolveFile(new File(path));

        String end = "." + getExtension();
        File[] scripts = directory.listFiles();
        Arrays.sort(scripts);
        for (File script : scripts) {
            if (script.getName().endsWith(end)) {
                privateLoadScript(script);
            }
        }

    }

    protected void loadScript( String filename )
        throws ScriptException
    {
        File file = this.manager.getScript(filename);
        loadScript(file);
    }

    protected void loadScript( File file )
        throws ScriptException
    {
        ensureInitialised();
        privateLoadScript(file);
    }

    private void privateLoadScript( File file )
        throws ScriptException
    {

        long lastModified = file.lastModified();
        this.engine.put(ScriptEngine.FILENAME, file.getPath());
        
        if (this.lastLoadedMap.containsKey(file)) {
            long lastLoaded = this.lastLoadedMap.get(file);
            if (lastLoaded > lastModified) {
                return;
            }
        }

        System.out.println("Loading script : " + file);
        try {
            Reader reader = new InputStreamReader(new FileInputStream(file));
            this.engine.eval(reader);
        } catch (IOException e) {
            
        }

        this.lastLoadedMap.put(file, new Date().getTime());
    }

    
    
    public abstract Behaviour createBehaviour( String filename )
        throws ScriptException;

    public abstract void onAttach( ScriptedBehaviour behaviour )
        throws ScriptException;

    public abstract void onDetach( ScriptedBehaviour behaviour )
        throws ScriptException;

    public abstract void onActivate( ScriptedBehaviour behaviour )
        throws ScriptException;

    public abstract void onDeactivate( ScriptedBehaviour behaviour )
        throws ScriptException;
    
    public abstract void onKill( ScriptedBehaviour behaviour )
        throws ScriptException;

    public abstract void onMessage( ScriptedBehaviour behaviour, String message )
        throws ScriptException;

    public abstract void tick( ScriptedBehaviour behaviour )
        throws ScriptException;

    
    
    public abstract SceneBehaviour createSceneBehaviour( String filename )
        throws ScriptException;


    
    public abstract void onActivate( ScriptedSceneBehaviour behaviour)
        throws ScriptException;
    
    public abstract void tick(ScriptedSceneBehaviour behaviour)
        throws ScriptException;
    
    public abstract boolean onMouseDown( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException;
    
    public abstract boolean onMouseUp( ScriptedSceneBehaviour behaviour,MouseButtonEvent mbe )
        throws ScriptException;

    public abstract boolean onMouseMove( ScriptedSceneBehaviour behaviour,MouseMotionEvent mme )
        throws ScriptException;
    
    public abstract boolean onKeyDown( ScriptedSceneBehaviour behaviour,KeyboardEvent ke )
        throws ScriptException;
    
    public abstract boolean onKeyUp( ScriptedSceneBehaviour behaviour,KeyboardEvent ke )
        throws ScriptException;
    
    public abstract void onMessage( ScriptedSceneBehaviour behaviour,String message )
        throws ScriptException;


    
}
