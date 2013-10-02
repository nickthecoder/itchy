/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.DoubleBox;

public class DoubleProperty<S> extends AbstractProperty<S, Double>
{
    public DoubleProperty( String label, String access, String key )
    {
        super(label, access, key);
    }

    public DoubleProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate,
        final ComponentChangeListener listener ) throws Exception
    {
        final DoubleBox box = new DoubleBox(this.getValue(subject));
        if (autoUpdate) {

            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        DoubleProperty.this.update(subject, box);
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
     * Do don't use the super class, just in case the type of the property value isn't a Double
     * (which would cause a cast exception). This could happen if the property was being retrieved
     * from a dynamically typed language, where we don't directly control attribute types.
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
    public void update( S subject, Component component ) throws Exception
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
    public Double parse( String value )
    {
        return Double.parseDouble(value);
    }

}
