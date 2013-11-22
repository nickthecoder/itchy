/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.makeup.DynamicMakeup;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.FontProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.Surface.BlendMode;

public final class Appearance implements OffsetSurface, PropertySubject<Appearance>
{
    private static List<AbstractProperty<Appearance, ?>> normalProperties =
        AbstractProperty.findAnnotations(Appearance.class);

    private static List<AbstractProperty<Appearance, ?>> textProperties = createTextProperties();

    private static List<AbstractProperty<Appearance, ?>> createTextProperties()
    {
        List<AbstractProperty<Appearance, ?>> result = new ArrayList<AbstractProperty<Appearance, ?>>(normalProperties);

        result.add(new FontProperty<Appearance>("Font", "pose.font"));
        result.add(new DoubleProperty<Appearance>("Font Size", "pose.fontSize"));
        result.add(new StringProperty<Appearance>("Text", "pose.text"));
        result.add(new RGBAProperty<Appearance>("Text Color", "pose.color", false, false));
        result.add(new DoubleProperty<Appearance>("X Alignment", "pose.xAlignment"));
        result.add(new DoubleProperty<Appearance>("Y Alignment", "pose.yAlignment"));

        return result;
    }

    private Actor actor;

    private Surface processedSurface;

    private boolean dynamicSurface;

    private Pose pose;

    private int offsetX;

    private int offsetY;

    private WorldRectangle worldRectangle;

    private Makeup makeup = new NullMakeup();

    private int previousMakeupId;

    /**
     * A scale factor. A value of 1 leaves the appearance unchanged, less than 1 shrinks, greater than 1 scales.
     */
    @Property(label = "Scale")
    private double scale;

    /**
     * The direction the image is pointing towards in degrees.
     */
    @Property(label = "Direction")
    private double direction;

    /**
     * How transparent the image is from 0 (fully transparent) to 255 (fully opaque).
     */
    @Property(label = "Alpha (0..255)")
    private double alpha;

    /**
     * If set, then the image tends to this color (uses the alpha value).
     */
    @Property(label = "Colourise")
    private RGBA colorize;

    /**
     * The clipping area in pixels measured from the top left of the image.
     */
    private Rect clip;

    public Appearance( Pose pose )
    {
        this.pose = pose;
        this.processedSurface = null;

        this.direction = 0;
        this.scale = 1;
        this.alpha = 255;
        this.colorize = null;
        this.worldRectangle = null;
    }

    void setActor( Actor actor )
    {
        assert this.actor == null : "An Appearance cannot be shared by more than one Actor";
        this.actor = actor;

        this.worldRectangle = new WorldRectangle(this.actor.getX() - this.pose.getOffsetX(),
            this.actor.getY() - this.pose.getOffsetY(), this.pose.getSurface().getWidth(),
            this.pose.getSurface().getHeight());
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
        this.clearCachedSurface();
        this.pose = pose;
    }

    public static Makeup createMakeup( ClassName className )
    {
        // TODO Implement ScriptedMakeup
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
        this.clearCachedSurface();
        this.makeup = makeup;
    }

    public Makeup getMakeup()
    {
        return this.makeup;
    }

    @Override
    public int getOffsetX()
    {
        this.ensureOk();
        return this.offsetX;
    }

    @Override
    public int getOffsetY()
    {
        this.ensureOk();
        return this.offsetY;
    }

    @Property(label = "Alpha")
    public double getAlpha()
    {
        return this.alpha;
    }

    public void setAlpha( double alpha )
    {
        if (this.alpha != alpha) {
            this.alpha = alpha;
            this.clearCachedSurface();
        }
    }

    public void adjustAlpha( double amount )
    {
        this.setAlpha(this.alpha + amount);
    }

    @Property(label = "Colourise", allowNull = true)
    public RGBA getColorize()
    {
        return this.colorize;
    }

    public void setColorize( RGBA color )
    {
        this.colorize = color;
        this.clearCachedSurface();
    }

