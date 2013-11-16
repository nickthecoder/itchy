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
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.NullBehaviour;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.SceneDirector;
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
        bindings.put("director", game.getDirector());
        bindings.put("sceneDirector", game.getSceneDirector());
    }

    public void handleException( Exception e )
    {
        this.manager.resources.errorLog.log(e.getMessage());
    }

    public void handleException( String activity, Exception e )
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

    // ===== DIRECTOR ======

    @Override
    public Director createDirector( ClassName className )
    {
        ensureGlobals();

        String name = ScriptManager.getName(className);
        ScriptedDirector director = null;

        try {
            Object directorScript = this.engine.eval("new " + name + "();");

            director = new ScriptedDirector(this, directorScript);

            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("directorScript", directorScript);
            bindings.put("director", director);
            this.engine.eval("directorScript.director = director;");

        } catch (ScriptException e) {
            handleException("Creating Director", e);
        }

        if (director == null) {
            log("Game not created. Using a PlainDirector instead.");
            return new PlainDirector();
        }
        return director;
    }

    @Override
    public void onStarted( ScriptedDirector director )
    {
        try {
            this.engine.eval("directorScript.onStarted();");
        } catch (ScriptException e) {
            handleException("Director.onStarted", e);
        }
    }
    
    @Override
    public void onActivate( ScriptedDirector director )
    {
        try {
            this.engine.eval("directorScript.onActivate();");
        } catch (ScriptException e) {
            handleException("Director.onActivate", e);
        }
    }

    @Override
    public void onDeactivate( ScriptedDirector director )
    {
        try {
            this.engine.eval("directorScript.onDeactivate();");
        } catch (ScriptException e) {
            handleException("Director.onDeactivate", e);
        }
    }

    @Override
    public boolean onQuit( ScriptedDirector director )
    {
        try {
            return eventResult(this.engine.eval("directorScript.onQuit();"));

        } catch (ScriptException e) {
            handleException("Director.onQuit", e);
            Itchy.terminate();
            return true;
        }
    }

    @Override
    public boolean onKeyDown( ScriptedDirector director, KeyboardEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", event);

            return eventResult(this.engine.eval("directorScript.onKeyDown( arg );"));

        } catch (ScriptException e) {
            handleException("Director.onKeyDown", e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( ScriptedDirector director, KeyboardEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", event);

            return eventResult(this.engine.eval("directorScript.onKeyUp( arg );"));

        } catch (ScriptException e) {
            handleException("Director.onKeyUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseDown( ScriptedDirector director, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", event);

            return eventResult(this.engine.eval("directorScript.onMouseDown( arg );"));

        } catch (ScriptException e) {
            handleException("Director.onMouseDown", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedDirector director, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", event);

            return eventResult(this.engine.eval("directorScript.onMouseUp( arg );"));

        } catch (ScriptException e) {
            handleException("Director.onMouseUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedDirector director, MouseMotionEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", event);

            return eventResult(this.engine.eval("directorScript.onMouseMove( arg );"));

        } catch (ScriptException e) {
            handleException("Director.onMouseUp", e);
            return false;
        }
    }

    @Override
    public void onMessage( ScriptedDirector director, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", message);

            this.engine.eval("directorScript.onMessage( arg );");

        } catch (ScriptException e) {
            handleException("Director.onMessage", e);
        }
    }

    @Override
    public void tick( ScriptedDirector director )
    {
        try {
            this.engine.eval("directorScript.tick();");

        } catch (ScriptException e) {
            handleException("Director.tick", e);
        }
    }

    @Override
    public boolean startScene( ScriptedDirector director, String sceneName )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", sceneName);
            return (boolean) this.engine.eval("directorScript.startScene(arg);");

        } catch (Exception e) {
            handleException("Director.startScene", e);
            return false;
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
    public void onBirth( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("javaActor", behaviour.getActor());
            this.engine.eval("behaviourScript.actor = javaActor; behaviourScript.onBirth();");

        } catch (ScriptException e) {
            handleException("Behaviour.onBirth", e);
        }

    }

    @Override
    public void onDeath( ScriptedBehaviour behaviour )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            this.engine.eval("behaviourScript.onDeath();");

        } catch (ScriptException e) {
            handleException("Behaviour.onDeath", e);
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
    public boolean onMouseDown( ScriptedBehaviour behaviour, MouseListenerView view, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg1", event);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseDown(arg1,arg2);"));

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedBehaviour behaviour,  MouseListenerView view, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg1", view);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseUp(arg1,arg2);"));

        } catch (ScriptException e) {
            handleException("Behaviour.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedBehaviour behaviour,  MouseListenerView view, MouseMotionEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("behaviourScript", behaviour.behaviourScript);
            bindings.put("arg1", view);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("behaviourScript.onMouseMove(arg1,arg2);"));

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

    // ===== SceneDirector ======

    @Override
    public SceneDirector createSceneDirector( ClassName className )
    {
        ScriptedSceneDirector sceneDirector = null;

        try {
            ensureGlobals();

            String name = ScriptManager.getName(className);
            Object sceneDirectorScript = this.engine.eval("new " + name + "();");
            sceneDirector = new ScriptedSceneDirector(className, this, sceneDirectorScript);

        } catch (ScriptException e) {
            handleException("creating SceneDirector " + className.name, e);
        }

        if (sceneDirector == null) {
            log("Using PlainSceneDirector instead.");
            return new PlainSceneDirector();
        }
        return sceneDirector;
    }

    @Override
    public void onActivate( ScriptedSceneDirector sceneDirector )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("sceneDirectorScript", sceneDirector.sceneDirectorScript);
            bindings.put("sceneDirector", sceneDirector);
            this.engine.eval("sceneDirectorScript.sceneDirector = sceneDirector;");

            this.engine.eval("sceneDirectorScript.onActivate();");

        } catch (ScriptException e) {
            handleException("SceneDirector.onActivate", e);
        }
    }

    @Override
    public void onDeactivate( ScriptedSceneDirector behaviour )
    {
        try {
            this.engine.eval("sceneDirectorScript.onDeactivate();");

        } catch (ScriptException e) {
            handleException("SceneDirector.onDeactivate", e);
        }
    }

    @Override
    public void tick( ScriptedSceneDirector behaviour )
    {
        try {
            this.engine.eval("sceneDirectorScript.tick();");

        } catch (ScriptException e) {
            handleException("SceneDirector.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedSceneDirector behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);
            return eventResult(this.engine.eval("sceneDirectorScript.onMouseDown(arg);"));

        } catch (ScriptException e) {
            handleException("SceneDirector.onMouseDown", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedSceneDirector behaviour, MouseButtonEvent mbe )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mbe);
            return eventResult(this.engine.eval("sceneDirectorScript.onMouseUp(arg);"));

        } catch (ScriptException e) {
            handleException("SceneDirector.onMouseUp", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedSceneDirector behaviour, MouseMotionEvent mme )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", mme);
            return eventResult(this.engine.eval("sceneDirectorScript.onMouseMove(arg);"));

        } catch (ScriptException e) {
            handleException("SceneDirector.onMouseMove", e);
            return false;
        }
    }

    @Override
    public boolean onKeyDown( ScriptedSceneDirector behaviour, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);
            return eventResult(this.engine.eval("sceneDirectorScript.onKeyDown(arg);"));

        } catch (ScriptException e) {
            handleException("SceneDirector.onKeyDown", e);
            return false;
        }
    }

    @Override
    public boolean onKeyUp( ScriptedSceneDirector behaviour, KeyboardEvent ke )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", ke);
            return eventResult(this.engine.eval("sceneDirectorScript.onKeyUp(arg);"));

        } catch (ScriptException e) {
            handleException("SceneDirector.onKeyUp", e);
            return false;
        }
    }

    @Override
    public void onMessage( ScriptedSceneDirector behaviour, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", message);
            this.engine.eval("sceneDirectorScript.onMessage(arg);");

        } catch (ScriptException e) {
            handleException("SceneDirector.onMessage", e);
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
