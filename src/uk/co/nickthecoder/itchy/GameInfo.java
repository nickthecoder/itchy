/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.PropertySubject;

public class GameInfo implements PropertySubject<GameInfo>
{
    @Property(label = "Title")
    public String title;

    @Property(label = "Width")
    public int width;

    @Property(label = "Height")
    public int height;

    @Property(label = "Icon")
    public String iconFilename;

    @Property(label = "Class Name", baseClass = Game.class)
    public ClassName className;

    public GameInfo()
    {
        this.title = "Itchy Game";
        this.width = 800;
        this.height = 600;
        this.iconFilename = "icon.bmp";
        this.className = new ClassName(Game.class.getName());

    }

    @Override
    public List<AbstractProperty<GameInfo, ?>> getProperties()
    {
        return AbstractProperty.findAnnotations(this.getClass());
    }

}
