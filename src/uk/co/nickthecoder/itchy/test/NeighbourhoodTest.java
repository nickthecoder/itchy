/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.neighbourhood.Neighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.StandardNeighbourhood;
import uk.co.nickthecoder.itchy.neighbourhood.SinglePointCollisionStrategy;
import uk.co.nickthecoder.itchy.neighbourhood.Square;
import uk.co.nickthecoder.jame.Video;

public class NeighbourhoodTest
{

    public static Resources resources;

    public Costume c30x30;
    public Costume c30x10;
    public Costume c10x30;

    @BeforeClass
    public static void setup()
    {
        System.out.println("Init Jame video");
        try {
            Video.init();
            Video.setMode(640, 480);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void teardown()
    {
    }

    @Before
    public void setUp() throws Exception
    {
        System.out.println("Loading resources");

        resources = new Resources();
        resources.load(new File("resources/tests/neighbourhood.xml"));

        this.c30x30 = resources.getCostume("30x30");
        this.c10x30 = resources.getCostume("10x30");
        this.c30x10 = resources.getCostume("30x10");
    }

    @After
    public void tearDown()
    {
    }

    public void testOneSquare()
    {
        Neighbourhood nbh = new StandardNeighbourhood(1);

        Square sq1 = nbh.getSquare(0, 0);
        assertNotNull("Looking for the origin square", sq1);

        Square sq2 = nbh.getSquare(0, 0);
        assertSame("Looking for the origin square again", sq1, sq2);

        Square sq3 = nbh.getSquare(1, 0);
        assertNotSame("Created a second square", sq1, sq3);

    }

    public SinglePointCollisionStrategy spcs( Neighbourhood nbh, Costume costume, double x, double y )
    {
        Actor actor = new Actor(costume);
        actor.moveTo(x, y);
        actor.addTag("all");

        SinglePointCollisionStrategy result = new SinglePointCollisionStrategy(actor, nbh);
        return result;
    }

    @Test
    public void createSquares()
    {
        Neighbourhood nbh = new StandardNeighbourhood(50);

        spcs(nbh, this.c30x30, 0, 0);
        spcs(nbh, this.c30x30, 60, 0);
        spcs(nbh, this.c30x30, -40, 0);
        spcs(nbh, this.c30x30, 290, 0);
        spcs(nbh, this.c30x30, -290, 0);
    }

    @Test
    public void createRows()
    {
        Neighbourhood nbh = new StandardNeighbourhood(50);

        spcs(nbh, this.c30x30, 0, 0);
        spcs(nbh, this.c30x30, 0, 60);
        spcs(nbh, this.c30x30, 0, -40);
        spcs(nbh, this.c30x30, 0, 290);
        spcs(nbh, this.c30x30, 0, -290);
    }

    @Test
    public void actorsInSquares()
    {
        Neighbourhood nbh = new StandardNeighbourhood(50);

        SinglePointCollisionStrategy a = spcs(nbh, this.c30x30, 0, 0);
        assertEquals(a.getSquare(), nbh.getSquare(0, 0));

        SinglePointCollisionStrategy b = spcs(nbh, this.c30x30, 10, 0);
        assertEquals(b.getSquare(), nbh.getSquare(0, 0));

        SinglePointCollisionStrategy c = spcs(nbh, this.c30x30, 50, 0);
        assertNotSame(a.getSquare(), c.getSquare());

        SinglePointCollisionStrategy d = spcs(nbh, this.c30x30, 0, 50);
        assertNotSame(a.getSquare(), d.getSquare());

        SinglePointCollisionStrategy e = spcs(nbh, this.c30x30, 0, 49);
        assertSame(a.getSquare(), e.getSquare());

        SinglePointCollisionStrategy f = spcs(nbh, this.c30x30, 49, 0);
        assertSame(a.getSquare(), f.getSquare());

        SinglePointCollisionStrategy g = spcs(nbh, this.c30x30, -1, 0);
        // assertNotSame( a.getSquare(), g.getSquare() );

        SinglePointCollisionStrategy h = spcs(nbh, this.c30x30, -49, 0);
        // assertNotSame( a.getSquare(), h.getSquare() );
        assertSame(g.getSquare(), h.getSquare());

        SinglePointCollisionStrategy i = spcs(nbh, this.c30x30, -51, 0);
        assertNotSame(i.getSquare(), h.getSquare());
        assertTrue(i.getSquare().getNeighbouringSquares().contains(h.getSquare()));

        SinglePointCollisionStrategy z = spcs(nbh, this.c30x30, 0, 0);
        assertEquals(z.getSquare(), a.getSquare());

    }

    @Test
    public void pixelOverlap()
    {
        Neighbourhood nbh = new StandardNeighbourhood(50);

        SinglePointCollisionStrategy a = spcs(nbh, this.c30x30, 0, 0);
        SinglePointCollisionStrategy b = spcs(nbh, this.c30x30, 10, 0);
        SinglePointCollisionStrategy c = spcs(nbh, this.c30x30, 50, 0);

        assertTrue(b.getActor().pixelOverlap(a.getActor()));
        assertFalse(a.getActor().pixelOverlap(c.getActor()));

        assertTrue(a.overlapping("all").contains(b.getActor()));
        assertFalse(a.overlapping("all").contains(c.getActor()));

        assertTrue(a.pixelOverlap("all").contains(b.getActor()));
        assertFalse(a.pixelOverlap("all").contains(c.getActor()));

    }

    public static void main( String[] argv )
    {
        NeighbourhoodTest test = new NeighbourhoodTest();
        test.actorsInSquares();
    }
}
