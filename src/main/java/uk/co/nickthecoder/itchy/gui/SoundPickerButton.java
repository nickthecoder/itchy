package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.SoundResource;

public class SoundPickerButton extends Button implements ActionListener
{

    private SoundResource soundResource;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private Label label;

    public SoundPickerButton(SoundResource soundResource)
    {
        super();

        this.label = new Label(soundResource == null ? "<none>" : soundResource.getName());
        this.addChild(label);

        this.soundResource = soundResource;
        this.addActionListener(this);
    }

    public SoundResource getValue()
    {
        return this.soundResource;
    }

    public void setValue(SoundResource soundResource)
    {
        this.soundResource = soundResource;

        this.label.setText(soundResource == null ? "<none>" : soundResource.getName());

        this.removeStyle("error");
        for (ComponentValidator validator : this.validators) {
            if (!validator.isValid()) {
                this.addStyle("error");
            }
        }
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void action()
    {
        SoundPicker picker = new SoundPicker(this.getValue())
        {
            @Override
            public void pick(String label, SoundResource value)
            {
                setValue(value);
            }
        };
        picker.show();
    }

    public void addChangeListener(ComponentChangeListener ccl)
    {
        this.changeListeners.add(ccl);
    }

    public void removeChangeListener(ComponentChangeListener ccl)
    {
        this.changeListeners.remove(ccl);
    }

    public void addValidator(ComponentValidator validator)
    {
        this.validators.add(validator);
    }

    public void removeValidator(ComponentValidator validator)
    {
        this.validators.remove(validator);
    }
}
