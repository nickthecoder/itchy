/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.NullComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PoseResourcePicker;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class EditFramedAnimation extends EditSingleAnimation
{
    private FramedAnimation framedAnimation;

    private PlainContainer framesContainer;

    private GridLayout framesGrid;

    public EditFramedAnimation(Resources resources, FramedAnimation animation)
    {
        super(resources, animation);
        this.framedAnimation = animation;
    }

    @Override
    public void addButtons(Container buttonBar)
    {
        Button add = new Button("Add");
        add.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                PoseResourcePicker posePicker = new PoseResourcePicker(EditFramedAnimation.this.resources)
                {
                    @Override
                    public void pick(PoseResource poseResource)
                    {
                        framedAnimation.addFrame(new Frame(poseResource.getName(), poseResource.pose));
                        EditFramedAnimation.this.rebuildFrames();
                    }
                };
                posePicker.show();
            }
        });
        buttonBar.addChild(add);

        super.addButtons(buttonBar);
    }

    @Override
    public Component createForm()
    {
        super.createForm();

        this.framesContainer = new PlainContainer();
        this.framesContainer.addStyle("form");
        this.framesGrid = new GridLayout(this.framesContainer, 7);

        this.framesGrid.addRow("Pose", new Label("Frames"), null, null);

        this.framesContainer.setLayout(this.framesGrid);

        this.rebuildFrames();

        VerticalScroll vs = new VerticalScroll(this.framesContainer);
        vs.addStyle("panel");

        PlainContainer both = new PlainContainer();
        both.setLayout(new VerticalLayout());
        both.addChild(this.form.container);
        both.addChild(vs);

        return both;
    }

    private void rebuildFrames()
    {
        this.framesGrid.clear();

        int i = -1;
        for (Frame frame : this.framedAnimation.getFrames()) {
            i++;
            this.rebuildFrame(i, frame);
        }
        this.framesContainer.invalidate();
    }

    private void rebuildFrame(final int i, final Frame frame)
    {
        String name = Itchy.getGame().resources.getPoseName(frame.getPose());
        if (name == null) {
            name = "?";
        }

        ImageComponent img = new ImageComponent(frame.getPose().getSurface());
        img.setTooltip("Pose " + name);
        final IntegerBox delay = new IntegerBox(frame.getDelay());
        delay.minimumValue = 1;

        delay.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                frame.setDelay(delay.getSafeValue(1));
            }

        });

        Stylesheet stylesheet = this.editWindow.getStylesheet();
        Button up = null;
        if (i > 0) {
            up = new Button(new ImageComponent(stylesheet.resources.getPose("icon_up").getSurface()));

            up.addActionListener(new ActionListener()
            {
                @Override
                public void action()
                {
                    Frame other = framedAnimation.getFrames().get(i - 1);
                    framedAnimation.getFrames().set(i, other);
                    framedAnimation.getFrames().set(i - 1, frame);
                    rebuildFrames();
                }
            });
            up.addStyle("compact");
            up.setTooltip("Move Up");
        }

        Button delete = new Button(new ImageComponent(stylesheet.resources.getPose("icon_delete").getSurface()));
        delete.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                framedAnimation.getFrames().remove(i);
                rebuildFrames();
            }
        });
        delete.addStyle("compact");
        delete.setTooltip("Remove " + name);

        final DoubleBox dxBox = new DoubleBox(frame.dx);
        dxBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                frame.dx = dxBox.getValue();
            }
        });
        final DoubleBox dyBox = new DoubleBox(frame.dy);
        dyBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                frame.dy = dyBox.getValue();
            }
        });

        Label label = new Label(name);

        this.framesGrid.addRow(new Component[] { img, delay, up == null ? new NullComponent() : up, delete, dxBox,
            dyBox, label });
    }

}
