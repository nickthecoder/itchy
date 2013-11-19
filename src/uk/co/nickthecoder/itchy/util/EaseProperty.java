/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.animation.EasePickerButton;
import uk.co.nickthecoder.itchy.animation.LinearEase;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;

public class EaseProperty<S> extends AbstractProperty<S, Ease>
{

    public EaseProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    public EaseProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Ease getDefaultValue()
    {
        return new LinearEase();
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final EasePickerButton button = new EasePickerButton(getSafeValue(subject));

        if (autoUpdate) {
            button.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        EaseProperty.this.update(subject, button);
                    } catch (Exception e) {
                    }
                }
            });
        }

        return button;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        EasePickerButton button = (EasePickerButton) component;
        button.addChangeListener(listener);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        EasePickerButton button = (EasePickerButton) component;
        setValue(subject, button.getValue());
    }

    @Override
    public Ease parse( String value )
    {
        Ease ease = Itchy.registry.getEase(value);
        if (ease == null) {
            throw new RuntimeException("Named Ease not found : " + value);
        }
        return ease;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Ease ease = getValue(subject);
        if (ease == null) {
            return null;
        } else {
            return ease.getName();
        }
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }
}
