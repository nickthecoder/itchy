/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class JavascriptLanguage extends ScriptLanguage
{
    public JavascriptLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    public String getExtension()
    {
        return "js";
    }

    @Override
    public void initialise()
    {
        this.engine = new ScriptEngineManager().getEngineByName("javascript");
    }


    @Override
    public Object getProperty( Object inst, String name ) throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("inst", inst);
        return this.engine.eval("inst." + name + ";");
    }

    @Override
    public Object putProperty( Object inst, String name, Object value ) throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("inst", inst);
        bindings.put("value", value);
        return this.engine.eval("inst." + name + " = value;");
    }

    
    
    @Override
    public Behaviour createBehaviour( String filename )
        throws ScriptException
    {
        String name = this.manager.getName(filename);

        Object scriptBehvaiour = this.engine.eval("new " + name + "();");

        ScriptedBehaviour javaBehaviour = new ScriptedBehaviour(filename, this, scriptBehvaiour);

        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", scriptBehvaiour);
        bindings.put("javaBehaviour", javaBehaviour);
        this.engine.eval("scriptBehaviour.owner = javaBehaviour;");

        return javaBehaviour;
    }

    @Override
    public void onAttach( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("javaActor", behaviour.getActor());
        this.engine.eval("scriptBehaviour.actor = javaActor; scriptBehaviour.onAttach();");

    }

    @Override
    public void onDetach( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onDetach(); scriptBehaviour.actor = null;");
    }

    @Override
    public void onActivate( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onActivate();");
    }

    @Override
    public void onDeactivate( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onDeactivate();");
    }

    @Override
    public void tick( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.tick();");
    }

    @Override
    public void onMessage( ScriptedBehaviour behaviour, String message )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("message", message);
        this.engine.eval("scriptBehaviour.onMessage(message);");

    }

    @Override
    public void onKill( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onKill();");
    }

    @Override
    public SceneBehaviour createSceneBehaviour( String filename )
        throws ScriptException
    {
        String name = this.manager.getName(filename);

        Object scriptBehvaiour = this.engine.eval("new " + name + "();");

        ScriptedSceneBehaviour javaBehaviour = new ScriptedSceneBehaviour(filename, this,
            scriptBehvaiour);

        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", scriptBehvaiour);
        bindings.put("javaBehaviour", javaBehaviour);
        this.engine.eval("scriptBehaviour.behaviour = javaBehaviour;");

        return javaBehaviour;
    }

    @Override
    public void onActivate( ScriptedSceneBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onActivate();");
    }

    @Override
    public void onDeactivate( ScriptedSceneBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.onDeactivate();");
    }

    @Override
    public void tick( ScriptedSceneBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        this.engine.eval("scriptBehaviour.tick();");
    }

    @Override
    public boolean onMouseDown( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", mbe);
        return (boolean) this.engine.eval("scriptBehaviour.onMouseDown(arg);");
    }

    @Override
    public boolean onMouseUp( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", mbe);
        return (boolean) this.engine.eval("scriptBehaviour.onMouseUp(arg);");
    }

    @Override
    public boolean onMouseMove( ScriptedSceneBehaviour behaviour, MouseMotionEvent mme )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", mme);
        return (boolean) this.engine.eval("scriptBehaviour.onMouseMove(arg);");
    }

    @Override
    public boolean onKeyDown( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", ke);
        return (boolean) this.engine.eval("scriptBehaviour.onKeyDown(arg);");
    }

    @Override
    public boolean onKeyUp( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", ke);
        return (boolean) this.engine.eval("scriptBehaviour.onKeyUp(arg);");
    }

    @Override
    public void onMessage( ScriptedSceneBehaviour behaviour, String message )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("scriptBehaviour", behaviour.scriptBehaviour);
        bindings.put("arg", message);
        this.engine.eval("scriptBehaviour.onMessage(arg);");
    }

}
