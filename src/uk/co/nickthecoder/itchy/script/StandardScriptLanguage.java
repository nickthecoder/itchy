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

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.util.ClassName;

public abstract class StandardScriptLanguage extends ScriptLanguage
{
    protected ScriptEngine engine;

    public StandardScriptLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    protected void initialise()
    {
        this.engine = createEngine();
        
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptLanguage", this);
        bindings.put("scriptEngine", this.engine);
        
        File directory = this.manager.getIncludeDirectory(this);

        String end = "." + getExtension();
        File[] scripts = directory.listFiles();
        // Sort by name, so they are loaded in the correct order. The names are prefixed with a 2
        // digit number.
        Arrays.sort(scripts);
        for (File script : scripts) {
            if (script.getName().endsWith(end)) {
                try {
                    loadScript(script);
                } catch (Exception e) {
                    
                }
            }
        }

    }
    
    abstract protected ScriptEngine createEngine();

    public boolean eventResult( Object result )
    {
        try {
            return (boolean) result;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void loadScript( ClassName className )
        throws ScriptException
    {
        File file = this.manager.getScript(className.name);
        loadScript(file);
    }

    @Override
    protected void loadScript( String filename )
        throws ScriptException
    {
        File file = this.manager.getScript(filename);
        loadScript(file);
    }

    protected void loadScript( File file )
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
            e.printStackTrace();
            throw new ScriptException("IOException loading " + file);
        }

        this.lastLoadedMap.put(file, new Date().getTime());
    }

}
