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
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.util.ClassName;
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
        Object result = this.engine.eval("inst." + name + ";");
        return result;
    }

    @Override
    public Object putProperty( Object inst, String name, Object value ) throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("inst", inst);
        bindings.put("value", value);
        return this.engine.eval("inst." + name + " = value;");
    }

    public void ensureGlobals()
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Game game = this.manager.resources.getGame();
        bindings.put("game", game);
        bindings.put("sceneBehaviour", game.getSceneBehaviour());
    }
    
    private boolean eventResult( Object object )
    {
        if (object == null) {
            return false;
        }
        return (boolean) object;
    }

    // ===== GAME ======

    @Override
    public Game createGame( Resources resources, ClassName className )
        throws ScriptException
    {

        String name = ScriptManager.getName(className);

        Object gameScript = this.engine.eval("new " + name + "();");

        ScriptedGame game;
        try {
            game = new ScriptedGame(resources, this, gameScript );
        } catch (Exception e) {
            throw new ScriptException(e);
        }

        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("gameScript", gameScript);
        bindings.put("game", game);
        this.engine.eval("gameScript.game = game;");

        return game;
    }

    @Override
    public String getInitialSceneName( ScriptedGame game )
        throws ScriptException
    {
        return (String) this.engine.eval("gameScript.getInitialSceneName();");
    }

    @Override
    public void onActivate( ScriptedGame game )
        throws ScriptException
    {
        this.engine.eval("gameScript.onActivate();");
    }

    @Override
    public void onDeactivate( ScriptedGame game )
        throws ScriptException
    {
        this.engine.eval("gameScript.onActivate();");
    }

    @Override
    public boolean onQuit( ScriptedGame game )
        throws ScriptException
    {
        return (boolean) this.engine.eval("gameScript.onQuit();");
    }

    @Override
    public boolean onKeyDown( ScriptedGame game, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", ke);

        return eventResult( this.engine.eval("gameScript.onKeyDown( arg );") );
    }

    @Override
    public boolean onKeyUp( ScriptedGame game, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", ke);

        return (boolean) this.engine.eval("gameScript.onKeyUp( arg );");
    }

    @Override
    public boolean onMouseDown( ScriptedGame game, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mbe);

        return (boolean) this.engine.eval("gameScript.onMouseDown( arg );");
    }

    @Override
    public boolean onMouseUp( ScriptedGame game, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mbe);

        return (boolean) this.engine.eval("gameScript.onMouseUp( arg );");
    }

    @Override
    public boolean onMouseMove( ScriptedGame game, MouseMotionEvent mme )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mme);

        return (boolean) this.engine.eval("gameScript.onMouseMove( arg );");
    }

    @Override
    public void onMessage( ScriptedGame game, String message )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", message);

        this.engine.eval("gameScript.onMessage( arg );");
    }

    @Override
    public void tick( ScriptedGame game )
        throws ScriptException
    {
        this.engine.eval("gameScript.tick();");
    }

    // ===== Behaviour ======

    @Override
    public Behaviour createBehaviour( ClassName className )
        throws ScriptException
    {
        ensureGlobals();

        String name = ScriptManager.getName(className);
        this.manager.loadScript(className.name);

        Object behaviourScript = this.engine.eval("new " + name + "();");

        ScriptedBehaviour behaviour = new ScriptedBehaviour(className, this, behaviourScript);

        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviourScript);
        bindings.put("behaviour", behaviour);
        this.engine.eval("behaviourScript.behaviour = behaviour;");

        return behaviour;
    }

    @Override
    public void onAttach( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        bindings.put("javaActor", behaviour.getActor());
        this.engine.eval("behaviourScript.actor = javaActor; behaviourScript.onAttach();");

    }

    @Override
    public void onDetach( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        this.engine.eval("behaviourScript.onDetach(); behaviourScript.actor = null;");
    }

    @Override
    public void onActivate( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        this.engine.eval("behaviourScript.onActivate();");
    }

    @Override
    public void onDeactivate( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        this.engine.eval("behaviourScript.onDeactivate();");
    }

    @Override
    public void tick( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        this.engine.eval("behaviourScript.tick();");
    }

    @Override
    public void onMessage( ScriptedBehaviour behaviour, String message )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        bindings.put("message", message);
        this.engine.eval("behaviourScript.onMessage(message);");

    }

    @Override
    public void onKill( ScriptedBehaviour behaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("behaviourScript", behaviour.behaviourScript);
        this.engine.eval("behaviourScript.onKill();");
    }

    // ===== SceneBehaviour ======

    @Override
    public SceneBehaviour createSceneBehaviour( ClassName className )
        throws ScriptException
    {
        ensureGlobals();

        String name = ScriptManager.getName(className);

        Object sceneBehaviourScript = this.engine.eval("new " + name + "();");

        ScriptedSceneBehaviour sceneBehaviour = new ScriptedSceneBehaviour(className, this,
            sceneBehaviourScript);

        return sceneBehaviour;
    }

    @Override
    public void onActivate( ScriptedSceneBehaviour sceneBehaviour )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("sceneBehaviourScript", sceneBehaviour.sceneBehaviourScript);
        bindings.put("sceneBehaviour", sceneBehaviour);
        this.engine.eval("sceneBehaviourScript.sceneBehaviour = sceneBehaviour;");

        this.engine.eval("sceneBehaviourScript.onActivate();");
    }

    @Override
    public void onDeactivate( ScriptedSceneBehaviour behaviour )
        throws ScriptException
    {
        this.engine.eval("sceneBehaviourScript.onDeactivate();");
    }

    @Override
    public void tick( ScriptedSceneBehaviour behaviour )
        throws ScriptException
    {
        this.engine.eval("sceneBehaviourScript.tick();");
    }

    @Override
    public boolean onMouseDown( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mbe);
        return (boolean) this.engine.eval("sceneBehaviourScript.onMouseDown(arg);");
    }

    @Override
    public boolean onMouseUp( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mbe);
        return (boolean) this.engine.eval("sceneBehaviourScript.onMouseUp(arg);");
    }

    @Override
    public boolean onMouseMove( ScriptedSceneBehaviour behaviour, MouseMotionEvent mme )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", mme);
        return (boolean) this.engine.eval("sceneBehaviourScript.onMouseMove(arg);");
    }

    @Override
    public boolean onKeyDown( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", ke);
        return (boolean) this.engine.eval("sceneBehaviourScript.onKeyDown(arg);");
    }

    @Override
    public boolean onKeyUp( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", ke);
        return (boolean) this.engine.eval("sceneBehaviourScript.onKeyUp(arg);");
    }

    @Override
    public void onMessage( ScriptedSceneBehaviour behaviour, String message )
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("arg", message);
        this.engine.eval("sceneBehaviourScript.onMessage(arg);");
    }

    // ===== CostumeProperties ======

    @Override
    public CostumeProperties createCostumeProperties( ClassName className ) throws ScriptException
    {
        ensureGlobals();

        String name = ScriptManager.getName(className);

        Object costumePropertiesScript = this.engine.eval("new " + name + "();");

        ScriptedCostumeProperties costumeProperties = new ScriptedCostumeProperties(className, this,
            costumePropertiesScript);

        return costumeProperties;
    }

}
