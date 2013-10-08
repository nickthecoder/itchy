/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.util.ClassName;
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
            bindings.put("game", this.manager.resources.getGame());
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

    public boolean createScript( String templateName, ClassName className )
    {
        HashMap<String, String> subs = new HashMap<String, String>();
        subs.put("NAME", className.name);
        
        try {
            createFromTemplate( className, templateName, subs );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void createFromTemplate( ClassName className, String templateName, Map<String, String> substitutions )
        throws IOException
    {
        File file = this.manager.getScript(className.name);

        String templateFilename = ".." + File.separator + "templates" + File.separator + getExtension() +
            File.separator + templateName + "." + this.getExtension();
        
        File templateFile = new File( this.manager.resources.resolveFilename( templateFilename ) );

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
            templateFile)));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

        String line = reader.readLine();
        while (line != null) {
            writeTemplateLine(writer, line, substitutions);

            line = reader.readLine();
        }
        reader.close();
        writer.close();

    }

    private void writeTemplateLine( PrintWriter out, String line, Map<String, String> substitutions )
    {
        int fromIndex = 0;
        int open = line.indexOf("${");
        while (open >= 0) {

            out.print(line.substring(fromIndex, open));

            int close = line.indexOf("}", open);
            if (close < 0) {
                out.println("${");
                fromIndex += 2;

            } else {
                String key = line.substring(open + 2, close);
                System.out.println("Found key : " + key);
                if (substitutions.containsKey(key)) {
                    out.print(substitutions.get(key));
                } else {
                    out.print("${");
                    out.print(key);
                    out.print("}");
                }
                fromIndex = close + 1;
            }
            open = line.indexOf("${", fromIndex);
        }
        out.println(line.substring(fromIndex));
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

    public abstract Object getProperty( Object inst, String name )
        throws ScriptException;

    public abstract Object putProperty( Object inst, String name, Object value )
        throws ScriptException;

    public abstract Game createGame( Resources resources, ClassName className )
        throws ScriptException;

    public abstract void onActivate( ScriptedGame game )
        throws ScriptException;

    public abstract String getInitialSceneName( ScriptedGame game )
        throws ScriptException;

    public abstract Behaviour createBehaviour( ClassName className )
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

    public abstract SceneBehaviour createSceneBehaviour( ClassName className )
        throws ScriptException;

    public abstract void onActivate( ScriptedSceneBehaviour behaviour )
        throws ScriptException;

    public abstract void onDeactivate( ScriptedSceneBehaviour behaviour )
        throws ScriptException;

    public abstract void tick( ScriptedSceneBehaviour behaviour )
        throws ScriptException;

    public abstract boolean onMouseDown( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException;

    public abstract boolean onMouseUp( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException;

    public abstract boolean onMouseMove( ScriptedSceneBehaviour behaviour, MouseMotionEvent mme )
        throws ScriptException;

    public abstract boolean onKeyDown( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException;

    public abstract boolean onKeyUp( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException;

    public abstract void onMessage( ScriptedSceneBehaviour behaviour, String message )
        throws ScriptException;

}
