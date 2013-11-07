/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.ColorAnimation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.ForwardsAnimation;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class AnimationTypePicker extends Window
{
    private static final Animation[] animationPrototypes = {
        new CompoundAnimation(true),
        new CompoundAnimation(false),
        new MoveAnimation(),
        new ForwardsAnimation(),
        new AlphaAnimation(),
        new TurnAnimation(),
        new FramedAnimation(),
        new ColorAnimation(),
        new ScaleAnimation() };

    public AnimationTypePicker()
    {
        super("Pick an Animation Type");

        Container container = new Container();
        container.setLayout(new VerticalLayout());
        container.addStyle("picker");

        for (Animation animation : animationPrototypes) {

            Component component = this.createButton(animation);

            container.addChild(component);
        }

        VerticalScroll vs = new VerticalScroll(container);
        this.clientArea.addChild(vs);
    }

    private Component createButton( final Animation animation )
    {

        Button button = new Button(animation.getName());
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                AnimationTypePicker.this.destroy();
                AnimationTypePicker.this.pick(animation);
            }
        });

        return button;
    }

    public abstract void pick( Animation animation );

}
