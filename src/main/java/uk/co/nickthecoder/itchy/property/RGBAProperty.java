/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.RGBABox;
import uk.co.nickthecoder.jame.RGBA;

public class RGBAProperty<S> extends Property<S, RGBA>
{
    private boolean includeAlpha;

    public RGBAProperty(String key)
    {
        super(key);
        this.includeAlpha = true;
        this.defaultValue = RGBA.BLACK;
    }

    @Override
    public Component createUnvalidatedComponent(final S subject )
    {
        RGBA color = this.getSafeValue(subject);
        final RGBABox result = new RGBABox(color, this.allowNull, this.includeAlpha);

        return result;
    }

    @Override
    public void addChangeListener(Component component, ComponentChangeListener listener)
    {
        RGBABox rgbaBox = (RGBABox) component;
        rgbaBox.addChangeListener(listener);
    }

    @Override
    public void addValidator(Component component, ComponentValidator validator)
    {
        RGBABox rgbaBox = (RGBABox) component;
        rgbaBox.addValidator(validator);
    }

    @Override
    public void updateComponentValue(RGBA value, Component component)
    {
        RGBABox rgbaBox = (RGBABox) component;
        rgbaBox.setValue(value);
    }


    @Override
    public RGBA getValueFromComponent(Component component) throws Exception
    {
        RGBABox rgbaBox = (RGBABox) component;
        return rgbaBox.getValue();
    }
    
    @Override
    public RGBA parse(String value)
    {
        try {
            return RGBA.parse(value, this.allowNull, this.includeAlpha);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Fluent API boilerplate
    @Override
    public RGBAProperty<S> label(String label)
    {
        super.label(label);
        return this;
    }

    @Override
    public RGBAProperty<S> access(String access)
    {
        super.access(access);
        return this;
    }

    @Override
    public RGBAProperty<S> hint(String hint)
    {
        super.hint(hint);
        return this;
    }

    public RGBAProperty<S> allowNull(boolean value)
    {
        super.allowNull(value);
        if ((this.defaultValue == null) && (!this.allowNull)) {
            this.defaultValue = RGBA.BLACK;
        }
        return this;
    }

    public RGBAProperty<S> includeAlpha(boolean value)
    {
        this.includeAlpha = value;
        return this;
    }


}
