/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

public class SceneResource extends Loadable
{
    private Scene scene;

    public Resources resources;

    public String name;

    public SceneResource( Resources resources, String name, String filename )
    {
        super(resources.resolveFilename(filename));

        this.resources = resources;
        this.scene = null;
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void rename( String newName )
    {
        this.resources.rename(this, newName);
        this.name = newName;
    }

    public Scene getScene() throws Exception
    {
        if (this.scene == null) {
            SceneReader sceneReader = new SceneReader(this.resources);
            this.scene = sceneReader.load(this.getFilename());
        }
        return this.scene;
    }

    public void setScene( Scene scene )
    {
        this.scene = scene;
    }

    @Override
    protected void actualSave( String filename ) throws Exception
    {
        try {
            this.getScene();
        } catch (Exception e) {
            this.scene = new Scene();
        }

        SceneWriter sceneWriter = new SceneWriter(this);
        sceneWriter.write(filename);
    }

    @Override
    protected void checkSave( String filename ) throws Exception
    {
        SceneReader sceneReader = new SceneReader(this.resources);
        Scene newScene = sceneReader.load(filename);

        List<Scene.SceneLayer> newSceneLayers = newScene.getSceneLayers();
        ensure(newSceneLayers.size() == this.scene.getSceneLayers().size(),
            "Different number of layers");
        
        int i = 0;
        for (Scene.SceneLayer oldSceneLayer : this.scene.getSceneLayers()) {
            Scene.SceneLayer newSceneLayer = newSceneLayers.get(i);
            i++;
            ensure(oldSceneLayer.name, newSceneLayer.name, "Different layer name");

            List<SceneActor> newSceneActors = newSceneLayer.getSceneActors();
            ensure(newSceneActors.size() == oldSceneLayer.getSceneActors().size(), "Different number of actors");
            
            int j = 0;
            for (SceneActor oldSceneActor : oldSceneLayer.getSceneActors()) {
                SceneActor newSceneActor = newSceneActors.get(j);
                j++;
                ensure(oldSceneActor, newSceneActor, "different actor #" + j);
            }

        }
    }

}
