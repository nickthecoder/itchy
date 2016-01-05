/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class SceneResource extends Loadable implements PropertySubject<SceneResource>
{
    protected static final List<AbstractProperty<SceneResource, ?>> properties = new ArrayList<AbstractProperty<SceneResource, ?>>();

    static {
        properties.add(new StringProperty<SceneResource>("name"));
        properties.add(new BooleanProperty<SceneResource>("scene.showMouse"));
        properties.add(new ClassNameProperty<SceneResource>(SceneDirector.class, "scene.sceneDirectorClassName"));
        properties.add(new RGBAProperty<SceneResource>("scene.backgroundColor"));
    }

    private Scene scene;

    public String name;

    public Resources resources;

    private static File makeFile(String name)
    {
        File file = new File("scenes");
        return new File(file, name + ".xml");
    }

    public SceneResource(Resources resources, String name)
    {
        super(resources.getDirectory(), makeFile(name));

        this.resources = resources;
        this.scene = null;
        this.name = name;
    }

    @Override
    public List<AbstractProperty<SceneResource, ?>> getProperties()
    {
        return properties;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean canRenameTo(String newName)
    {
        File file = makeFile(newName);
        File resolvedFile = this.resources.resolveFile(file);
        if (resolvedFile.equals(this.getFile())) {
            return true;
        }
        if (resolvedFile.exists()) {
            return false;
        }
        return true;
    }

    public void rename(String newName) throws Exception
    {
        File file = makeFile(newName);
        File resolvedFile = this.resources.resolveFile(file);
        if (!this.name.equals(newName)) {
            if (resolvedFile.exists()) {
                throw new Exception("File already exists");
            }
            this.renameFile(file);
        }
        this.resources.renameResource(this, newName);
        this.name = newName;
    }

    public Scene getScene() throws Exception
    {
        if (this.scene == null) {
            this.load();
        }
        return this.scene;
    }

    public void unloadScene()
    {
        this.scene = null;
    }

    public Scene loadScene() throws Exception
    {
        load();
        return this.scene;
    }

    @Override
    public void load() throws Exception
    {
        if (this.resolveFile(this.getFile()).exists()) {
            SceneReader sceneReader = new SceneReader(this.resources);
            this.scene = sceneReader.load(getFilename());
        } else {
            this.scene = new Scene();
        }
    }

    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    @Override
    protected void actualSave(File file) throws Exception
    {
        if (this.scene == null) {
            this.load();
        }

        if (this.scene.sceneDirector == null) {
            this.scene.sceneDirector = this.scene.createSceneDirector(this.resources);
        }

        SceneWriter sceneWriter = new SceneWriter(this);
        sceneWriter.write(file.getPath());
    }

    @Override
    protected void checkSave(File file) throws Exception
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
            ensure(oldSceneLayer.name, newSceneLayer.name, "Different stage name");

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
