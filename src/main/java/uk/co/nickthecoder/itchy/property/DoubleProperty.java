/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.util.BeanHelper;

public class DoubleProperty<S> extends Property<S, Double>
{
    public DoubleProperty( String key )
    {
        super(key);
        this.defaultValue = 0.0;
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        final DoubleBox box = new DoubleBox(this.getSafeValue(subject));

        if (autoUpdate) {

            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        DoubleProperty.this.updateSubject(subject, box);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return box;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        DoubleBox doubleBox = (DoubleBox) component;
        doubleBox.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator)
    {
        DoubleBox doubleBox = (DoubleBox) component;
        doubleBox.addValidator(validator);
    }

    /**
     * Do don't use the super class, just in case the type of the property value isn't a Double (which would cause a cast exception). This
     * could happen if the property was being retrieved from a dynamically typed language, where we don't directly control attribute types.
     * For example, the value may be an Integer, not a Double.
     */
    @Override
    public Double getValue( S subject )
        throws Exception
    {
        Number result = (Number) BeanHelper.getProperty(subject, this.access);
        return result.doubleValue();
    }

    @Override
    public void updateSubject( S subject, Component component ) throws Exception
    {
        DoubleBox doubleBox = (DoubleBox) component;
        try {
            this.setValue(subject, doubleBox.getValue());
            doubleBox.removeStyle("error");
        } catch (Exception e) {
            doubleBox.addStyle("error");
        }
    }

    @Override
    public void updateComponent( S subject, Component component ) throws Exception
    {
        DoubleBox doubleBox = (DoubleBox) component;
        doubleBox.setValue(this.getValue(subject));
    }

    @Override
    public Double parse( String value )
    {
        return Double.parseDouble(value);
    }

    @Override
    public String getErrorText( Component component )
    {
        try {
            ((DoubleBox) component).getValue();
        } catch (Exception e) {
            return "Not a valid decimal number";
        }
        return null;
    }

}
