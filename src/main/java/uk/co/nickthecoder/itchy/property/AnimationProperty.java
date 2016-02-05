package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.AnimationPickerButton;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;

public class AnimationProperty<S> extends Property<S, AnimationResource>
{
    public AnimationProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject )
    {
        AnimationResource animationResource = this.getSafeValue(subject);

        final AnimationPickerButton pickerButton = new AnimationPickerButton( animationResource);
        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        AnimationPickerButton button = (AnimationPickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        AnimationPickerButton button = (AnimationPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public AnimationResource getValueFromComponent( Component component )
    {
        AnimationPickerButton pickerButton = (AnimationPickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( AnimationResource value, Component component )
    {
        AnimationPickerButton pickerButton = (AnimationPickerButton) component;
        pickerButton.setValue(value);
    }

    @Override
    public AnimationResource parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        AnimationResource result = Itchy.getGame().resources.getAnimationResource(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        AnimationResource value = this.getValue(subject);

        return value.getName();
    }

}
