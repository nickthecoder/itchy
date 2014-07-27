/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.collision.CollisionStrategy;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * For script languages which cannot subclass java classes and interfaces, each scripted object needs to be wrapped by a Java class, which
 * passes each method call to the script. ScriptLanguageShim will be called from ScriptedRole, ScriptedDirector etc, and will call the
 * script.
 */
public abstract class ShimmedScriptLanguage extends StandardScriptLanguage
{
    public ShimmedScriptLanguage( ScriptManager manager )
    {
        super(manager);
    }

    @Override
    public boolean isInstance( Object instance )
    {
        if (instance instanceof ScriptedObject) {
            // BUG ? When in the editor we get a different JavascriptLangauge instances, so we compare extensions rather than
            // getLanguage() == this.
            return ((ScriptedObject) instance).getLanguage().getExtension() == this.getExtension();
        }
        return false;
    }

    // ===== DIRECTOR =====

    public abstract void onStarted( ScriptedDirector director );

    public abstract void onActivate( ScriptedDirector director );

    public abstract void onDeactivate( ScriptedDirector director );

    public abstract boolean onQuit( ScriptedDirector director );

    public abstract boolean onKeyDown( ScriptedDirector director, KeyboardEvent ke );

    public abstract boolean onKeyUp( ScriptedDirector director, KeyboardEvent ke );

    public abstract boolean onMouseDown( ScriptedDirector director, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedDirector director, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedDirector director, MouseMotionEvent mme );

    public abstract void onMessage( ScriptedDirector director, String message );

    public abstract void tick( ScriptedDirector director );

    public abstract boolean startScene( ScriptedDirector director, String sceneName );

    // ===== ROLE =====

    public abstract List<AbstractProperty<Role, ?>> getProperties( ScriptedRole scriptedRole );

    public abstract void onBirth( ScriptedRole role );

    public abstract void onDeath( ScriptedRole role );

    public abstract void onAttach( ScriptedRole role );

    public abstract void onDetach( ScriptedRole role );

    public abstract boolean onMouseDown( ScriptedRole role, MouseListenerView view, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedRole role, MouseListenerView view, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedRole role, MouseListenerView view, MouseMotionEvent mbe );

    public abstract void onMessage( ScriptedRole role, String message );

    public abstract void tick( ScriptedRole role );

    public abstract boolean isMouseListener( ScriptedRole role );

    // ===== SCENE DIRECTOR=====

    public abstract List<AbstractProperty<SceneDirector, ?>> getProperties( ScriptedSceneDirector scriptedSceneDirector );

    public abstract void onLoaded( ScriptedSceneDirector sceneDirector );

    public abstract void onActivate( ScriptedSceneDirector sceneDirector );

    public abstract void onDeactivate( ScriptedSceneDirector sceneDirector );

    public abstract void tick( ScriptedSceneDirector sceneDirector );

    public abstract boolean onMouseDown( ScriptedSceneDirector sceneDirector, MouseButtonEvent mbe );

    public abstract boolean onMouseUp( ScriptedSceneDirector sceneDirector, MouseButtonEvent mbe );

    public abstract boolean onMouseMove( ScriptedSceneDirector sceneDirector, MouseMotionEvent mme );

    public abstract boolean onKeyDown( ScriptedSceneDirector sceneDirector, KeyboardEvent ke );

    public abstract boolean onKeyUp( ScriptedSceneDirector sceneDirector, KeyboardEvent ke );

    public abstract void onMessage( ScriptedSceneDirector sceneDirector, String message );

    public abstract CollisionStrategy getCollisionStrategy( ScriptedSceneDirector sceneDirector, Actor actor );

    // ====== COSTUME PROPERTIES =====

    public abstract List<AbstractProperty<CostumeProperties, ?>> getProperties( ScriptedCostumeProperties scriptedCostumeProperties );

    // ====== STAGE CONSTRAINT ====
    
    public abstract double constrainX( ScriptedStageConstraint stageConstraint, double x, double y );

    public abstract double constrainY( ScriptedStageConstraint stageConstraint, double x, double y );
    
    public abstract void added( ScriptedStageConstraint stageConstraint, Actor actor );

}
