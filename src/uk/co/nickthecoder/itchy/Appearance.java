/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.DoubleProperty;
import uk.co.nickthecoder.itchy.util.FontProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.PropertySubject;
import uk.co.nickthecoder.itchy.util.RGBAProperty;
import uk.co.nickthecoder.itchy.util.StringProperty;
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
        List<AbstractProperty<Appearance, ?>> result =
            new ArrayList<AbstractProperty<Appearance, ?>>(normalProperties);

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

    /**
     * A scale factor. A value of 1 leaves the appearance unchanged, less than 1 shrinks, greater
     * than 1 scales.
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

        if (this.pose.changedSinceLastUsed()) {
            this.clearCachedSurface();
        }
        if (this.processedSurface == null) {
            this.processSurface();
        }
    }

    private void processSurface()
    {
        try {
            Surface newSurface = this.pose.getSurface();
            int offsetX = this.pose.getOffsetX();
            int offsetY = this.pose.getOffsetY();

            this.dynamicSurface = false;

            double scale = this.scale;
            if (scale < 0) {
                scale = 0;
            }
            
            if (this.clip != null) {
                Rect rect = new Rect( 0,0, newSurface.getWidth(), newSurface.getHeight());
                rect = this.clip.intersection(rect);
                if ( (rect.width < 0) || (rect.height < 0)) {
                    newSurface = new Surface(1,1,true);
                } else {
                    Surface old = newSurface;
                    newSurface = new Surface( rect.width, rect.height, true);
                    old.blit(rect,newSurface,0,0, BlendMode.COMPOSITE);
                    offsetY -= rect.y;
                    offsetX -= rect.x;
                }
            }
            
            double dirDiff = this.direction - this.pose.getDirection();
            if ((dirDiff != 0)) { // && (this.rotationType ==
                                  // ROTATION_FULL ) ) {

                // The rotation will increase the size of the image (as the
                // source is rectangular, not circular).
                // We will recalculate the new offset relative to the center
                // of the surface.
                double odx = offsetX - newSurface.getWidth() / 2.0;
                double ody = offsetY - newSurface.getHeight() / 2.0;

                newSurface = newSurface.rotoZoom(dirDiff, scale, true);
                this.dynamicSurface = true;

                double dirRadians = dirDiff / 180.0 * Math.PI;
                double cosa = Math.cos(-dirRadians);
                double sina = Math.sin(-dirRadians);
                double ndy = odx * sina + ody * cosa;
                double ndx = odx * cosa - ody * sina;

                offsetX = (int) (newSurface.getWidth() / 2.0 + ndx * scale);
                offsetY = (int) (newSurface.getHeight() / 2.0 + ndy * scale);

            } else {

                if (scale != 1.0) {

                    offsetX *= scale;
                    offsetY *= scale;
                    int width = (int) (newSurface.getWidth() * scale);
                    int height = (int) (newSurface.getHeight() * scale);

                    if (width <= 0) {
                        width = 1;
                    }
                    if (height <= 0) {
                        height = 1;
                    }

                    newSurface = newSurface.zoom(scale, scale, true);
                    this.dynamicSurface = true;

                }
            }

            if (this.colorize != null) {

                Surface colorSurface = new Surface(newSurface.getWidth(), newSurface.getHeight(),
                    true);
                // if ( newSurface == pose.getSurface() ) {
                if (!this.dynamicSurface) {
                    newSurface = newSurface.copy();
                    this.dynamicSurface = true;
                }
                colorSurface.fill(this.colorize);
                colorSurface.blit(newSurface);

            }

            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.processedSurface = newSurface;

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

}
