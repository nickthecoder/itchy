package uk.co.nickthecoder.itchy.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class FontProperty<S> extends AbstractProperty<S, Font>
{
    public FontProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate,
            final ComponentChangeListener listener ) throws IllegalArgumentException,
        SecurityException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
    {
        HashMap<String, Font> hashMap = new HashMap<String, Font>();
        Resources resources = Itchy.singleton.getGame().resources;
        for (String name : resources.fontNames()) {
            hashMap.put(name, resources.getFont(name));
        }

        final PickerButton<Font> pickerButton = new PickerButton<Font>("Fonts",
                this.getValue(subject), hashMap);

        if (autoUpdate) {

            pickerButton.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    try {
                        FontProperty.this.update(subject, pickerButton);
                        if (listener != null) {
                            listener.changed();
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }

        return pickerButton;
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        @SuppressWarnings("unchecked")
        PickerButton<String> pickerButton = (PickerButton<String>) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public Font parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Font result = Itchy.singleton.getGame().resources.getFont(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

}
