package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.SoundPickerButton;

public class SoundProperty<S> extends Property<S, SoundResource>
{
    public SoundProperty( String key )
    {
        super(key);
    }

    @Override
    public SoundResource getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        SoundResource soundResource = this.getSafeValue(subject);

        final SoundPickerButton pickerButton = new SoundPickerButton( soundResource);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        SoundProperty.this.update(subject, pickerButton);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        SoundPickerButton button = (SoundPickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        SoundPickerButton button = (SoundPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        SoundPickerButton pickerButton = (SoundPickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            e.printStackTrace();
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        SoundPickerButton pickerButton = (SoundPickerButton) component;
        pickerButton.setValue(this.getValue(subject));
    }

    @Override
    public SoundResource parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        SoundResource result = Itchy.getGame().resources.getSound(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        SoundResource value = this.getValue(subject);

        return value.getName();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }


}
