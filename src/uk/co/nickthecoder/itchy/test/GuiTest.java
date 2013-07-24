/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorsLayer;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.FlowLayout;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.Rules;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SimpleTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public class GuiTest extends Game
{
    public ActorsLayer mainLayer;

    public Rules rules;

    public GuiPose guiPose;

    public GuiTest() throws Exception
    {
        super("GuiTest", 640, 480);

        this.rules = new Rules(new File("resources/defaultGui/style.xml"));

    }

    @Override
    public void init()
    {
        this.mainLayer = new ScrollableLayer("main", this.screenRect, new RGBA(255, 255, 255));
        this.mainLayer.setYAxisPointsDown(true);
        this.layers.add(this.mainLayer);

        this.guiPose = new GuiPose();
        this.guiPose.setRules(this.rules);
        this.guiPose.addStyle("test");
        this.guiPose.setPosition(0, 0, this.screenRect.width, this.screenRect.height);
        Actor actor = this.guiPose.getActor();
        this.mainLayer.add(actor);

        addEventListener(this.guiPose);
        menu();
    }

    public void oneHorizontal()
    {
        this.guiPose.setLayout(new HorizontalLayout());

        Label label1 = new Label("Hello World");
        label1.addStyle("likeButton");
        this.guiPose.addChild(label1);
    }

    public void oneVertical()
    {
        this.guiPose.setLayout(new VerticalLayout());

        Label label1 = new Label("Hello World");
        label1.addStyle("likeButton");
        this.guiPose.addChild(label1);
    }

    public void twoHorizontal()
    {
        this.guiPose.setLayout(new HorizontalLayout());

        Label label1 = new Label("Hello World");
        Label label2 = new Label("Second Label");
        label1.addStyle("likeButton");
        label2.addStyle("likeButton");
        this.guiPose.addChild(label1);
        this.guiPose.addChild(label2);
    }

    public void twoVertical()
    {
        this.guiPose.setLayout(new VerticalLayout());

        Label label1 = new Label("Hello World");
        Label label2 = new Label("Second Label");
        label1.addStyle("likeButton");
        label2.addStyle("likeButton");
        this.guiPose.addChild(label1);
        this.guiPose.addChild(label2);
    }

    public void oneHorizontalFilled()
    {
        this.guiPose.setLayout(new HorizontalLayout());
        this.guiPose.setFill(true, true);

        Label label1 = new Label("Hello World");
        label1.addStyle("likeButton");
        this.guiPose.addChild(label1);
    }

    public void oneVerticalFilled()
    {
        this.guiPose.setLayout(new VerticalLayout());
        this.guiPose.setFill(true, true);

        Label label1 = new Label("Hello World");
        label1.addStyle("likeButton");
        this.guiPose.addChild(label1);
    }

    public void twoHorizontalFilled()
    {
        this.guiPose.setLayout(new HorizontalLayout());
        this.guiPose.setFill(true, true);

        Label label1 = new Label("Hello World");
        Label label2 = new Label("Second Label");

        label1.addStyle("likeButton");
        label2.addStyle("likeButton");

        this.guiPose.addChild(label1);
        this.guiPose.addChild(label2);
    }

    public void twoVerticalFilled()
    {
        this.guiPose.setLayout(new VerticalLayout());
        this.guiPose.setFill(true, true);

        Label label1 = new Label("Hello World");
        Label label2 = new Label("Second Label");

        label1.addStyle("likeButton");
        label2.addStyle("likeButton");

        this.guiPose.addChild(label1);
        this.guiPose.addChild(label2);
    }

    public void table()
    {
        SimpleTableModel model = new SimpleTableModel();
        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        TableModelColumn col1 = new TableModelColumn("One", 0, 100);
        TableModelColumn col2 = new TableModelColumn("Two", 0, 200);
        columns.add(col1);
        columns.add(col2);

        SimpleTableModelRow row1 = new SimpleTableModelRow();
        row1.add("Top Left");
        row1.add("Top Right");

        SimpleTableModelRow row2 = new SimpleTableModelRow();
        row2.add("Middle Left");
        row2.add("Middle Right");

        SimpleTableModelRow row3 = new SimpleTableModelRow();
        row3.add("Bottom Left");
        row3.add("Bottom Right");

        model.addRow(row1);
        model.addRow(row2);
        model.addRow(row3);

        Table table = new Table(model, columns);

        this.guiPose.addChild(table);
    }

    public void window()
    {
        Window win = new Window("Test Window");
        win.clientArea.setFill(true, true);
        win.clientArea.setLayout(new VerticalLayout());
        win.clientArea.addChild(new Label("Hello"));
        win.clientArea.addChild(new Label("World"));
        win.setRules(this.rules);
        win.show();
    }

    public void reset()
    {
        this.guiPose.setFill(false, false);
        this.guiPose.clear();
        this.guiPose.setLayout(new VerticalLayout());
    }

    public void addTest( final String name )
    {
        Button button = new Button(name);
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                GuiTest.this.reset();
                GuiTest.this.runTest(name);
            }
        });

        this.guiPose.addChild(button);
    }

    public void menu()
    {
        this.reset();
        this.guiPose.setLayout(new FlowLayout());

        this.addTest("oneHorizontal");
        this.addTest("oneVertical");

        this.addTest("twoHorizontal");
        this.addTest("twoVertical");

        this.addTest("oneHorizontalFilled");
        this.addTest("oneVerticalFilled");

        this.addTest("twoHorizontalFilled");
        this.addTest("twoVerticalFilled");

        this.addTest("table");
        this.addTest("window");
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        System.out.println("Key symbol " + ke.symbol);
        if (ke.symbol == Keys.ESCAPE) {
            this.menu();
            return true;
        }

        return false;
    }

    public void runTest( String name )
    {
        try {
            Class<?>[] argTypes = new Class<?>[] {};
            Method method = this.getClass().getDeclaredMethod(name, argTypes);
            method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] argv ) throws Exception
    {
        GuiTest testGui = new GuiTest();
        testGui.start();
    }
}
