/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

import org.python.core.PyBoolean;
import org.python.core.PyException;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PyProxy;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import uk.co.nickthecoder.itchy.util.ClassName;

public class PythonLanguage extends ScriptLanguage
{
    PythonInterpreter interpreter;

    private Map<ClassName, PyObject> classes;

    public PythonLanguage( ScriptManager manager )
    {
        super(manager);
        initialise();
    }

    private final void initialise()
    {
    	this.classes = new HashMap<ClassName, PyObject>();
        Properties properties = new Properties();
        String path =
            this.manager.getScriptDirectory().getPath() + ":" +
                this.manager.getIncludeDirectory(this).getPath();

        properties.setProperty("python.path", path);
        PythonInterpreter.initialize(System.getProperties(), properties, new String[] {});
        this.interpreter = new PythonInterpreter();
        
        this.interpreter.set("game", manager.resources.game);
    }

    @Override
    public String getExtension()
    {
        return "py";
    }

    
    @Override
    public void loadScript( ClassName className, File file )
        throws ScriptException
    {
    	String name = ScriptManager.getName(className);
        String klassName = name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
            this.interpreter.exec("from " + name + " import " + klassName);
            PyObject jythonClass = this.interpreter.get(klassName);
            this.classes.put(className, jythonClass);            
            System.out.println( "Loaded jython script : " + file + " = " + jythonClass.hashCode() );
        } catch (Exception e) {
            throw wrapException(e);
        }
    }

    public void unload()
    {
    	super.unload();
        interpreter.exec("import sys\nsys.modules.clear()");
        initialise();
    }
    
    protected ScriptException wrapException( Exception e )
    {
        if (e instanceof PyException) {
            PyException pe = (PyException) e;
            return new WrappedScriptException( pe, pe.value.toString() );
        }
        
        return super.wrapException( e );
    }
    
    @Override
    public boolean isInstance( Object inst )
    {
        return (inst instanceof PyObject) || (inst instanceof PyProxy);
    }

    @Override
    public Object getAttribute( Object inst, String name ) throws ScriptException
    {
        try {
            this.interpreter.set("__inst", inst);
            Object result = this.interpreter.eval("__inst." + name);
            if (result instanceof PyBoolean) {
                return ((PyBoolean) result).getBooleanValue();
            }
            if (result instanceof PyInteger) {
                return ((PyInteger) result).getValue();
            } else if (result instanceof PyString) {
                return ((PyString) result).toString();
            } else if (result instanceof PyFloat) {
                return ((PyFloat) result).getValue();
            } else if (result instanceof PyLong) {
                return ((PyLong) result).getValue();
            } else if (result instanceof PyNone) {
                return null;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setAttribute( Object inst, String name, Object value ) throws ScriptException
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
    
    protected Object createInstance( ClassName className )
    	throws Exception
    {
    	if (!this.classes.containsKey(className)) {
            this.loadScript(className);    		
    	}
    	
        PyObject instance = this.classes.get(className).__call__();
        return instance.__tojava__(className.baseClass);
    }
}
