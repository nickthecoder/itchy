/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
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
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Util;
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
            bindings.put("scriptLanguage", this);
            bindings.put("scriptEngine", this.engine);
        }

        // Load all of the scripts in resources/scripts/${LANGUAGE-EXTENSION}/
        File directory = new File(Itchy.getResourcesDirectory(),
            "scripts" + File.separator + this.getExtension());

        String end = "." + getExtension();
        File[] scripts = directory.listFiles();
        // Sort by name, so they are loaded in the correct order. The names are prefixed with a 2
        // digit number.
        Arrays.sort(scripts);
        for (File script : scripts) {
            if (script.getName().endsWith(end)) {
                privateLoadScript(script);
            }
        }

    }

    public boolean createScript( String templateName, ClassName className )
    {
        HashMap<String, String> subs = new HashMap<String, String>();
        subs.put("NAME", ScriptManager.getName(className));

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

        try {
            Reader reader = new InputStreamReader(new FileInputStream(file));
            if (this.engine instanceof Compilable) {
                System.out.println("Compiling script : " + file);
                Compilable compilable = (Compilable) this.engine;
                compilable.compile(reader).eval();
            } else {
                System.out.println("Loading script : " + file);
                this.engine.eval(reader);
            }
        } catch (IOException e) {

        }

        this.lastLoadedMap.put(file, new Date().getTime());
    }

    public String simpleMessage( ScriptException e, boolean includeFilename )
    {
        return e.getMessage();
    }

    public abstract Object getProperty( Object inst, String name )
        throws ScriptException;

    public abstract Object putProperty( Object inst, String name, Object value )
        throws ScriptException;

    // ===== DIRECTOR =====

    public abstract Director createDirector( ClassName className );

    public abstract void onStarted( ScriptedDirector director );

    public abstract void onActivate( ScriptedDirector director );

    public abstract void onDeactivate( ScriptedDirector director );

    public abstract boolean onQuit( ScriptedDirector director );

    public abstract boolean onKeyDown( ScriptedDirector director, KeyboardEvent ke );

    public abstract boolean onKeyUp( ScriptedDirector director, KeyboardEvent ke );

    public abstract boolean onMouseDown( ScriptedDirector director, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedDirector director, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedDirector director, MouseMotionEvent mme );

    public abstract void onMessage( ScriptedDirector director, String message );

    public abstract void tick( ScriptedDirector director );

    public abstract boolean startScene( ScriptedDirector director, String sceneName );

    // ===== ROLE =====

    public abstract Role createRole( ClassName className );

    public abstract void onBirth( ScriptedRole role );

    public abstract void onDeath( ScriptedRole role );
    
    public abstract void onAttach( ScriptedRole role );
    
    public abstract void onDetach( ScriptedRole role );
    
    public abstract boolean onMouseDown( ScriptedRole role, MouseListenerView view, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedRole role, MouseListenerView view, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedRole role, MouseListenerView view, MouseMotionEvent mbe );

    public abstract void onMessage( ScriptedRole role, String message );

    public abstract void tick( ScriptedRole role );

    public abstract boolean isMouseListener( ScriptedRole role );

    // ===== SCENE DIRECTOR=====

    public abstract SceneDirector createSceneDirector( ClassName className );

    public abstract void onActivate( ScriptedSceneDirector role );

    public abstract void onDeactivate( ScriptedSceneDirector role );

    public abstract void tick( ScriptedSceneDirector role );

    public abstract boolean onMouseDown( ScriptedSceneDirector role, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedSceneDirector role, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedSceneDirector role, MouseMotionEvent mme );

    public abstract boolean onKeyDown( ScriptedSceneDirector role, KeyboardEvent ke );

    public abstract boolean onKeyUp( ScriptedSceneDirector role, KeyboardEvent ke );

    public abstract void onMessage( ScriptedSceneDirector role, String message );

    // ====== COSTUME PROPERTIES =====

    public abstract CostumeProperties createCostumeProperties( ClassName className );

}
