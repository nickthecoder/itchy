/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyProxy;
import org.python.util.PythonInterpreter;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.PlainRole;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.util.ClassName;

public class PythonLanguage extends ScriptLanguage
{

    PythonInterpreter interpreter;

    private Map<String, PyObject> classes = new HashMap<String, PyObject>();

    public PythonLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    protected void initialise()
    {
        Properties properties = new Properties();
        String path =
            this.manager.getScriptDirectory().getPath() + ":" +
                this.manager.getIncludeDirectory(this).getPath();

        properties.setProperty("python.path", path);
        PythonInterpreter.initialize(System.getProperties(), properties, new String[] {});
        this.interpreter = new PythonInterpreter();
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
        ensureInitialised();
        String moduleName = ScriptManager.getName(className);
        String klassName = moduleName.substring(0, 1).toUpperCase() + moduleName.substring(1);

        this.interpreter.exec("from " + moduleName + " import " + klassName);
        PyObject jythonClass = this.interpreter.get(klassName);
        this.classes.put(className.name, jythonClass);
    }

    @Override
    public String getExtension()
    {
        return "py";
    }

    @Override
    public boolean isInstance( Object inst )
    {
        return (inst instanceof PyObject) || (inst instanceof PyProxy);
    }

    @Override
    public Object getProperty( Object inst, String name ) throws ScriptException
    {
        try {
            this.interpreter.set("__inst", inst);
            Object result = this.interpreter.eval("__inst." + name);
            if (result instanceof PyInteger) {
                return ((PyInteger) result).getValue();
            } else if (result instanceof PyFloat) {
                return ((PyFloat) result).getValue();
            } else if (result instanceof PyLong) {
                return ((PyLong) result).getValue();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void putProperty( Object inst, String name, Object value ) throws ScriptException
    {
        try {
            this.interpreter.set("__inst", inst);
            this.interpreter.set("__value", value);
            this.interpreter.exec("__inst." + name + " = __value" );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void ensureGlobals()
        throws ScriptException
    {
        ensureInitialised();
        Game game = this.manager.resources.getGame();
        this.interpreter.set("game", game);
        this.interpreter.set("director", game.getDirector());
        this.interpreter.set("sceneDirector", game.getSceneDirector());
    }

    private PyObject getClass( ClassName className )
    {
        return this.classes.get(className.name);
    }

    @Override
    public Director createDirector( ClassName className )
    {
        try {
            ensureGlobals();
            this.manager.loadScript(className);
            PyObject instance = getClass(className).__call__();
            return (Director) instance.__tojava__(Director.class);

        } catch (Exception e) {
            handleException("Creating Director", e);
            return new PlainDirector();
        }
    }

    @Override
    public Role createRole( ClassName className )
    {
        try {
            ensureGlobals();
            this.manager.loadScript(className);
            PyObject instance = getClass(className).__call__();
            return (Role) instance.__tojava__(Role.class);

        } catch (Exception e) {
            handleException("Creating Role", e);
            return new PlainRole();
        }
    }

    @Override
    public SceneDirector createSceneDirector( ClassName className )
    {
        try {
            ensureGlobals();
            this.manager.loadScript(className);
            PyObject instance = getClass(className).__call__();
            return (SceneDirector) instance.__tojava__(SceneDirector.class);

        } catch (Exception e) {
            handleException("Creating SceneDirector", e);
            return new PlainSceneDirector();
        }
    }

    @Override
    public CostumeProperties createCostumeProperties( ClassName className )
    {
        try {
            ensureGlobals();
            this.manager.loadScript(className);
            PyObject instance = getClass(className).__call__();
            return (CostumeProperties) instance.__tojava__(CostumeProperties.class);

        } catch (Exception e) {
            handleException("Creating CostumeProperties", e);
            return new CostumeProperties();
        }
    }

}
