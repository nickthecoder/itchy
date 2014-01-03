/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.RGBABox;
import uk.co.nickthecoder.jame.RGBA;

public class RGBAProperty<S> extends AbstractProperty<S, RGBA>
{
    private boolean allowNull;

    private boolean includeAlpha;
    
    public RGBAProperty( String key )
    {
        super(key);
        this.allowNull = false;
        this.includeAlpha = false;
    }

    @Override
    public RGBA getDefaultValue()
    {
        return RGBA.BLACK;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        RGBA color = this.getSafeValue(subject);
        final RGBABox result = new RGBABox(color, this.allowNull, this.includeAlpha);

        if (autoUpdate) {

            result.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        RGBAProperty.this.update(subject, result);
                    } catch (Exception e) {
                    }
                }
            });
        }

        return result;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        RGBABox rgbaBox = (RGBABox) component;
        rgbaBox.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component )
    {
        RGBABox rgbaBox = (RGBABox) component;
        try {
            this.setValue(subject, rgbaBox.getValue());
        } catch (Exception e) {
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        RGBABox rgbaBox = (RGBABox) component;
        rgbaBox.setValue(this.getValue(subject));
    }

    @Override
    public RGBA parse( String value )
    {
        try {
            return RGBA.parse(value, this.allowNull, this.includeAlpha);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getErrorText( Component component )
    {
        try {
            ((RGBABox) component).getValue();
        } catch (Exception e) {
            return "Not a valid colour";
        }
        return null;
    }

    // Fluent API boilerplate
    @Override
    public RGBAProperty<S> label( String label )
    {
        super.label(label);
        return this;
    }

    @Override
    public RGBAProperty<S> access( String access )
    {
        super.access(access);
        return this;
    }

    @Override
    public RGBAProperty<S> hint( String hint )
    {
        super.hint(hint);
        return this;
    }

    public RGBAProperty<S> allowNull( boolean value )
    {
        this.allowNull = value;
        return this;
    }
    
    public RGBAProperty<S> includeAlpha( boolean value )
    {
        this.includeAlpha = value;
        return this;
    }

}
