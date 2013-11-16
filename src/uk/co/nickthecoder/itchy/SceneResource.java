/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;

public class SceneResource extends Loadable
{
    public static List<AbstractProperty<SceneResource, ?>> properties = AbstractProperty.findAnnotations(SceneResource.class);

    private Scene scene;

    @Property(label = "Name", sortOrder = -1)
    public String name;

    public Resources resources;

    private static File makeFile( String name )
    {
        File file = new File("scenes");
        return new File(file, name + ".xml");
    }

    public SceneResource( Resources resources, String name )
    {
        super(resources.getDirectory(), makeFile(name));

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
        this.resources.renameResource(this, newName);
        this.name = newName;
        this.renameFile(makeFile(newName));
    }

    @Property(label = "Scene", recurse = true)
    public Scene getScene() throws Exception
    {
        if (this.scene == null) {
            if ( this.resolveFile( this.getFile()).exists()) {
                this.load();
            } else {
                this.scene = new Scene();
            }
        }
        return this.scene;
    }

    public void setScene( Scene scene )
    {
        this.scene = scene;
    }

    @Override
    protected void actualSave( File file ) throws Exception
    {
        try {
            this.getScene();
        } catch (Exception e) {
            this.scene = new Scene();
        }
        if (this.scene.sceneDirector == null) {
            this.scene.sceneDirector = this.scene.createSceneDirector(this.resources);
        }

        SceneWriter sceneWriter = new SceneWriter(this);
        sceneWriter.write(file.getPath());
    }

    @Override
    protected void checkSave( File file ) throws Exception
    {
        SceneReader sceneReader = new SceneReader(this.resources);
        Scene newScene = sceneReader.load(file.getPath());

        List<Scene.SceneLayer> newSceneLayers = newScene.getSceneLayers();

        int i = 0;
        for (Scene.SceneLayer oldSceneLayer : this.scene.getSceneLayers()) {
            if (i >= newSceneLayers.size()) {
                if (oldSceneLayer.getSceneActors().size() != 0) {
                    throw new Exception("Layers differ");
                }
                continue;
            }
            Scene.SceneLayer newSceneLayer = newSceneLayers.get(i);
            if (!oldSceneLayer.getName().equals(newSceneLayer.getName())) {
                if (oldSceneLayer.getSceneActors().size() == 0) {
                    continue;
                }
            }
            i++;
            ensure(oldSceneLayer.name, newSceneLayer.name, "Different layer name");

            List<SceneActor> newSceneActors = newSceneLayer.getSceneActors();
            ensure(newSceneActors.size() == oldSceneLayer.getSceneActors().size(),
                "Different number of actors");

            int j = 0;
            for (SceneActor oldSceneActor : oldSceneLayer.getSceneActors()) {
                SceneActor newSceneActor = newSceneActors.get(j);
                j++;
                ensure(oldSceneActor, newSceneActor, "different actor #" + j);
            }

        }
    }

    @Override
    public void load() throws Exception
    {
        SceneReader sceneReader = new SceneReader(this.resources);
        this.scene = sceneReader.load(getFilename());
    }

}
