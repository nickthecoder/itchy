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

import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.NullRole;
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

    // ===== Role ======

    @Override
    public Role createRole( ClassName className )
    {
        ensureGlobals();

        ScriptedRole role = null;

        try {
            String name = ScriptManager.getName(className);
            this.manager.loadScript(className.name);

            Object roleScript = this.engine.eval("new " + name + "();");

            role = new ScriptedRole(className, this, roleScript);

            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", roleScript);
            bindings.put("role", role);
            this.engine.eval("roleScript.role = role;");

        } catch (ScriptException e) {
            handleException("creating Role " + className.name, e);
        }

        if (role == null) {
            log("Using NullRole instead.");
            return new NullRole();
        }
        return role;
    }

    @Override
    public void onBirth( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("javaActor", role.getActor());
            this.engine.eval("roleScript.actor = javaActor; roleScript.onBirth();");

        } catch (ScriptException e) {
            handleException("Role.onBirth", e);
        }

    }

    @Override
    public void onDeath( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            this.engine.eval("roleScript.onDeath();");

        } catch (ScriptException e) {
            handleException("Role.onDeath", e);
        }
    }

    @Override
    public void tick( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            this.engine.eval("roleScript.tick();");

        } catch (ScriptException e) {
            handleException("Role.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedRole role, MouseListenerView view, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("arg1", event);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("roleScript.onMouseDown(arg1,arg2);"));

        } catch (ScriptException e) {
            handleException("Role.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseUp( ScriptedRole role,  MouseListenerView view, MouseButtonEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("arg1", view);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("roleScript.onMouseUp(arg1,arg2);"));

        } catch (ScriptException e) {
            handleException("Role.onMessage", e);
            return false;
        }
    }

    @Override
    public boolean onMouseMove( ScriptedRole role,  MouseListenerView view, MouseMotionEvent event )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("arg1", view);
            bindings.put("arg2", event);
            return this.eventResult(this.engine.eval("roleScript.onMouseMove(arg1,arg2);"));

        } catch (ScriptException e) {
            handleException("Role.onMessage", e);
            return false;
        }
    }

    @Override
    public void onMessage( ScriptedRole role, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("message", message);
            this.engine.eval("roleScript.onMessage(message);");

        } catch (ScriptException e) {
            handleException("Role.onMessage", e);
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
    public void onDeactivate( ScriptedSceneDirector role )
    {
        try {
            this.engine.eval("sceneDirectorScript.onDeactivate();");

        } catch (ScriptException e) {
            handleException("SceneDirector.onDeactivate", e);
        }
    }

    @Override
    public void tick( ScriptedSceneDirector role )
    {
        try {
            this.engine.eval("sceneDirectorScript.tick();");

        } catch (ScriptException e) {
            handleException("SceneDirector.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedSceneDirector role, MouseButtonEvent mbe )
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
    public boolean onMouseUp( ScriptedSceneDirector role, MouseButtonEvent mbe )
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
    public boolean onMouseMove( ScriptedSceneDirector role, MouseMotionEvent mme )
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
    public boolean onKeyDown( ScriptedSceneDirector role, KeyboardEvent ke )
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
    public boolean onKeyUp( ScriptedSceneDirector role, KeyboardEvent ke )
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
    public void onMessage( ScriptedSceneDirector role, String message )
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
