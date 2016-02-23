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
    public double minValue = -Double.MAX_VALUE;
    
    public double maxValue = Double.MAX_VALUE;
    

    public DoubleProperty( String key )
    {
        super(key);
        this.defaultValue = 0.0;
    }

    public DoubleProperty<S> min( int value )
    {
        this.minValue = value;
        return this;
    }

    public DoubleProperty<S> max( int value )
    {
        this.maxValue = value;
        return this;
    }
    
    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        final DoubleBox box = new DoubleBox(this.getSafeValue(subject));
        box.minimumValue = this.minValue;
        box.maximumValue = this.maxValue;

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
    public Double getValueFromComponent(Component component ) throws Exception
    {
        DoubleBox doubleBox = (DoubleBox) component;
        return doubleBox.getValue();
    }

    @Override
    public void updateComponentValue( Double value, Component component )
    {
        DoubleBox doubleBox = (DoubleBox) component;
        doubleBox.setValue(value);
    }

    @Override
    public boolean isValid(Component component)
    {
        try {
            double value = getValueFromComponent( component );
            if (value < this.minValue) {
                return false;
            }
            if (value > this.maxValue) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return super.isValid(component);
    }
    
    @Override
    public Double parse( String value )
    {
        return Double.parseDouble(value);
    }
}
