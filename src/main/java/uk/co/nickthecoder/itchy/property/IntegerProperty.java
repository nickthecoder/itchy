/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.util.BeanHelper;

public class IntegerProperty<S> extends Property<S, Integer>
{
    public int minValue = Integer.MIN_VALUE;
    
    public int maxValue = Integer.MAX_VALUE;
    

    public IntegerProperty( String key)
    {
        super(key);
        this.defaultValue = 0;
    }

    public IntegerProperty<S> min( int value )
    {
        this.minValue = value;
        return this;
    }

    public IntegerProperty<S> max( int value )
    {
        this.maxValue = value;
        return this;
    }
    
    
    @Override
    public Component createUnvalidatedComponent( final S subject )
    {
        final IntegerBox box = new IntegerBox(this.getSafeValue(subject));
        box.minimumValue = this.minValue;
        box.maximumValue = this.maxValue;
        
        return box;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        IntegerBox integerBox = (IntegerBox) component;
        integerBox.addChangeListener(listener);
    }

    @Override
    public void addValidator( Component component, ComponentValidator validator)
    {
        IntegerBox integerBox = (IntegerBox) component;
        integerBox.addValidator(validator);
    }
    
    /**
     * Do don't use the super class, just in case the type of the property value isn't an Integer (which would cause a cast exception). This
     * could happen if the property was being retrieved from a dynamically typed language, where we don't directly control attribute types.
     */
    @Override
    public Integer getValue( S subject )
        throws Exception
    {
        Object value = BeanHelper.getProperty(subject, this.access);
        try {
            Number result = (Number) value;
            return result.intValue();
        } catch (Exception e) {
            if (value == null) {
                System.err.println("Expected Integer but found null");
                return 0;
            }
            System.err.println("Expected Integer but found : " + value + " : " + value.getClass().getName());
            return Integer.parseInt(value.toString());
        }

    }
    
    @Override
    public Integer getValueFromComponent( Component component ) throws Exception
    {
        IntegerBox integerBox = (IntegerBox) component;
         return integerBox.getValue();
    }

    @Override
    public void updateComponentValue( Integer value, Component component )
    {
        IntegerBox integerBox = (IntegerBox) component;
        integerBox.setValue(value);
    }

    @Override
    public boolean isValid(Component component)
    {
        try {
            int value = getValueFromComponent( component );
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
    public Integer parse( String value )
    {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return (int) Float.parseFloat(value);
        }
    }
    
}
