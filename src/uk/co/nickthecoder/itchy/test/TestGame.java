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

public class TestGame extends Game
{
    public ActorsLayer mainLayer;
    
    public String sceneName = "menu";

    public TestGame() throws Exception
    {
        super("Test", 800, 600);
        this.resources.load(new File("resources/tests/resources.xml"));
    }

    @Override
    public void init()
    {
        this.mainLayer = new ScrollableLayer("main", this.screenRect, new RGBA(0, 0, 0));
        this.mainLayer.enableMouseListener();
        this.layers.add(this.mainLayer);
        loadScene( "menu", this.mainLayer);
    }

    public void reloadScene()
    {
        loadScene(this.sceneName);
    }
    
    public void loadScene(String sceneName)
    {
        System.out.println("Starting scene " + sceneName );
        this.sceneName = sceneName;
        this.mainLayer.clear();
        this.loadScene( this.sceneName,  this.mainLayer);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.F12) {
            startEditor();
            return true;
        }

        if (ke.symbol == Keys.ESCAPE) {
            loadScene( "menu" );
        }

        if (ke.symbol == Keys.RETURN) {
            reloadScene();
        }

        if (ke.symbol == Keys.DELETE) {
            for (Actor actor : this.mainLayer.getActors()) {
                actor.deathEvent("death");
            }
            return true;
        }

        return super.onKeyDown(ke);
    }

    public void onMessage(String message)
    {
        System.out.println("Message : " + message);
        
        if (message.startsWith("scene:")) {
            loadScene( message.substring(6));
            return;
        }
        
        super.onMessage(message);
    }
    
    public static void main( String[] argv ) throws Exception
    {

        TestGame test = new TestGame();
        test.start();
    }
}
