package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.CostumePickerButton;

public class CostumeProperty<S> extends Property<S, Costume>
{
    public CostumeProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject)
    {
        Costume costume = this.getSafeValue(subject);

        final CostumePickerButton pickerButton = new CostumePickerButton(Itchy.getGame().resources, costume);
        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        CostumePickerButton button = (CostumePickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        CostumePickerButton button = (CostumePickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public Costume getValueFromComponent( Component component )
    {
        CostumePickerButton pickerButton = (CostumePickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( Costume value, Component component )
    {
        CostumePickerButton pickerButton = (CostumePickerButton) component;
        pickerButton.setValue(value);
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

}
