/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class EasePicker extends Window
{
    public EasePicker()
    {
        this(null);
    }
    
    public EasePicker(Ease defaultEase)
    {
        super("Ease Picker");

        this.clientArea.setLayout(new VerticalLayout());
        this.clientArea.setFill(true, false);
        
        Container container = new Container();
        VerticalScroll vs = new VerticalScroll(container);

        Component focus = this.createEases(container, defaultEase);
        this.clientArea.addChild(vs);
        this.clientArea.addStyle("vScrolled");

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);
        

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                EasePicker.this.destroy();
            }

        });
        buttons.addChild(cancel);
        this.clientArea.addChild(buttons);
        
        if (focus!=null) {
            focus.focus();
        }
    }

    private Component createEases( Container container, Ease defaultEase )
    {
        Component focus = null;
        
        GridLayout gridLayout = new GridLayout(container, 5);
        container.setLayout(gridLayout);
        container.addStyle("pickGrid");

        HashMap<String, Ease> map = NumericAnimation.getEases();
        List<String> orderedNames = Resources.sortNames(map.keySet());

        for (String name : orderedNames) {
            Ease ease = map.get(name);

            Component component = this.createButton(name, ease);
            if (ease == defaultEase) {
                focus = component;
            }
            gridLayout.addChild(component);
        }
        gridLayout.endRow();
        
        return focus;
    }

    private Component createButton( final String name, final Ease ease )
    {
        Button button = new Button();
        button.setLayout(new VerticalLayout());
        button.setXAlignment(0.5f);

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                EasePicker.this.destroy();
                EasePicker.this.pick(ease);
            }
        });

        ImageComponent img = new ImageComponent(ease.getThumbnail());
        Label label = new Label(name);
        label.addStyle("TEST"); // TODO remove

        button.addChild(img);
        button.addChild(label);

        return button;
    }

    public abstract void pick( Ease ease );

}
