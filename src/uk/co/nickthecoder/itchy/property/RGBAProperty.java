/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.RGBABox;
import uk.co.nickthecoder.jame.RGBA;

public class RGBAProperty<S> extends AbstractProperty<S, RGBA>
{
    private final boolean allowNull;

    private final boolean includeAlpha;

    public RGBAProperty( String label, String access, String key, boolean allowNull, boolean includeAlpha )
    {
        super(label, access, key);
        this.allowNull = allowNull;
        this.includeAlpha = includeAlpha;
    }

    public RGBAProperty( String label, String access, boolean allowNull, boolean includeAlpha )
    {
        super(label, access);
        this.allowNull = allowNull;
        this.includeAlpha = includeAlpha;
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

}
