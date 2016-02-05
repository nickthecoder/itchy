/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.animation.EasePickerButton;
import uk.co.nickthecoder.itchy.animation.LinearEase;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;

public class EaseProperty<S> extends Property<S, Ease>
{
    public EaseProperty( String key)
    {
        super(key);
        this.defaultValue = new LinearEase();

    }

    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        final EasePickerButton button = new EasePickerButton(getSafeValue(subject));
        return button;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        EasePickerButton button = (EasePickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        EasePickerButton button = (EasePickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public Ease getValueFromComponent( Component component )
    {
        EasePickerButton button = (EasePickerButton) component;
        return button.getValue();
    }

    @Override
    public void updateComponentValue( Ease value, Component component )
    {
        EasePickerButton button = (EasePickerButton) component;
        button.setValue(value);
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

}
