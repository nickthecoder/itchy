package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.NinePatchPickerButton;
import uk.co.nickthecoder.itchy.util.NinePatch;

public class NinePatchProperty<S> extends Property<S, NinePatch>
{
    public NinePatchProperty( String key )
    {
        super(key);
    }

    @Override
    public Component createUnvalidatedComponent( final S subject )
    {
        Resources resources = Itchy.getGame().resources;

        NinePatch ninePatch = this.getSafeValue(subject);

        final NinePatchPickerButton pickerButton = new NinePatchPickerButton(resources, ninePatch);
        pickerButton.setCompact(true);

        return pickerButton;
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        NinePatchPickerButton button = (NinePatchPickerButton) component;
        button.addChangeListener(listener);
    }
    
    @Override
    public void addValidator( Component component, ComponentValidator validator )
    {
        NinePatchPickerButton button = (NinePatchPickerButton) component;
        button.addValidator(validator);
    }

    @Override
    public NinePatch getValueFromComponent( Component component )
    {
        NinePatchPickerButton pickerButton = (NinePatchPickerButton) component;
        return pickerButton.getValue();
    }

    @Override
    public void updateComponentValue( NinePatch value, Component component )
    {
        NinePatchPickerButton pickerButton = (NinePatchPickerButton) component;
        pickerButton.setValue(value);
    }

    @Override
    public NinePatch parse( String value )
    {
        if ("".equals(value) || (value == null)) {
            return null;
        }
        NinePatch result = Itchy.getGame().resources.getNinePatch(value);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    @Override
    public String getStringValue( S subject ) throws Exception
    {
        NinePatch ninePatch = this.getValue(subject);
        
        return ninePatch == null ? "" : ninePatch.getName();
    }
}