    @Property(label = "Direction")
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
            this.clearCachedSurface();
        }
    }

    public void setDirection( Actor other )
    {
        this.setDirection(this.actor.directionOf(other));
    }

    public void adjustDirection( double degrees )
    {
        this.direction += degrees;
        this.clearCachedSurface();
    }

    public void setDirectionRadians( double radians )
    {
        this.setDirection((radians * 180 / Math.PI));
    }

    @Property(label = "Scale")
    public double getScale()
    {
        return this.scale;
    }

    public void setScale( double value )
    {
        if (this.scale != value) {
            this.scale = value;
            this.clearCachedSurface();
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
        this.clearCachedSurface();
    }

    public Rect getClip()
    {
        return this.clip;
    }

    public boolean visibleWithin( WorldRectangle worldRect )
    {
        return this.getWorldRectangle().overlaps(worldRect);
    }

    public void clearCachedSurface()
    {
        if (this.dynamicSurface) {
            this.processedSurface.free();
            this.dynamicSurface = false;
        }
        this.processedSurface = null;
        this.worldRectangle = null;
    }

    private void ensureOk()
    {
        if (this.makeup.getChangeId() != this.previousMakeupId) {
            this.clearCachedSurface();
        }
        if (this.pose.changedSinceLastUsed()) {
            this.clearCachedSurface();
        }
        if (this.processedSurface == null) {
            this.processSurface();
        }
    }

    private Makeup clipper = new DynamicMakeup()
    {

        @Override
        public OffsetSurface apply( OffsetSurface os )
        {
            if (Appearance.this.clip == null) {
                return os;
            }

            Rect rect = new Rect(0, 0, os.getSurface().getWidth(), os.getSurface().getHeight());
            rect = Appearance.this.clip.intersection(rect);

            if ((rect.width < 0) || (rect.height < 0)) {
                return new SimpleOffsetSurface(new Surface(1, 1, true), 0, 0);

            } else {
                Surface clippedSurface = new Surface(rect.width, rect.height, true);
                os.getSurface().blit(rect, clippedSurface, 0, 0, BlendMode.COMPOSITE);

                return new SimpleOffsetSurface(clippedSurface, os.getOffsetX() - rect.x, os.getOffsetY() - rect.y);
            }
        }
    };

    private Makeup rotoZoom = new DynamicMakeup()
    {
        @Override
        public OffsetSurface apply( OffsetSurface os )
        {
            double dirDiff = Appearance.this.direction - Appearance.this.pose.getDirection();
            if (((int) dirDiff) == 0) {
                return Appearance.this.scaler.apply(os);
            }

            // The rotation will increase the size of the image (as the source is rectangular, not circular).
            // We will recalculate the new offset relative to the center of the surface.
            double odx = os.getOffsetX() - os.getSurface().getWidth() / 2.0;
            double ody = os.getOffsetY() - os.getSurface().getHeight() / 2.0;

            Surface rotated = os.getSurface().rotoZoom(dirDiff, Appearance.this.scale, true);

            double dirRadians = dirDiff / 180.0 * Math.PI;
            double cosa = Math.cos(-dirRadians);
            double sina = Math.sin(-dirRadians);
            double ndy = odx * sina + ody * cosa;
            double ndx = odx * cosa - ody * sina;

            return new SimpleOffsetSurface(
                rotated,
                (int) (os.getSurface().getWidth() / 2.0 + ndx * Appearance.this.scale),
                (int) (os.getSurface().getHeight() / 2.0 + ndy * Appearance.this.scale)
            );
        }
    };

    private Makeup scaler = new DynamicMakeup()
    {
        @Override
        public OffsetSurface apply( OffsetSurface os )
        {
            double scale = Appearance.this.scale;
            
            if (scale == 1.0) {
                return os;
            }

            if (scale <= 0) {
                return new SimpleOffsetSurface( new Surface(1,1,true), 0, 0);
            }

            Appearance.this.offsetX *= scale;
            Appearance.this.offsetY *= scale;
            int width = (int) (os.getSurface().getWidth() * scale);
            int height = (int) (os.getSurface().getHeight() * scale);

            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }
            Surface scaled = os.getSurface().zoom(scale, scale, true);

            return new SimpleOffsetSurface(
                scaled,
                (int) (os.getOffsetX() * scale),
                (int) (os.getOffsetY() * scale));
        }

    };

    private Makeup colorizer = new DynamicMakeup()
    {
        @Override
        public OffsetSurface apply( OffsetSurface os )
        {
            if (Appearance.this.colorize == null) {
                return os;
            }

            Surface colorSurface = new Surface(os.getSurface().getWidth(), os.getSurface().getHeight(), true);
            Surface result = os.getSurface();
            // if (os.isShared()) {
            result = result.copy();
            // }
            colorSurface.fill(Appearance.this.colorize);
            colorSurface.blit(result);
            colorSurface.free();

            return new SimpleOffsetSurface(result, os.getOffsetX(), os.getOffsetY());
        }
    };

    public ClassName getMakeupClassName()
    {
        return getMakeupClassName(this.makeup);
    }

    public static ClassName getMakeupClassName( Makeup makeup )
    {
        // TODO Allow for scripted makeup.
        // if (role instanceof ScriptedMakup) {
        // return ((ScriptedMakeup) makeup).getClassName();
        // } else {
        return new ClassName(Makeup.class, makeup.getClass().getName());
        // }
    }

    public static OffsetSurface applyMakeup( Makeup makeup, OffsetSurface src )
    {
        OffsetSurface result = makeup.apply(src);

        if ((!src.isShared()) && (src.getSurface() != result.getSurface())) {
            src.getSurface().free();
        }
        return result;
    }

    private OffsetSurface processing;
    
    private void processSurface()
    {
        processing = this.pose;

        try {

            processing = applyMakeup(this.clipper, processing);
            processing = applyMakeup(this.makeup, processing);
            this.previousMakeupId = this.makeup.getChangeId();

            processing = applyMakeup(this.rotoZoom, processing);
            processing = applyMakeup(this.colorizer, processing);

            this.offsetX = processing.getOffsetX();
            this.offsetY = processing.getOffsetY();
            this.processedSurface = processing.getSurface();

            this.dynamicSurface = !processing.isShared();

            this.pose.used();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Surface getSurface()
    {
        this.ensureOk();
        return this.processedSurface;
    }

    public void onMoved()
    {
        this.worldRectangle = null;
    }

    public WorldRectangle getWorldRectangle()
    {
        // MORE Calc the rectangle without needing to redraw the surface
        // If available, give the surface's rectangle
        // otherwise
        // if rotated
        // calculate the radius of the bounding circle for width and height
        // otherwise
        // use the base surfaces for the width and height
        // Scale the width and height if needed and center it.
        if (this.worldRectangle == null) {
            this.ensureOk();

            this.worldRectangle = new WorldRectangle(
                this.actor.getX() - this.offsetX,
                this.actor.getY() + this.offsetY - this.processedSurface.getHeight(),
                this.processedSurface.getWidth(),
                this.processedSurface.getHeight());
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

    @Override
    public List<AbstractProperty<Appearance, ?>> getProperties()
    {
        if (this.pose instanceof TextPose) {
            return textProperties;
        } else {
            return normalProperties;
        }
    }

    /**
     * Takes a snapshot of how the actor looks right now, and uses it as their pose. Resets the Makeup to NullMakeup, as the makeup is not
     * "baked-into" the pose itself.
     * <p>
     * This can be used for efficiency; if the appearance has complex makeup, then the makeup would normally be applied every time the actor
     * is rotated, colourised, scaled etc. If you fix the appearance, then the makeup is only applied once.
     * <p>
     * This can also be used to create particular effects. For example, if the actor is rotated, and you use clip, then the clip is usually
     * done before the rotation. However, if you fix the appearance, and then clip, the clip will apply to the rotated image.
     * <p>
     * Note: If an actor changes pose, then the results of the fixed makeup will be lost.
     */
    public void fixAppearance()
    {
        ImagePose pose = new ImagePose(getSurface(), getOffsetX(), getOffsetY());
        pose.setDirection(getDirection());
        setPose(pose);
        setMakeup(new NullMakeup());
    }

    @Override
    public boolean isShared()
    {
        return true;
    }

}
