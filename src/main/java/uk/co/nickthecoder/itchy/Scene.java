/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.LayoutProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.StringUtils;

public class Scene implements NamedSubject<Scene>
{
    protected static final List<Property<Scene, ?>> properties = new ArrayList<Property<Scene, ?>>();

    static {
        properties.add(new StringProperty<Scene>("name"));
        properties.add(new LayoutProperty<Scene>("layout"));
        properties.add(new ClassNameProperty<Scene>(SceneDirector.class, "sceneDirector")
            .access("sceneDirectorClassName").aliases("role"));
        properties.add(new BooleanProperty<Scene>("showMouse"));
    }


    @Override
    public List<Property<Scene, ?>> getProperties()
    {
        return properties;
    }

    public String name = "";

    public boolean showMouse = true;

    private ClassName sceneDirectorClassName;

    private SceneDirector sceneDirector;

    public Layout layout;


    public Scene()
    {
        this.sceneDirectorClassName = new ClassName(SceneDirector.class, PlainSceneDirector.class.getName());
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    public void clear()
    {
        for (Layer layer : this.layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage != null) {
                stage.clear();
            }
        }
    }

    public Scene copy()
    {
        Scene result = new Scene();
        result.showMouse = this.showMouse;
        result.sceneDirectorClassName = this.sceneDirectorClassName;
        result.sceneDirector = this.sceneDirector;
        result.layout = this.layout.clone();

        return result;
    }

    public ClassName getSceneDirectorClassName()
    {
        return this.sceneDirectorClassName;
    }

    public void setSceneDirectorClassName(ClassName className)
    {
        this.sceneDirectorClassName = className;
        this.sceneDirector = null;
    }

    public SceneDirector getSceneDirector()
    {
        if (this.sceneDirector != null) {
            return this.sceneDirector;
        }

        if (StringUtils.isBlank(this.sceneDirectorClassName.name)) {
            this.sceneDirector = new PlainSceneDirector();
        } else {
            try {
                this.sceneDirector = (SceneDirector) this.sceneDirectorClassName
                    .createInstance(Itchy.getGame().resources);
            } catch (Exception e) {
                Itchy.handleException(e);
                this.sceneDirector = new PlainSceneDirector();
            }
        }
        return this.sceneDirector;
    }

    public boolean uses(Font font)
    {
        for (Layer layer : this.layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage!= null) {
                if (stageUses(stage, font)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean uses(Costume costume)
    {
        for (Layer layer : this.layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage!= null) {
                if (stageUses(stage, costume)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean stageUses(Stage stage, Font font)
    {
        // TODO implement stageUses(Font)
        return false;
    }
    
    public boolean stageUses(Stage stage, Costume costume)
    {
        // TODO implement stageUses(Costume)
        return false;
    }
   
}
