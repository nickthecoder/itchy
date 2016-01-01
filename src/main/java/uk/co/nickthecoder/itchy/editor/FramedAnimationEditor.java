/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class FramedAnimationEditor extends AnimationEditor
{
    private final Resources resources;

    private List<Frame> frames;

    private PlainContainer framesContainer;

    private GridLayout framesGrid;

    public FramedAnimationEditor( Editor editor, Resources resources, FramedAnimation animation )
    {
        super(editor, animation);
        this.resources = resources;
    }

    @Override
    public void createButtons( Container buttonBar )
    {
        Button add = new Button("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                PosePicker posePicker = new PosePicker(FramedAnimationEditor.this.resources) {
                    @Override
                    public void pick( PoseResource poseResource )
                    {
                        FramedAnimationEditor.this.frames.add(new Frame(poseResource.getName(),
                            poseResource.pose));
                        FramedAnimationEditor.this.rebuildFrames();
                    }
                };

                posePicker.show();
            }
        });
        buttonBar.addChild(add);

        super.createButtons(buttonBar);
    }

    @Override
    public AbstractComponent createExtra()
    {
        this.frames = new ArrayList<Frame>(((FramedAnimation) this.animation).getFrames());

        this.framesContainer = new PlainContainer();
        this.framesContainer.addStyle("form");
        this.framesGrid = new GridLayout(this.framesContainer, 4);

        this.framesGrid.addRow("Pose", new Label("Frames"), null, null);

        this.framesContainer.setLayout(this.framesGrid);

        this.rebuildFrames();

        VerticalScroll vs = new VerticalScroll(this.framesContainer);
        vs.addStyle("panel");

        return vs;
    }

    private void rebuildFrames()
    {
        this.framesGrid.clear();

        int i = -1;
        for (Frame frame : this.frames) {
            i++;
            this.rebuildFrame(i, frame);
        }
        this.framesContainer.invalidate();
    }

    private void rebuildFrame( final int i, final Frame frame )
    {
        AbstractComponent image;
        ImageComponent img = new ImageComponent(frame.getPose().getSurface());
        image = img;
        final IntegerBox delay = new IntegerBox(frame.getDelay());
        delay.minimumValue = 1;

        delay.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                frame.setDelay(delay.getSafeValue(1));
            }

        });

        Button up = null;
        if (i > 0) {
            up = new Button(new ImageComponent(this.editor.getStylesheet().resources.getPose(
                "icon_up").getSurface()));
            up.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    Frame other = FramedAnimationEditor.this.frames.get(i - 1);
                    FramedAnimationEditor.this.frames.set(i, other);
                    FramedAnimationEditor.this.frames.set(i - 1, frame);
                    FramedAnimationEditor.this.rebuildFrames();
                }
            });
            up.addStyle("compact");
        }

        Button delete = new Button(new ImageComponent(this.editor.getStylesheet().resources.getPose(
            "icon_delete").getSurface()));
        delete.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FramedAnimationEditor.this.frames.remove(i);
                FramedAnimationEditor.this.rebuildFrames();
            }
        });
        delete.addStyle("compact");

        this.framesGrid.addRow(image, delay, up, delete);
    }

    @Override
    public boolean save()
    {
        super.save();

        FramedAnimation framedAnimation = (FramedAnimation) this.animation;
        framedAnimation.replaceFrames(this.frames);

        return true;
    }

}