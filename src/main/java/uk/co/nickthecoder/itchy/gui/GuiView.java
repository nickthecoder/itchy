/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.AbstractView;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.InputListener;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.SurfaceGraphicsContext;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.makeup.PictureFrame;
import uk.co.nickthecoder.itchy.makeup.SimpleFrame;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 */
public class GuiView extends AbstractView implements View, InputListener
{
    private boolean invalid = true;

    public final RootContainer rootContainer;

    private Surface surface;

    /**
     * A timer for tooltips to appear after the given time.
     */
    private Timer tooltipTimer = Timer.createTimerSeconds(1);

    /**
     * When true, the tooltip will appear when tooltipTimer has elapsed
     */
    private boolean timingTooltip = false;

    private Actor tooltipActor;

    private String tooltipText;

    /**
     * Defines how tooltip text will appear.
     */
    public TextStyle tooltipStyle;

    /**
     * Defines how tooltip text will be decorated. The default is a simple black frame (SimpleFrame).
     */
    public Makeup tooltipMakeup;

    public GuiView(Rect position, RootContainer rootContainer)
    {
        super(position);
        this.rootContainer = rootContainer;
        this.rootContainer.view = this;
    }

    @Override
    public void setPosition(Rect position)
    {
        System.err.println( "Setting GuiView position : " + position );
        super.setPosition(position);
        this.invalid = true;
        this.surface = null;
    }

    public Surface getSurface()
    {
        this.rootContainer.ensureLayedOut();
        if (this.surface == null) {
            this.surface = new Surface(this.rootContainer.width, this.rootContainer.height, true);
        }
        if (this.invalid) {
            this.surface.fill(new RGBA(0, 0, 0, 0));
            GraphicsContext gc = new SurfaceGraphicsContext(this.surface);
            this.rootContainer.render(gc);

        }
        this.invalid = false;
        return this.surface;
    }

    public void invalidate()
    {
        this.invalid = true;
    }

    private int oldX;
    private int oldY;

    /**
     * Changes the event's x,y from being screen coordinates to coordinates relative to this view's viewport.
     * 
     * @return true iff the event was within this view's viewport.
     */
    protected boolean adjustMouse(MouseEvent event)
    {
        this.oldX = event.x;
        this.oldY = event.y;
        Rect rect = getPosition();
        event.x -= rect.x;
        event.y -= rect.y;
        return ((event.x >= 0) && (event.x < rect.width) && (event.y >= 0) && (event.y < rect.height));
    }

    protected void unadjustMouse(MouseEvent event)
    {
        event.x = this.oldX;
        event.y = this.oldY;
    }

    @Override
    public void onMouseDown(MouseButtonEvent event)
    {
        try {
            if (!adjustMouse(event)) {
                return;
            }
            this.rootContainer.mouseDown(event);

        } finally {
            unadjustMouse(event);
        }

    }

    @Override
    public void onMouseUp(MouseButtonEvent event)
    {
        try {
            if (!adjustMouse(event)) {
                return;
            }
            this.rootContainer.mouseUp(event);

        } finally {
            unadjustMouse(event);
        }
    }

    @Override
    public void onMouseMove(MouseMotionEvent event)
    {
        try {
            if (!adjustMouse(event)) {
                this.endTooltipTimer();
                return;
            }
            beginTooltipTimer(event);
            this.rootContainer.mouseMove(event);

        } finally {
            unadjustMouse(event);
        }
    }

    @Override
    public void onKeyDown(KeyboardEvent event)
    {
        this.rootContainer.keyDown(event);
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }

    @Override
    public GraphicsContext adjustGraphicsContext(GraphicsContext gc)
    {
        return gc.window(new Rect(
            rootContainer.x + this.position.x, rootContainer.y + this.position.y,
            rootContainer.width, rootContainer.height));
    }

    @Override
    public void render(GraphicsContext gc)
    {
        this.checkTooltipTimer();

        Surface surface = getSurface();
        gc.blit(surface, 0, 0);
    }

    private void endTooltipTimer()
    {
        this.timingTooltip = false;
    }

    private void beginTooltipTimer(MouseEvent event)
    {
        if (!this.isVisible()) {
            return;
        }

        if (this.tooltipActor != null) {
            this.tooltipActor.kill();
        }
        this.tooltipText = null;

        Component tooltipComponent = this.rootContainer.getComponent(event);
        if (tooltipComponent == null) {
            return;
        }

        while (tooltipComponent.getTooltip() == null) {
            tooltipComponent = tooltipComponent.getParent();
            if (tooltipComponent == null) {
                return;
            }
        }

        this.tooltipText = tooltipComponent.getTooltip();
        this.tooltipTimer.reset();
        this.timingTooltip = true;
    }

    private void checkTooltipTimer()
    {
        if (this.timingTooltip && this.tooltipTimer.isFinished() && (this.tooltipText != null)) {
            createTooltip();
        }
    }

    private void createTooltip()
    {
        this.timingTooltip = false;

        if (this.tooltipStyle == null) {
            Label dummy = new Label("Dummy");
            dummy.addStyle("tooltip");
            this.rootContainer.stylesheet.style(dummy);
            this.tooltipStyle = new TextStyle(dummy.getFont(), dummy.getFontSize());
            this.tooltipStyle.color = dummy.getColor();
            this.tooltipStyle.setMargins(4);

            if (dummy.background != null) {
                PictureFrame frame = new PictureFrame(dummy.getMarginTop(), dummy.getMarginRight(),
                    dummy.getMarginBottom(), dummy.getMarginLeft());
                frame.setBackground(dummy.background);
                this.tooltipMakeup = frame;
            } else {
                this.tooltipMakeup = new SimpleFrame(new RGBA(128, 128, 128, 200), new RGBA(160, 160, 160, 200), 2,
                    dummy.getMarginTop(),
                    dummy.getMarginRight(), dummy.getMarginBottom(), dummy.getMarginLeft());
            }
        }
        if (this.tooltipMakeup == null) {
            this.tooltipMakeup = new SimpleFrame(new RGBA(128, 128, 128, 200), new RGBA(160, 160, 160, 200), 2,
                this.tooltipStyle.marginTop, this.tooltipStyle.marginRight, this.tooltipStyle.marginBottom,
                this.tooltipStyle.marginLeft);
        }
        MultiLineTextPose pose = new MultiLineTextPose(this.tooltipStyle);
        pose.setText(this.tooltipText);
        pose.setAlignment(0, 0);
        this.tooltipActor = new Actor(pose);
        this.tooltipActor.moveTo(Itchy.getMouseX() + 20,
            Itchy.getGame().getGlassView().getPosition().height - Itchy.getMouseY() - 20);
        this.tooltipActor.getAppearance().setMakeup(this.tooltipMakeup);
        Itchy.getGame().getGlassStage().add(this.tooltipActor);

    }

    @Override
    public String toString()
    {
        return "GuiView : " + this.rootContainer;
    }

}
