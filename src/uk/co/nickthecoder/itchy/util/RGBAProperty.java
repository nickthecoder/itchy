package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.InvocationTargetException;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;

public class RGBAProperty<S> extends AbstractProperty<S, RGBA>
{
    private final boolean allowNull;

    private final boolean includeAlpha;

    public RGBAProperty( String label, String access, boolean allowNull, boolean includeAlpha )
    {
        super(label, access);
        this.allowNull = allowNull;
        this.includeAlpha = includeAlpha;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate,
            final ComponentChangeListener listener ) throws IllegalArgumentException,
        SecurityException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        RGBA color = this.getValue(subject);
        final TextBox textBox = new TextBox(color == null ? ""
                : this.includeAlpha ? color.getRGBACode() : color.getRGBCode());

        if (autoUpdate) {

            textBox.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        RGBAProperty.this.update(subject, textBox);
                        if (listener != null) {
                            listener.changed();
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }

        return textBox;
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        TextBox textBox = (TextBox) component;
        try {
            this.setValue(subject, this.parse(textBox.getText()));
            textBox.removeStyle("error");
        } catch (Exception e) {
            textBox.addStyle("error");
        }
    }

    @Override
    public RGBA parse( String value )
    {
        if (this.allowNull && ("".equals(value))) {
            return null;
        }

        try {
            RGBA result = RGBA.parse(value);
            if (!this.includeAlpha) {
                if (result.a != 255) {
                    throw new NullPointerException();
                }
            }
            return result;
        } catch (JameException e) {
            throw new NullPointerException();
        }
    }

}
