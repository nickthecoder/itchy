package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.InvocationTargetException;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.IntegerBox;

public class GIntegerProperty<S> extends GProperty<S, Integer>
{
    public GIntegerProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate,
            final ComponentChangeListener listener ) throws IllegalArgumentException,
        SecurityException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        final IntegerBox box = new IntegerBox(this.getValue(subject));
        if (autoUpdate) {

            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        GIntegerProperty.this.update(subject, box);
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
