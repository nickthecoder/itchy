/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.IntegerBox;

public class IntegerProperty<S> extends AbstractProperty<S, Integer>
{
    public IntegerProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    public IntegerProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate,
        final ComponentChangeListener listener )
    {
        final IntegerBox box = new IntegerBox(this.getSafeValue(subject));
        if (autoUpdate) {

            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        IntegerProperty.this.update(subject, box);
                        if (listener != null) {
                            listener.changed();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
        return box;
    }

    /**
     * Do don't use the super class, just in case the type of the property value isn't an Integer
     * (which would cause a cast exception). This could happen if the property was being retrieved
     * from a dynamically typed language, where we don't directly control attribute types.
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
            System.err.println("Expected Integer but found : " + value + " : " +
                value.getClass().getName());
            return Integer.parseInt(value.toString());
        }

    }

    @Override
    public Integer getDefaultValue()
    {
        return 0;
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        IntegerBox integerBox = (IntegerBox) component;
        try {
            this.setValue(subject, integerBox.getValue());
            integerBox.removeStyle("error");
        } catch (Exception e) {
            integerBox.addStyle("error");
        }
    }

    @Override
    public Integer parse( String value )
    {
        return Integer.parseInt(value);
    }

}
