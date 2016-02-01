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
    public Component createComponent( final S subject, final boolean autoUpdate )
    {
        Resources resources = Itchy.getGame().resources;

        NinePatch ninePatch = this.getSafeValue(subject);

        final NinePatchPickerButton pickerButton = new NinePatchPickerButton(resources, ninePatch);
        pickerButton.setCompact(true);

        if (autoUpdate) {

            pickerButton.addChangeListener(new ComponentChangeListener() {

                @Override
                public void changed()
                {
                    try {
                        NinePatchProperty.this.updateSubject(subject, pickerButton);
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }

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
    public void updateSubject( S subject, Component component ) throws Exception
    {
        NinePatchPickerButton pickerButton = (NinePatchPickerButton) component;
        try {
            this.setValue(subject, pickerButton.getValue());
            pickerButton.removeStyle("error");
        } catch (Exception e) {
            pickerButton.addStyle("error");
        }
    }

    @Override
    public void updateComponent( S subject, Component component ) throws Exception
    {
        NinePatchPickerButton pickerButton = (NinePatchPickerButton) component;
        NinePatch ninePatch = this.getValue(subject);
        pickerButton.setValue(ninePatch);
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

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
