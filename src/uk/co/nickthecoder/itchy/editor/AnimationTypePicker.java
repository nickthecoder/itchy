/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class AnimationTypePicker extends Window
{
    public AnimationTypePicker()
    {
        super("Pick an Animation Type");

        PlainContainer container = new PlainContainer();
        container.setXSpacing(10);
        container.setYSpacing(10);
        GridLayout grid = new GridLayout(container, 5);

        for (Animation animation : Itchy.registry.getAnimations()) {

            AbstractComponent component = this.createButton(animation);

            grid.addChild(component);
        }
        grid.endRow();

        VerticalScroll vs = new VerticalScroll(container);
        this.clientArea.addChild(vs);
    }

    private AbstractComponent createButton( final Animation animation )
    {
        PlainContainer container = new PlainContainer();
        container.setLayout(new VerticalLayout());
        container.setXAlignment(0.5);

        PlainContainer center = new PlainContainer();
        center.setXAlignment(0.5);
        center.setYAlignment(0.5);
        center.setMinimumWidth(64);
        center.setMaximumWidth(64);
        center.setMinimumHeight(64);
        center.setMaximumHeight(64);

        ImagePose icon = Editor.instance.getStylesheet().resources.getPose("animation-" + animation.getTagName());
        ImageComponent img = new ImageComponent((icon == null) ? null : icon.getSurface());
        center.addChild(img);
        Button button = new Button(center);
        container.addChild(button);

        container.addChild(new Label(animation.getName()));

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                AnimationTypePicker.this.hide();
                AnimationTypePicker.this.pick(animation);
            }
        });

        return container;
    }

    public abstract void pick( Animation animation );

}
