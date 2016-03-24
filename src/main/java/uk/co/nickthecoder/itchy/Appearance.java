/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.animation.ColorAnimation;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.makeup.BuiltInClip;
import uk.co.nickthecoder.itchy.makeup.BuiltInColorize;
import uk.co.nickthecoder.itchy.makeup.BuiltInMakeup;
import uk.co.nickthecoder.itchy.makeup.BuiltInRotoZoom;
import uk.co.nickthecoder.itchy.makeup.BuiltInScale;
import uk.co.nickthecoder.itchy.makeup.ForwardingMakeup;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.makeup.MakeupPipeline;
import uk.co.nickthecoder.itchy.makeup.NullMakeup;
import uk.co.nickthecoder.itchy.makeup.TransformationData;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.FontProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

/**
 * Part of an {@link Actor}, defines how the actor looks on screen, including its {@link Pose} a scaling factor, an
 * alpha value (to make the actor semi-transparent), its rotation and colour.
 */
public final class Appearance implements OffsetSurface, PropertySubject<Appearance>
{
    protected static final List<Property<Appearance, ?>> properties = new ArrayList<Property<Appearance, ?>>();
    protected static final List<Property<Appearance, ?>> textProperties = new ArrayList<Property<Appearance, ?>>();
    protected static final List<Property<Appearance, ?>> imageProperties = new ArrayList<Property<Appearance, ?>>();

    static {
        properties.add(new DoubleProperty<Appearance>("scale").defaultValue(1.0));
        properties.add(new DoubleProperty<Appearance>("direction"));
        properties.add(new DoubleProperty<Appearance>("alpha").defaultValue(255.0));
        properties.add(new RGBAProperty<Appearance>("colorize").allowNull(true).defaultValue(null));

        textProperties.add(new FontProperty<Appearance>("pose.font"));
        textProperties.add(new DoubleProperty<Appearance>("pose.fontSize").aliases("size"));
        textProperties.add(new StringProperty<Appearance>("pose.text"));
        textProperties.add(new RGBAProperty<Appearance>("pose.color").defaultValue(RGBA.WHITE));
        textProperties.add(new DoubleProperty<Appearance>("pose.xAlignment").defaultValue(0.5));
        textProperties.add(new DoubleProperty<Appearance>("pose.yAlignment").defaultValue(0.5));
        textProperties.addAll(properties);

        imageProperties.addAll(properties);
    }

    private Actor actor;

    private Pose pose;

    private WorldRectangle worldRectangle;

    /**
     * A scale factor. A value of 1 leaves the appearance unchanged, less than 1 shrinks, greater than 1 scales.
     */
    private double scale;

    /**
     * The direction the image is pointing towards in degrees.
     */
    private double direction;

    /**
     * How transparent the image is from 0 (fully transparent) to 255 (fully opaque).
     */
    private double alpha;

    /**
     * If set, then the image tends to this color (uses the alpha value).
     */
    private RGBA colorize;

    /**
     * The clipping area in pixels measured from the top left of the image.
     */
    private Rect clip;

    /**
     * Contains a cached Surface and the offset x,y after applying the transformations in the pipeline. If there are no
     * transformations,
     * then this will be Pose itself.
     */
    private OffsetSurface processedSurface;

    /**
     * We need to know if our processed surface was generated from the pose's current surface (which might change). We
     * do this by asking
     * pose for a change ID when we process the surface. Later, we ask for the change ID again, and if it is the same,
     * then we know that the
     * cached processed surface was generated from the pose's current surface.
     */
    private int previousPoseChangeId = -1;

    /**
     * We need to know if the cached image in processedSurface is valid. i.e. we need to know if the transformations
     * (Makeup) have changed
     * since we last created the surface. We do this by asking the pipe line for a change id when we process the image.
     * We then ask the
     * pipeline for the id again, and if it is the same, then our cached surface is valid, so doesn't need to be
     * recreated.
     */
    private int previousPipelineChangeId;

