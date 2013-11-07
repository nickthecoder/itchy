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
import uk.co.nickthecoder.itchy.GameManager;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.NullBehaviour;
import uk.co.nickthecoder.itchy.NullSceneBehaviour;
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
    public String simpleMessage( ScriptException e, boolean includeFilename )
    {
        String message = e.getMessage();
        int index = message.indexOf("Exception: ");
        if (index >= 0) {
            message = message.substring(index + 11);
        }

        if (!includeFilename) {
            String pattern = "(.*)\\(.*\\#([0-9]*)\\)";
            message = message.replaceAll(pattern, "Line $2. $1");
        }

        return message;
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
        Game game = Itchy.getGame();
        bindings.put("game", game);
        bindings.put("sceneBehaviour", game.getSceneBehaviour());
    }

    public void handleException( ScriptException e )
    {
        this.manager.resources.errorLog.log(e.getMessage());
    }

    public void handleException( String activity, ScriptException e )
    {
        this.manager.resources.errorLog.log(activity + " : " + e.getMessage());
    }

    public void log( String message )
    {
        this.manager.resources.errorLog.log(message);
    }

    public boolean eventResult( Object result )
    {
        try {
            return (boolean) result;
        } catch (Exception e) {
            return false;
        }
    }

    // ===== GAME ======

    @Override
    public Game createGame( GameManager gameManager, ClassName className )
    {
        String name = ScriptManager.getName(className);
        ScriptedGame game = null;

        try {
            Object gameScript = this.engine.eval("new " + name + "();");

            game = new ScriptedGame(gameManager, this, gameScript);

            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("gameScript", gameScript);
            bindings.put("game", game);
            this.engine.eval("gameScript.game = game;");

        } catch (ScriptException e) {
            handleException("Creating Game", e);
        }

        if (game == null) {
            log("Game not created. Using a default Game instead.");
            return new Game(gameManager);
        }
        return game;
    }

    @Override
    public void onActivate( ScriptedGame game )
    {
        try {
            this.engine.eval("gameScript.onActivate();");
        } catch (ScriptException e) {
            handleException("Game.onActivate", e);
        }
    }

    @Override
    public void onDeactivate( ScriptedGame game )
    {
        try {
            this.engine.eval("gameScript.onDeativate();");
        } catch (ScriptException e) {
            handleException("Game.onDeactivate", e);
        }
    }

    @Override
    public boolean onQuit( ScriptedGame game )
    {
        try {
            return eventResult(this.engine.eval("gameScript.onQuit();"));

        } catch (ScriptException e) {
            handleException("Game.onQuit", e);
            Itchy.terminate();
            return true;
        }
    }

    @Override
    public boolean onKeyDown( ScriptedGame game, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);

            return eventResult(this.engine.eval("gameScript.onKeyDown( arg );"));

        } catch (ScriptException e) {
            handleException("Game.onKeyDown", e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( ScriptedGame game, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);

            return eventResult(this.engine.eval("gameScript.onKeyUp( arg );"));

        } catch (ScriptException e) {
            handleException("Game.onKeyUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseDown( ScriptedGame game, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);

            return eventResult(this.engine.eval("gameScript.onMouseDown( arg );"));

        } catch (ScriptException e) {
            handleException("Game.onMouseDown", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedGame game, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);

            return eventResult(this.engine.eval("gameScript.onMouseUp( arg );"));

        } catch (ScriptException e) {
            handleException("Game.onMouseUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedGame game, MouseMotionEvent mme )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mme);

            return eventResult(this.engine.eval("gameScript.onMouseMove( arg );"));

        } catch (ScriptException e) {
            handleException("Game.onMouseUp", e);
            return false;
        }
    }

    @Override
    public void onMessage( ScriptedGame game, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", message);

            this.engine.eval("gameScript.onMessage( arg );");

        } catch (ScriptException e) {
            handleException("Game.onMessage", e);
        }
    }

    @Override
    public void tick( ScriptedGame game )
    {
        try {
            this.engine.eval("gameScript.tick();");

        } catch (ScriptException e) {
            handleException("Game.tick", e);
        }
    }

    // ===== Behaviour ======

    @Override
    public Behaviour createBehaviour( ClassName className )
    {
        ensureGlobals();

        ScriptedBehaviour behaviour = null;

        try {
            String name = ScriptManager.getName(className);
            this.manager.loadScript(className.name);

            Object behaviourScript = this.engine.eval("new " + name + "();");

            behaviour = new ScriptedBehaviour(className, this, behaviourScript);

            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviourScript);
            bindings.put("behaviour", behaviour);
            this.engine.eval("behaviourScript.behaviour = behaviour;");

        } catch (ScriptException e) {
            handleException("creating Behaviour " + className.name, e);
        }

        if (behaviour == null) {
            log("Using NullBehaviour instead.");
            return new NullBehaviour();
        }
        return behaviour;
    }

    @Override
    public void onAttach( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("javaActor", behaviour.getActor());
            this.engine.eval("behaviourScript.actor = javaActor; behaviourScript.onAttach();");

        } catch (ScriptException e) {
            handleException("Behaviour.onAttach", e);
        }

    }

    @Override
    public void onDetach( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.onDetach(); behaviourScript.actor = null;");

        } catch (ScriptException e) {
            handleException("Behaviour.onDetach", e);
        }
    }

    @Override
    public void onActivate( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.onActivate();");

        } catch (ScriptException e) {
            handleException("Behaviour.onActivate", e);
        }
    }

    @Override
    public void onDeactivate( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.onDeactivate();");

        } catch (ScriptException e) {
            handleException("Behaviour.onDeactivate", e);
        }
    }

    @Override
    public void tick( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.tick();");

        } catch (ScriptException e) {
            handleException("Behaviour.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedBehaviour behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg", mbe);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseDown(arg);"));

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedBehaviour behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg", mbe);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseUp(arg);"));

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedBehaviour behaviour, MouseMotionEvent mme )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg", mme);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseMove(arg);"));

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
            return false;
        }
    }
    
    @Override
    public void onMessage( ScriptedBehaviour behaviour, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("message", message);
            this.engine.eval("behaviourScript.onMessage(message);");

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
        }
    }

    @Override
    public void onKill( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.onKill();");

        } catch (ScriptException e) {
            handleException("Behaviour.onKill", e);
        }
    }

    // ===== SceneBehaviour ======

    @Override
    public SceneBehaviour createSceneBehaviour( ClassName className )
    {
        ScriptedSceneBehaviour sceneBehaviour = null;

        try {
            ensureGlobals();

            String name = ScriptManager.getName(className);
            Object sceneBehaviourScript = this.engine.eval("new " + name + "();");
            sceneBehaviour = new ScriptedSceneBehaviour(className, this, sceneBehaviourScript);

        } catch (ScriptException e) {
            handleException("creating SceneBehaviour " + className.name, e);
        }

        if (sceneBehaviour == null) {
            log("Using NullSceneBehaviour instead.");
            return new NullSceneBehaviour();
        }
        return sceneBehaviour;
    }

    @Override
    public void onActivate( ScriptedSceneBehaviour sceneBehaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("sceneBehaviourScript", sceneBehaviour.sceneBehaviourScript);
            bindings.put("sceneBehaviour", sceneBehaviour);
            this.engine.eval("sceneBehaviourScript.sceneBehaviour = sceneBehaviour;");

            this.engine.eval("sceneBehaviourScript.onActivate();");

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onActivate", e);
        }
    }

    @Override
    public void onDeactivate( ScriptedSceneBehaviour behaviour )
    {
        try {
            this.engine.eval("sceneBehaviourScript.onDeactivate();");

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onDeactivate", e);
        }
    }

    @Override
    public void tick( ScriptedSceneBehaviour behaviour )
    {
        try {
            this.engine.eval("sceneBehaviourScript.tick();");

        } catch (ScriptException e) {
            handleException("SceneBehaviour.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);
            return eventResult(this.engine.eval("sceneBehaviourScript.onMouseDown(arg);"));

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onMouseDown", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedSceneBehaviour behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);
            return eventResult(this.engine.eval("sceneBehaviourScript.onMouseUp(arg);"));

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onMouseUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedSceneBehaviour behaviour, MouseMotionEvent mme )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mme);
            return eventResult(this.engine.eval("sceneBehaviourScript.onMouseMove(arg);"));

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onMouseMove", e);
            return false;
        }
    }

    @Override
    public boolean onKeyDown( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);
            return eventResult(this.engine.eval("sceneBehaviourScript.onKeyDown(arg);"));

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onKeyDown", e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( ScriptedSceneBehaviour behaviour, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);
            return eventResult(this.engine.eval("sceneBehaviourScript.onKeyUp(arg);"));

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onKeyUp", e);
            return false;
        }
    }

    @Override
    public void onMessage( ScriptedSceneBehaviour behaviour, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", message);
            this.engine.eval("sceneBehaviourScript.onMessage(arg);");

        } catch (ScriptException e) {
            handleException("SceneBehaviour.onMessage", e);
        }
    }

    // ===== CostumeProperties ======

    @Override
    public CostumeProperties createCostumeProperties( ClassName className )
    {
        ScriptedCostumeProperties costumeProperties = null;
        try {
            ensureGlobals();

            String name = ScriptManager.getName(className);

            Object costumePropertiesScript = this.engine.eval("new " + name + "();");

            costumeProperties = new ScriptedCostumeProperties(className,
                this,
                costumePropertiesScript);

        } catch (ScriptException e) {
            e.printStackTrace();
            handleException("Creating costume properties : " + className.name, e);
        }

        if (costumeProperties == null) {
            return new CostumeProperties();
        }
        return costumeProperties;
    }


}
