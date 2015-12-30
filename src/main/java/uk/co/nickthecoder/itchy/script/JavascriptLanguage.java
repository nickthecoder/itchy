/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.PlainSceneDirector;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.collision.BruteForceCollisionStrategy;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class JavascriptLanguage extends ShimmedScriptLanguage
{
    public JavascriptLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    public void reload()
    {
        // TODO Reload ???
    }
    
    @Override
    public String getExtension()
    {
        return "js";
    }

    @Override
    public ScriptEngine createEngine()
    {
        return new ScriptEngineManager().getEngineByName("javascript");
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

    public void ensureGlobals()
        throws ScriptException
    {
        Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
        Game game = this.manager.resources.getGame();
        bindings.put("game", game);
        bindings.put("director", game.getDirector());
        bindings.put("sceneDirector", game.getSceneDirector());
        bindings.put("language", this);
    }

    @Override
    public Object getAttribute( Object wrapper, String name ) throws ScriptException
    {
        try {
            Object inst = ((ScriptedObject) wrapper).getScriptedObject();
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("inst", inst);
            Object result = this.engine.eval("inst." + name + ";");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setAttribute( Object wrapper, String name, Object value ) throws ScriptException
    {
        try {
            Object inst = ((ScriptedObject) wrapper).getScriptedObject();
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("inst", inst);
            bindings.put("value", value);
            this.engine.eval("inst." + name + " = value;");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    // ===== DIRECTOR ======

    @Override
    public Director createDirector( ClassName className )
    {

        ScriptedDirector director = null;
        
        try {
            ensureGlobals();
            this.manager.loadScript(className);

            String name = ScriptManager.getName(className);

            Object directorScript = this.engine.eval("new " + name + "();");

            director = new ScriptedDirector(className, this, directorScript);

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

    public Role createRole( String name )
    {
        return this.createRole( new ClassName( Role.class, name + ".js") );
    }

    public Role createRole( ClassName className )
    {
        ScriptedRole role = null;

        try {
            ensureGlobals();
            String name = ScriptManager.getName(className);
            this.manager.loadScript(className);

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
            log("Using PlainRole instead.");
            return new PlainRole();
        }
        return role;
    }

    @SuppressWarnings("unchecked")
    public List<AbstractProperty<Role,?>> getProperties( ScriptedRole scriptedRole )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", scriptedRole.roleScript);
            return (List<AbstractProperty<Role,?>>) this.engine.eval("roleScript.getProperties();");
        } catch (ScriptException e) {
            handleException("Director.onStarted", e);
            return new ArrayList<AbstractProperty<Role,?>>();
        }
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
    public void onAttach( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("javaActor", role.getActor());
            this.engine.eval("roleScript.actor = javaActor; roleScript.onAttach();");

        } catch (ScriptException e) {
            handleException("Role.onAttach", e);
        }

    }

    @Override
    public void onDetach( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            bindings.put("javaActor", role.getActor());
            this.engine.eval("roleScript.actor = javaActor; roleScript.onDetach();");

        } catch (ScriptException e) {
            handleException("Role.onDetach", e);
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
    public boolean onMouseUp( ScriptedRole role, MouseListenerView view, MouseButtonEvent event )
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
    public boolean onMouseMove( ScriptedRole role, MouseListenerView view, MouseMotionEvent event )
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
    public boolean isMouseListener( ScriptedRole role )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("roleScript", role.roleScript);
            return (boolean) this.engine.eval("roleScript.isMouseListener();");

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
            this.manager.loadScript(className);

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

    @SuppressWarnings("unchecked")
    public List<AbstractProperty<SceneDirector,?>> getProperties( ScriptedSceneDirector sceneDirector )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("sceneDirectorScript", sceneDirector.sceneDirectorScript);
            return (List<AbstractProperty<SceneDirector,?>>) this.engine.eval("sceneDirectorScript.getProperties();");
        } catch (ScriptException e) {
            handleException("SceneDirectory.getProperties", e);
            return new ArrayList<AbstractProperty<SceneDirector,?>>();
        }
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
    public void onDeactivate( ScriptedSceneDirector sceneDirector )
    {
        try {
            this.engine.eval("sceneDirectorScript.onDeactivate();");

        } catch (ScriptException e) {
            handleException("SceneDirector.onDeactivate", e);
        }
    }

    @Override
    public void tick( ScriptedSceneDirector sceneDirector )
    {
        try {
            this.engine.eval("sceneDirectorScript.tick();");

        } catch (ScriptException e) {
            handleException("SceneDirector.tick", e);
        }
    }

    @Override
    public boolean onMouseDown( ScriptedSceneDirector sceneDirector, MouseButtonEvent mbe )
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
    public boolean onMouseUp( ScriptedSceneDirector sceneDirector, MouseButtonEvent mbe )
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
    public boolean onMouseMove( ScriptedSceneDirector sceneDirector, MouseMotionEvent mme )
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
    public boolean onKeyDown( ScriptedSceneDirector sceneDirector, KeyboardEvent ke )
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
    public boolean onKeyUp( ScriptedSceneDirector sceneDirector, KeyboardEvent ke )
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
    public void onMessage( ScriptedSceneDirector sceneDirector, String message )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", message);
            this.engine.eval("sceneDirectorScript.onMessage(arg);");

        } catch (ScriptException e) {
            handleException("SceneDirector.onMessage", e);
        }
    }
    
    @Override
    public CollisionStrategy getCollisionStrategy( ScriptedSceneDirector sceneDirector, Actor actor )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("arg", actor);
            return (CollisionStrategy) this.engine.eval("sceneDirectorScript.getCollisionStrategy(arg);");

        } catch (ScriptException e) {
            handleException("SceneDirector.getCollisionStrategy", e);
            return BruteForceCollisionStrategy.pixelCollision;
        }
    }

    // ===== CostumeProperties ======

    @Override
    public CostumeProperties createCostumeProperties( ClassName className )
    {
        ScriptedCostumeProperties costumeProperties = null;
        try {
            ensureGlobals();
            this.manager.loadScript(className);

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

    @SuppressWarnings("unchecked")
    public List<AbstractProperty<CostumeProperties,?>> getProperties( ScriptedCostumeProperties costumeProperties )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("costumePropertiesScript", costumeProperties.costumePropertiesScript);
            return (List<AbstractProperty<CostumeProperties,?>>) this.engine.eval("costumePropertiesScript.getProperties();");
        } catch (ScriptException e) {
            handleException("CostumeProperties.getProperties", e);
            return new ArrayList<AbstractProperty<CostumeProperties,?>>();
        }
    }


    // ===== StageConstraint ======
    
    @Override
    public double constrainX( ScriptedStageConstraint stageConstraint, double x, double y )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("ssc", stageConstraint);
            bindings.put("x", x);
            bindings.put("y", x);
            return (Double) this.engine.eval("ssc.onconstrainX(x,y);");

        } catch (ScriptException e) {
            handleException("StageConstraint.constrainX", e);
            return x;
        } 
    }

    @Override
    public double constrainY( ScriptedStageConstraint stageConstraint, double x, double y )
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("ssc", stageConstraint);
            bindings.put("x", x);
            bindings.put("y", x);
            return (Double) this.engine.eval("ssc.onconstrainY(x,y);");

        } catch (ScriptException e) {
            handleException("StageConstraint.constrainX", e);
            return x;
        } 
    }
    
    public void added(ScriptedStageConstraint stageConstraint, Actor actor)
    {
        try {
            Bindings bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("ssc", stageConstraint);
            bindings.put("actor", actor);
            this.engine.eval("ssc.added(actor);");

        } catch (ScriptException e) {
            handleException("StageConstraint.added", e);
        } 
        
    }
}

