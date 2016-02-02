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
    public Component createUnvalidatedComponent( final S subject, final boolean autoUpdate )
    {
        SoundResource soundResource = this.getSafeValue(subject);

        final SoundPickerButton pickerButton = new SoundPickerButton( soundResource);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        SoundProperty.this.updateSubject(subject, pickerButton);
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
    public SoundResource getValueFromComponent( Component component )
    {
        SoundPickerButton pickerButton = (SoundPickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( SoundResource value, Component component )
    {
        SoundPickerButton pickerButton = (SoundPickerButton) component;
        pickerButton.setValue(value);
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

}
