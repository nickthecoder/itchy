package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;

public class AnimationPickerButton extends Button implements ActionListener
{
    private AnimationResource animationResource;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private Label label;

    public AnimationPickerButton( AnimationResource animationResource)
    {
        super();

        this.label = new Label(  animationResource == null ? "<none>" : animationResource.getName() );
        this.addChild(label);
        
        this.animationResource = animationResource;
        this.addActionListener(this);
    }

    public AnimationResource getValue()
    {
        return this.animationResource;
    }

    public void setValue( AnimationResource animationResource)
    {
        this.animationResource = animationResource;

        this.label.setText( animationResource == null ? "<none>" : animationResource.getName());

        this.removeStyle("error");
        for (ComponentValidator validator : this.validators) {
            if ( ! validator.isValid() ) {
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
        AnimationPicker picker = new AnimationPicker(this.getValue())
        {
            @Override
            public void pick(String label, AnimationResource value)
            {
                setValue(value);                
            }
        };
        picker.show();
    }

    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.changeListeners.add(ccl);
    }

    public void removeChangeListener( ComponentChangeListener ccl )
    {
        this.changeListeners.remove(ccl);
    }
    
    public void addValidator( ComponentValidator validator)
    {
        this.validators.add(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }
}
