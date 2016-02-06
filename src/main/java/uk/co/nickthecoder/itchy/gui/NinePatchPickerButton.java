package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.Surface;

public class NinePatchPickerButton extends Button implements ActionListener
{
    private Resources resources;

    private NinePatch ninePatch;

    private List<ComponentChangeListener> changeListeners = new ArrayList<ComponentChangeListener>();

    private List<ComponentValidator> validators = new ArrayList<ComponentValidator>();

    private ImageComponent img;

    private Label label;

    public NinePatchPickerButton( Resources resources, NinePatch ninePatch)
    {
        super();
        this.layout = new VerticalLayout();
        this.setXAlignment(0.5f);

        if (ninePatch == null) {
            Surface surface = new Surface(1, 1, true);
            this.img = new ImageComponent(surface);
            this.label = new Label("<none>");
        } else {
            this.img = new ImageComponent(ninePatch.getThumbnail());
            this.label = new Label(ninePatch.getName());
        }
        this.addChild(this.img);
        this.addChild(this.label);

        this.resources = resources;
        this.ninePatch = ninePatch;
        this.addActionListener(this);
    }

    public NinePatch getValue()
    {
        return this.ninePatch;
    }

    public void setCompact( boolean value )
    {
        this.img.setVisible(!value);
    }

    public void setValue( NinePatch ninePatch)
    {
        this.ninePatch = ninePatch;

        this.img.setImage(ninePatch.getThumbnail());
        this.label.setText(ninePatch.getName());


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
        NinePatchPicker picker = new NinePatchPicker(this.resources, this.getValue())
        {
            @Override
            public void pick( NinePatch ninePatch)
            {
                setValue(ninePatch);
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
