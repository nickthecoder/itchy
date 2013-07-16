/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;

public class AnimationEditor extends Window
{
    public Animation animation;

    protected Editor editor;

    public AnimationEditor( Editor editor, Animation animation )
    {
        super(animation.getName());
        assert (animation != null);
        this.editor = editor;
        this.animation = animation;
        this.clientArea.setLayout(new VerticalLayout());

        Container form = new Container();
        GridLayout grid = new GridLayout(form, 2);
        form.setLayout(grid);
        form.addStyle("form");

        this.createForm(grid);
        if (form.getChildren().size() > 0) {
            this.clientArea.addChild(form);
        }

        Component extra = this.createExtra();
        if (extra != null) {
            this.clientArea.addChild(extra);
        }

        Container buttonBar = new Container();
        buttonBar.addStyle("buttonBar");
        this.createButtons(buttonBar);
        this.clientArea.addChild(buttonBar);
    }

    public void createButtons( Container buttonBar )
    {
        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                AnimationEditor.this.onOk();
            }
        });
        buttonBar.addChild(ok);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                AnimationEditor.this.onCancel();
            }
        });
        buttonBar.addChild(cancel);
    }

    public void createForm( GridLayout gridLayout )
    {
    }

    public Component createExtra()
    {
        return null;
    }

    @Override
    public void show()
    {
        Itchy.singleton.showWindow(this);
    }

    public void onOk()
    {
        if (this.save()) {
            Itchy.singleton.hideWindow(this);
        }
    }

    public void onCancel()
    {
        Itchy.singleton.hideWindow(this);
    }

    public boolean save()
    {
        return true;
    }
}
