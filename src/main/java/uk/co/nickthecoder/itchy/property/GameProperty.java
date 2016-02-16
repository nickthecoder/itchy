package uk.co.nickthecoder.itchy.property;

import java.io.File;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.GamePickerButton;

public class GameProperty<S> extends Property<S, File>
{
    public GameProperty(String access)
    {
        super(access);
    }

    @Override
    public Component createUnvalidatedComponent(S subject)
    {
        File file = this.getSafeValue(subject);
        return new GamePickerButton(file);
    }

    @Override
    public void addChangeListener(Component component, ComponentChangeListener listener)
    {
        GamePickerButton picker = (GamePickerButton) component;
        picker.addChangeListener(listener);
    }

    @Override
    public void addValidator(Component component, ComponentValidator validator)
    {
        GamePickerButton picker = (GamePickerButton) component;
        picker.addValidator(validator);
    }

    @Override
    public File getValueFromComponent(Component component) throws Exception
    {
        GamePickerButton picker = (GamePickerButton) component;
        return picker.getValue();
    }

    @Override
    public void updateComponentValue(File value, Component component)
    {
        GamePickerButton picker = (GamePickerButton) component;
        picker.setValue(value);
    }

    @Override
    public File parse(String stringValue)
    {
        return new File(stringValue);
    }

}
