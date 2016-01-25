package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.CostumePickerButton;
import uk.co.nickthecoder.itchy.gui.PosePickerButton;

public class CostumeProperty<S> extends Property<S, Costume>
{
    public CostumeProperty( String key )
    {
        super(key);
    }

    @Override
    public Costume getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Costume costume = this.getSafeValue(subject);

        final CostumePickerButton pickerButton = new CostumePickerButton(Itchy.getGame().resources, costume);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        CostumeProperty.this.update(subject, pickerButton);
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
        PosePickerButton button = (PosePickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        PosePickerButton button = (PosePickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        CostumePickerButton pickerButton = (CostumePickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void refresh( S subject, Component component ) throws Exception
    {
        CostumePickerButton pickerButton = (CostumePickerButton) component;
        pickerButton.setValue(this.getValue(subject));
    }

    @Override
    public Costume parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        Costume result = Itchy.getGame().resources.getCostume(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        Costume value = this.getValue(subject);

        return value.getName();
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
