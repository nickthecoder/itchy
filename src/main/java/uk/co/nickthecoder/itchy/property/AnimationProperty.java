package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.AnimationPickerButton;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.PosePickerButton;

public class AnimationProperty<S> extends Property<S, AnimationResource>
{
    public AnimationProperty( String key )
    {
        super(key);
    }

    @Override
    public AnimationResource getDefaultValue()
    {
        return null;
    }

    @Override
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        AnimationResource animationResource = this.getSafeValue(subject);

        final AnimationPickerButton pickerButton = new AnimationPickerButton( animationResource);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        AnimationProperty.this.update(subject, pickerButton);
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
        AnimationPickerButton pickerButton = (AnimationPickerButton) component;
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
        AnimationPickerButton pickerButton = (AnimationPickerButton) component;
        pickerButton.setValue(this.getValue(subject));
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

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
