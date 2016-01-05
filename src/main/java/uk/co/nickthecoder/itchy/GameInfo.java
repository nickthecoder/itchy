/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;

public class GameInfo implements PropertySubject<GameInfo>
{
    protected static List<AbstractProperty<GameInfo, ?>> properties = new LinkedList<AbstractProperty<GameInfo, ?>>();
    
    static {
        properties.add( new StringProperty<GameInfo>( "title" ));
        properties.add( new IntegerProperty<GameInfo>( "width" ));
        properties.add( new IntegerProperty<GameInfo>( "height" ));
        properties.add( new BooleanProperty<GameInfo>( "resizable" ));
        properties.add( new StringProperty<GameInfo>( "initialScene" ));
        properties.add( new ClassNameProperty<GameInfo>( Director.class, "directorClassName" ).aliases( "className" ));
        properties.add( new StringProperty<GameInfo>( "authors" ).multiLine());        
    }
    
    public String title;

    public int width;

    public int height;

    public String initialScene;

    public ClassName directorClassName;

    public String authors;
    
    public boolean resizable;
    
    public GameInfo()
    {
        this.title = "Itchy Game";
        this.width = 800;
        this.height = 600;
        this.resizable = false;
        this.directorClassName = new ClassName(Director.class, PlainDirector.class.getName());
        this.initialScene = "";
        this.authors = "";
    }

    @Override
    public List<AbstractProperty<GameInfo, ?>> getProperties()
    {
        return properties;
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
