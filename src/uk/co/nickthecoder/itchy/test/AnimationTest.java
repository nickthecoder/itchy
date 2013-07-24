/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.io.File;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorsLayer;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class AnimationTest extends Game
{
    public ActorsLayer mainLayer;

    public AnimationTest() throws Exception
    {
        super("AnimationTest", 640, 480);
        this.resources.load(new File("resources/tests/animations.xml"));
    }

    @Override
    public void init()
    {
        this.mainLayer = new ScrollableLayer("main", this.screenRect, new RGBA(0, 0, 0));
        this.mainLayer.setYAxisPointsDown(true);
        this.layers.add(this.mainLayer);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        this.mainLayer.clear();

        if (ke.symbol == Keys.SPACE) {
            try {
                this.resources.getScene("testAnimation").create(this.mainLayer, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Actor actor : this.mainLayer.getActors()) {
                actor.deathEvent("death");
            }
            return true;
        }

        return false;
    }

    @Override
    public void tick()
    {

    }

    @Override
    public String getIconFilename()
    {
        return "resources/drunkInvaders/icon.bmp";
    }

    public static void main( String[] argv ) throws Exception
    {

        AnimationTest test = new AnimationTest();
        test.start();
    }
}