    /**
     * The pose can be transformed in many ways before appearing on the screen. Possible transformations include
     * scaling, rotating, colouring and applying arbitrary Makeup, such as a Shadow. The pipeline is an ordered list of
     * transformations ({@link Makeup}), which are applied in sequence.
     * <p>
     * The pipeline is usually made up of the following items :
     * <p>
     * {@link #clipMakeup}, {@link #normalMakeup}, {@link #rotateAndScaleMakeup} and {@link #colorizeMakeup}.
     * <p>
     * Advanced game writers may choose to alter this pipeline. For example, you may want to apply the clip after the
     * normalMakeup, (in which case, you would remove it from the list, and then add it at index #1).
     * <p>
     * Another example: If your game is on a hex grid, you might want your actors to rotate in jumps of 60 degrees. In
     * which case, you would replace the rotateAndScaleMakeup with a customised version.
     * <p>
     * Note that {@link #normalMakeup} is NOT the same as {@link #getMakeup()}. normalMakeup is a wrapper around the
     * result of getMakeup(). This lets you call setMakeup(x), without having to remove the old makeup from the
     * pipeline, and adding the new one back into the correct place.
     * <p>
     * rotateAndScaleMakeup is a single item because scale and rotate are done in a single operation. however, if no
     * rotation is needed, then rotateAndScaleMakeup will use {@link #scaleMakeup} to perform the transformation.
     * 
     * @priority 4
     */
    public final MakeupPipeline pipeline;

    /**
     * @priority 4
     */
    public final ForwardingMakeup normalMakeup = new ForwardingMakeup(new NullMakeup());

    /**
     * @priority 4
     */
    public final BuiltInMakeup clipMakeup = new BuiltInClip(this);

    /**
     * @priority 4
     */
    public final BuiltInMakeup rotateAndScaleMakeup = new BuiltInRotoZoom(this);

    /**
     * @priority 4
     */
    public final BuiltInMakeup scaleMakeup = new BuiltInScale(this);

    /**
     * @priority 4
     */
    public final BuiltInMakeup colorizeMakeup = new BuiltInColorize(this);

