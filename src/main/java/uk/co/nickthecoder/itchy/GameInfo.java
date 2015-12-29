/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.util.ClassName;

public class GameInfo implements PropertySubject<GameInfo>
{
    @Property(label = "Title")
    public String title;

    @Property(label = "Width")
    public int width;

    @Property(label = "Height")
    public int height;

    @Property(label = "Initial Scene")
    public String initialScene;

    @Property(label = "Class Name", baseClass = Director.class, aliases = { "className" })
    public ClassName directorClassName;

    public GameInfo()
    {
        this.title = "Itchy Game";
        this.width = 800;
        this.height = 600;
        this.directorClassName = new ClassName(Director.class, PlainDirector.class.getName());
        this.initialScene = "";
    }

    @Override
    public List<AbstractProperty<GameInfo, ?>> getProperties()
    {
        return AbstractProperty.findAnnotations(this.getClass());
    }

    public Director createDirector( Resources resources )
    {
        Director director;

        try {
            if (resources.isValidScript(this.directorClassName.name)) {
                director = resources.scriptManager.createDirector(resources.getGameInfo().directorClassName);

            } else {
                Class<?> klass = Class.forName(resources.getGameInfo().directorClassName.name);
                director = (Director) klass.newInstance();
            }
        } catch (Exception e) {
            System.err.println("Failed to create director, using a PlainDirector instead");
            director = new PlainDirector();
            e.printStackTrace();
        }
        return director;
    }
}
