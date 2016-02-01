/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.makeup.BuiltInClip;
import uk.co.nickthecoder.itchy.makeup.BuiltInColorize;
import uk.co.nickthecoder.itchy.makeup.BuiltInMakeup;
import uk.co.nickthecoder.itchy.makeup.BuiltInRotoZoom;
import uk.co.nickthecoder.itchy.makeup.BuiltInScale;
import uk.co.nickthecoder.itchy.makeup.ForwardingMakeup;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.makeup.MakeupPipeline;
import uk.co.nickthecoder.itchy.makeup.TransformationData;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.FontProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public final class Appearance implements OffsetSurface, PropertySubject<Appearance>
{
    protected static final List<Property<Appearance, ?>> properties = new ArrayList<Property<Appearance, ?>>();
    protected static final List<Property<Appearance, ?>> textProperties = new ArrayList<Property<Appearance, ?>>();
    protected static final List<Property<Appearance, ?>> imageProperties = new ArrayList<Property<Appearance, ?>>();

    static {
        properties.add(new DoubleProperty<Appearance>("scale").defaultValue(1.0));
        properties.add(new DoubleProperty<Appearance>("direction"));
        properties.add(new DoubleProperty<Appearance>("alpha").defaultValue(255.0));
        properties.add(new RGBAProperty<Appearance>("colorize").allowNull(true));

        textProperties.add(new FontProperty<Appearance>("pose.font"));
        textProperties.add(new DoubleProperty<Appearance>("pose.fontSize").aliases("size"));
        textProperties.add(new StringProperty<Appearance>("pose.text"));
        textProperties.add(new RGBAProperty<Appearance>("pose.color"));
        textProperties.add(new DoubleProperty<Appearance>("pose.xAlignment").defaultValue(0.5));
        textProperties.add(new DoubleProperty<Appearance>("pose.yAlignment").defaultValue(0.5));
        textProperties.addAll( properties );
        
        imageProperties.addAll( properties );
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
     * Contains a cached Surface and the offset x,y after applying the transformations in the pipeline. If there are no transformations,
     * then this will be Pose itself.
     */
    private OffsetSurface processedSurface;

    /**
     * We need to know if our processed surface was generated from the pose's current surface (which might change). We do this by asking
     * pose for a change ID when we process the surface. Later, we ask for the change ID again, and if it is the same, then we know that the
     * cached processed surface was generated from the pose's current surface.
     */
    private int previousPoseChangeId = -1;

    /**
     * We need to know if the cached image in processedSurface is valid. i.e. we need to know if the transformations (Makeup) have changed
     * since we last created the surface. We do this by asking the pipe line for a change id when we process the image. We then ask the
     * pipeline for the id again, and if it is the same, then our cached surface is valid, so doesn't need to be recreated.
     */
    private int previousPipelineChangeId;

    /**
     * The pose can be transformed in many ways before appearing on the screen. Possible transformations include scaling, rotating,
     * colouring and applying arbitrary Makeup, such as a Shadow. The pipeline is an ordered list of transformations (Makeup), which are
     * applied in sequence.
     * <p>
     * The pipeline is usually made up of the following items :
     * <p>
     * {@link #clipMakeup}, {@link #normalMakeup}, {@link #rotateAndScaleMakeup} and {@link #colorizeMakeup}.
     * <p>
     * Advanced game writers may choose to alter this pipeline. For example, you may want to apply the clip after the normalMakeup, (in
     * which case, you would remove it from the list, and then add it at index #1).
     * <p>
     * Another example: If your game is on a hex grid, you might want your actors to rotate in jumps of 60 degrees. In which case, you would
     * replace the rotateAndScaleMakeup with a customised version.
     * <p>
     * Note that {@link #normalMakeup} is NOT the same as {@link #getMakeup()}. normalMakeup is a wrapper around the result of getMakeup().
     * This lets you call setMakeup(x), without having to remove the old makeup from the pipeline, and adding the new one back into the
     * correct place.
     * <p>
     * rotateAndScaleMakeup is a single item because scale and rotate are done in a single operation. however, if no rotation is needed,
     * then rotateAndScaleMakeup will use {@link #scaleMakeup} to perform the transformation.
     */
    public final MakeupPipeline pipeline;

    public final ForwardingMakeup normalMakeup = new ForwardingMakeup(new NullMakeup());

    public final BuiltInMakeup clipMakeup = new BuiltInClip(this);

    public final BuiltInMakeup rotateAndScaleMakeup = new BuiltInRotoZoom(this);

    public final BuiltInMakeup scaleMakeup = new BuiltInScale(this);

    public final BuiltInMakeup colorizeMakeup = new BuiltInColorize(this);

    public Appearance( Pose pose )
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


    @Override
    public List<Property<Appearance, ?>> getProperties()
    {
        if (this.pose instanceof TextPose ) {
            return textProperties;
        } else {
            return imageProperties;
        }
    }

    void setActor( Actor actor )
    {
        assert this.actor == null : "An Appearance cannot be shared by more than one Actor";
        this.actor = actor;
    }

    public Actor getActor()
    {
        return this.actor;
    }

    public Pose getPose()
    {
        return this.pose;
    }

    public void setPose( Pose pose )
    {
        pose.attach(this);
        invalidateShape();
        this.pose = pose;
    }

    public static Makeup createMakeup( ClassName className )
    {
        try {
            return (Makeup) Class.forName(className.name).newInstance();
        } catch (Exception e) {
            System.err.println("Failed to create Makeup : " + className.name);
            e.printStackTrace();
            return new NullMakeup();
        }
    }

    public void setMakeup( ClassName className )
    {
        this.setMakeup(createMakeup(className));
    }

    public void setMakeup( Makeup makeup )
    {
        this.normalMakeup.setMakeup(makeup);
        invalidateShape();
    }

    public Makeup getMakeup()
    {
        return this.normalMakeup.getMakeup();
    }

    @Override
    public int getOffsetX()
    {
        this.ensureOk();
        return this.processedSurface.getOffsetX();
    }

    @Override
    public int getOffsetY()
    {
        this.ensureOk();
        return this.processedSurface.getOffsetY();
    }

    public double getAlpha()
    {
        return this.alpha;
    }

    public void setAlpha( double alpha )
    {
        if (this.alpha != alpha) {
            this.alpha = alpha;
        }
    }

    public void adjustAlpha( double amount )
    {
        this.setAlpha(this.alpha + amount);
    }

    public RGBA getColorize()
    {
        return this.colorize;
    }

    public void setColorize( RGBA color )
    {
        this.colorize = color;
        this.colorizeMakeup.changed();
        invalidateShape();
    }

    public double getDirection()
    {
        return this.direction;
    }

    public double getDirectionRadians()
    {
        return this.direction * Math.PI / 180;
    }

    public void setDirection( double degrees )
    {
        if (this.direction != degrees) {
            this.direction = degrees;
            this.rotateAndScaleMakeup.changed();
            invalidateShape();
        }
    }

    public void setDirection( Actor other )
    {
        this.setDirection(this.actor.directionOf(other));
    }

    public void adjustDirection( double degrees )
    {
        setDirection(this.direction + degrees);
    }

    public void setDirectionRadians( double radians )
    {
        this.setDirection((radians * 180 / Math.PI));
    }

    public double getScale()
    {
        return this.scale;
    }

    public void setScale( double value )
    {
        if (this.scale != value) {
            this.scale = value;
            this.rotateAndScaleMakeup.changed();
            this.scaleMakeup.changed();
            invalidateShape();
        }
    }

    public void adjustScale( double delta )
    {
        this.setScale(this.scale + delta);
    }

    public void scale( double scale )
    {
        this.setScale(this.scale * scale);
    }

    public void setClip( Rect clip )
    {
        this.clip = clip;
        invalidateShape();
    }

    public Rect getClip()
    {
        return this.clip;
    }

    public boolean visibleWithin( WorldRectangle worldRect )
    {
        return this.getWorldRectangle().overlaps(worldRect);
    }

    public void invalidateShape()
    {
        if ((this.processedSurface != null) && (!this.processedSurface.isShared())) {
            this.processedSurface.getSurface().free();
        }
        this.processedSurface = null;
        this.worldRectangle = null;

    }

    public void invalidatePosition()
    {
        this.worldRectangle = null;
    }

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

    public ClassName getMakeupClassName()
    {
        return getMakeupClassName(this.normalMakeup.getMakeup());
    }

    public static ClassName getMakeupClassName( Makeup makeup )
    {
        // if (role instanceof ScriptedMakup) {
        // return ((ScriptedMakeup) makeup).getClassName();
        // } else {
        return new ClassName(Makeup.class, makeup.getClass().getName());
        // }
    }

    private void processSurface()
    {
        this.previousPoseChangeId = this.pose.getChangeId();
        this.previousPipelineChangeId = this.pipeline.getChangeId();

        this.processedSurface = this.pipeline.apply(this.pose);
    }

    @Override
    public Surface getSurface()
    {
        this.ensureOk();
        return this.processedSurface.getSurface();
    }

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
                    this.actor.getY() + this.processedSurface.getOffsetY() - this.processedSurface.getSurface().getHeight(),
                    this.processedSurface.getSurface().getWidth(),
                    this.processedSurface.getSurface().getHeight());
            }
        }
        return this.worldRectangle;
    }

    public int getWidth()
    {
        return this.getSurface().getWidth();
    }

    public int getHeight()
    {
        return this.getSurface().getHeight();
    }

    public void superimpose( OffsetSurface other, int dx, int dy )
    {
        this.setPose(ImagePose.superimpose(this, other, dx, dy));
    }

    /**
     * Takes a snapshot of how the actor looks right now, and uses it as their pose. Resets the Makeup to NullMakeup, as the makeup is not
     * "baked-into" the pose itself.
     * <p>
     * This can be used for efficiency; if the appearance has complex makeup, then the makeup would normally be applied every time the actor
     * is rotated, colorized, scaled etc. If you fix the appearance, then the makeup is only applied once.
     * <p>
     * This can also be used to create particular effects. For example, if the actor is rotated, and you use clip, then the clip is usually
     * done before the rotation. However, if you fix the appearance, and then clip, the clip will apply to the rotated image.
     * <p>
     * Note: If an actor changes pose, then the results of the fixed makeup will be lost.
     * 
     * TO DO BUG? Doesn't look right, as the scale, rotation, colorize and makeup will be applied, but
     * only the makeup is removed, so the others will be applied twice. This should probably be replaced
     * with "fixMakeup", which renders the makeup, and then nulls it out, and by "snapshot", which
     * returns the current image including scale, rotation etc, which could then be used as the basis
     * for other kind of processing, but leaves this completely appearance unchanged. 
     */
    public void fixAppearance()
    {
        ImagePose pose = new ImagePose(getSurface().copy(), getOffsetX(), getOffsetY());

        // Doesn't work. Bodge it bt setting direction to 0 for now.
        // pose.setDirection(getDirection());
        this.direction = 0;

        setPose(pose);
        setMakeup(new NullMakeup());
        this.scale = 1;
    }

    @Override
    public boolean isShared()
    {
        return true;
    }

}