    /**
     * Appearances are created automatically when an Actor is created.
     * 
     * @param pose
     * @priority 5
     */
    Appearance(Pose pose)
    {
        this.pose = pose;
        this.processedSurface = null;

        this.direction = 0;
        this.scale = 1;
        this.alpha = 255;
        this.colorize = null;
        this.worldRectangle = null;

        this.pipeline = new MakeupPipeline();
        this.pipeline.add(this.clipMakeup);
        this.pipeline.add(this.normalMakeup);
        this.pipeline.add(this.rotateAndScaleMakeup);
        this.pipeline.add(this.colorizeMakeup);
        pose.attach(this);
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<Appearance, ?>> getProperties()
    {
        if (this.pose instanceof TextPose) {
            return textProperties;
        } else {
            return imageProperties;
        }
    }

    /**
     * Used internally by Itchy.
     * 
     * @param actor
     * @priority 5
     */
    void setActor(Actor actor)
    {
        assert this.actor == null : "An Appearance cannot be shared by more than one Actor";
        this.actor = actor;
    }

    /**
     * A simple getter.
     */
    public Actor getActor()
    {
        return this.actor;
    }

    /**
     * A simple getter.
     * 
     * @return
     */
    public Pose getPose()
    {
        return this.pose;
    }

    /**
     * A simple setter.
     * The most common way to change an {@link Actor}'s {@link Pose} is to create an event in its {@link Costume} from
     * within the {@link Editor}, and then call {@link Actor#event(String)} or {@link AbstractRole#event(String)}.
     * 
     * @param pose
     */
    public void setPose(Pose pose)
    {
        pose.attach(this);
        invalidateShape();
        this.pose = pose;
    }

    /**
     * Usually Makeup is applied from the {@link Editor}, very rarely called from within a game.
     * 
     * @param makeup
     * @priority 2
     */
    public void setMakeup(Makeup makeup)
    {
        this.normalMakeup.setMakeup(makeup);
        invalidateShape();
    }

    /**
     * A simple getter.
     * 
     * @return
     * @priority 2
     */
    public Makeup getMakeup()
    {
        return this.normalMakeup.getMakeup();
    }

    /**
     * Similar to {@link Pose#getOffsetX()}, but for this Appearance's surface, which may be scaled, rotated or changed
     * in other ways using {@link Makeup}.
     */
    @Override
    public int getOffsetX()
    {
        this.ensureOk();
        return this.processedSurface.getOffsetX();
    }

    /**
     * Similar to {@link Pose#getOffsetY()}, but for this Appearance's surface, which may be scaled, rotated or changed
     * in other ways using {@link Makeup}.
     */
    @Override
    public int getOffsetY()
    {
        this.ensureOk();
        return this.processedSurface.getOffsetY();
    }

    /**
     * A simple getter; 0 = fully transparent, 255 = fully opaque.
     * 
     * @return
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * A simple setter; 0 = fully transparent, 255 = fully opaque.
     * 
     * @return
     */
    public void setAlpha(double alpha)
    {
        if (this.alpha != alpha) {
            this.alpha = alpha;
        }
    }

    public void adjustAlpha(double amount)
    {
        this.setAlpha(this.alpha + amount);
    }

    /**
     * A simple getter, see {@link #setColorize(RGBA)}.
     * 
     * @return
     */
    public RGBA getColorize()
    {
        return this.colorize;
    }

    /**
     * Colours the image, leaving the transparent pixels transparent.
     * For example, you could colour the image slightly red to indicate damage.
     * <p>
     * Consider using a {@link ColorAnimation}, rather than setting colorize directly. It will make your code simpler
     * and more flexible (as different animations can be used without changing any code).
     * 
     * @param color
     */
    public void setColorize(RGBA color)
    {
        this.colorize = color;
        this.colorizeMakeup.changed();
        invalidateShape();
    }

    /**
     * A simple getter - The rotation of the image in degrees.
     * When using trigonometric functions such as sin and cos, use {@link #getDirectionRadians()} instead.
     * 
     * @return The rotation of the image in degrees.
     */
    public double getDirection()
    {
        return this.direction;
    }

    /**
     * 
     * @return The rotation of the image in radians.
     * @priority 2
     */
    public double getDirectionRadians()
    {
        return this.direction * Math.PI / 180;
    }

    /**
     * A simple setter - the rotation of the image in degrees
     * 
     * @param degrees
     */
    public void setDirection(double degrees)
    {
        if (this.direction != degrees) {
            this.direction = degrees;
            this.rotateAndScaleMakeup.changed();
            invalidateShape();
        }
    }

    /**
     * A simple setter - the rotation of the image in radians
     * 
     * @param radians
     * @priority 2
     */
    public void setDirectionRadians(double radians)
    {
        this.setDirection((radians * 180 / Math.PI));
    }

    /**
     * Adds delta to the direction.
     * 
     * @param delta
     * @priority 2
     */
    public void adjustDirection(double delta)
    {
        this.setDirection(this.direction + delta);
    }

    /**
     * Rotates this Actor so that it is pointing in the direction of the other Actor.
     * 
     * @param other
     * @priority 2
     */
    public void setDirectionTowards(Actor other)
    {
        this.setDirection(this.actor.directionOf(other));
    }

    /**
     * A simple getter - the scale factor, 1 = no scaling.
     * 
     * @return
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * A simple getter - the scale factor, 1 = no scaling.
     * 
     * @param value
     */
    public void setScale(double value)
    {
        if (this.scale != value) {
            this.scale = value;
            this.rotateAndScaleMakeup.changed();
            this.scaleMakeup.changed();
            invalidateShape();
        }
    }

    /**
     * Adds delta to the scale.
     * 
     * @param delta
     * @priority 2
     */
    public void adjustScale(double delta)
    {
        this.setScale(this.scale + delta);
    }

    /**
     * Clips the image, so that it is partially obscured.
     * 
     * @param clip
     */
    public void setClip(Rect clip)
    {
        this.clip = clip;
        invalidateShape();
    }

    /**
     * A simple getter - the current clipping rectangle, or null if there is no clipping rectangle.
     * 
     * @return
     */
    public Rect getClip()
    {
        return this.clip;
    }

    /**
     * Is the Actor within the given rectangle? This is based on the bounding rectangle of the image, i.e.
     * transparent pixels make no difference.
     * 
     * @param worldRect
     * @return
     * @priority 2
     */
    public boolean visibleWithin(WorldRectangle worldRect)
    {
        return this.getWorldRectangle().overlaps(worldRect);
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    public void invalidateShape()
    {
        if ((this.processedSurface != null) && (!this.processedSurface.isShared())) {
            this.processedSurface.getSurface().free();
        }
        this.processedSurface = null;
        this.worldRectangle = null;

    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    public void invalidatePosition()
    {
        this.worldRectangle = null;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    private void ensureOk()
    {
        if (this.pipeline.getChangeId() != this.previousPipelineChangeId) {
            this.invalidateShape();
        }
        if (this.pose.getChangeId() != this.previousPoseChangeId) {
            this.invalidateShape();
        }
        if (this.processedSurface == null) {
            this.processSurface();
        }
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    private void processSurface()
    {
        this.previousPoseChangeId = this.pose.getChangeId();
        this.previousPipelineChangeId = this.pipeline.getChangeId();

        this.processedSurface = this.pipeline.apply(this.pose);
    }

    /**
     * Gets the {@link Surface} (image) for the Actor, after the transformations such as scale and rotation have been
     * applied.
     * <p>
     * Note that the alpha ({@link #setAlpha(double)}) is not applied, because alpha is done only when rendering the
     * actor on the screen.
     * 
     * @priority 5
     */
    @Override
    public Surface getSurface()
    {
        this.ensureOk();
        return this.processedSurface.getSurface();
    }

    /**
     * Calculates the bounding rectangle for the Actor in world coordinates (not screen coordinates).
     * <p>
     * Implementation note. This data is cached, and updated only when the Appearance has changed, or the Actor has
     * moved. The world rectangle is used to test for collisions, so its important that it is fast as possible.
     * 
     * @return
     */
    public WorldRectangle getWorldRectangle()
    {
        if (this.worldRectangle == null) {

            if (this.processedSurface == null) {

                TransformationData data = new TransformationData();
                data.set(this.pose.getSurface().getWidth(), this.pose.getSurface().getHeight(),
                    this.pose.getOffsetX(), this.pose.getOffsetY());
                this.pipeline.applyGeometry(data);

                this.worldRectangle = new WorldRectangle(
                    this.actor.getX() - data.offsetX,
                    this.actor.getY() + data.offsetY - data.height,
                    data.width,
                    data.height
                    );

            } else {
                this.worldRectangle = new WorldRectangle(
                    this.actor.getX() - this.processedSurface.getOffsetX(),
                    this.actor.getY() + this.processedSurface.getOffsetY()
                        - this.processedSurface.getSurface().getHeight(),
                    this.processedSurface.getSurface().getWidth(),
                    this.processedSurface.getSurface().getHeight());
            }
        }
        return this.worldRectangle;
    }

    /**
     * The width of the bounding rectangle for the Actor.
     * 
     * @return
     */
    public int getWidth()
    {
        return this.getSurface().getWidth();
    }

    /**
     * The height of the bounding rectangle for the Actor.
     * 
     * @return
     */
    public int getHeight()
    {
        return this.getSurface().getHeight();
    }

    /**
     * Takes the current {@link Pose}, and superimposes another image ('other') on top of it. The new image
     * is used as the Appearance's new Pose.
     * <p>
     * If the pose is changed (using {@link #setPose(Pose)}, or {@link Actor#event(String)}, then the superimposed image
     * will be lost.
     * 
     * @param other
     *            The image to place on top of the actor's current image.
     * @param dx
     *            The relative position of 'other'. The two offsetXs are also taken into account.
     * @param dy
     *            The relative position of 'other'. The two offsetYs are also taken into account.
     * @priority 2
     */
    public void superimpose(OffsetSurface other, int dx, int dy)
    {
        this.setPose(ImagePose.superimpose(this, other, dx, dy));
    }

    /**
     * Takes a snapshot of how the actor looks right now, and uses it as their pose. Resets the Makeup to NullMakeup, as
     * the makeup is not
     * "baked-into" the pose itself.
     * <p>
     * This can be used for efficiency; if the appearance has complex makeup, then the makeup would normally be applied
     * every time the actor is rotated, colorized, scaled etc. If you fix the appearance, then the makeup is only
     * applied once.
     * <p>
     * This can also be used to create particular effects. For example, if the actor is rotated, and you use clip, then
     * the clip is usually done before the rotation. However, if you fix the appearance, and then clip, the clip will
     * apply to the rotated image.
     * <p>
     * Note: If an actor changes pose, then the results of the fixed makeup will be lost.
     * 
     * TO DO BUG? Doesn't look right, as the scale, rotation, colorize and makeup will be applied, but only the makeup
     * is removed, so the others will be applied twice. This should probably be replaced with "fixMakeup", which renders
     * the makeup, and then nulls it out, and by "snapshot", which returns the current image including scale, rotation
     * etc, which could then be used as the basis for other kind of processing, but leaves this completely appearance
     * unchanged.
     * 
     * @priority 2
     */
    public void fixAppearance()
    {
        ImagePose pose = new ImagePose(getSurface().copy(), getOffsetX(), getOffsetY());

        pose.setDirection(this.direction);
        this.pose = pose;
        this.invalidateShape();
        setMakeup(new NullMakeup());
        this.scale = 1;

        this.getSurface();
    }

    /**
     * Used internally by Itchy as part of the {@link OffsetSurface} interface.
     * 
     * @return true
     * @priority 5
     */
    @Override
    public boolean isShared()
    {
        return true;
    }

}
