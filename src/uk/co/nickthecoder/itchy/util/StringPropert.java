package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.InvocationTargetException;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class StringPropert<S> extends AbstractProperty<S, String>
{
    public StringPropert( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( final S subject, boolean autoUpdate,
            final ComponentChangeListener listener ) throws IllegalArgumentException,
        SecurityException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        final TextBox box = new TextBox(this.getValue(subject));
        if (autoUpdate) {
            box.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        StringPropert.this.update(subject, box);
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
        TextBox textBox = (TextBox) component;
        try {
            this.setValue(subject, textBox.getText());
            textBox.removeStyle("error");
        } catch (Exception e) {
            textBox.addStyle("error");
            throw e;
        }
    }

    @Override
    public String parse( String value )
    {
        return value;
    }

}
