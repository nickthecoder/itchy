package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.KeyInputPicker;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class InputProperty<S> extends StringProperty<S>
{
    public InputProperty(String key)
    {
        super(key);
    }

    @Override
    public Component createComponent(final S subject, boolean autoUpdate)
    {

        PlainContainer container = new PlainContainer();
        container.addStyle("combo");
        
        final TextBox box = new TextBox(this.getSafeValue(subject));
        this.addChangeListener(box, subject, autoUpdate);

        GuiButton keysButton = new GuiButton("+");
        keysButton.addActionListener(new ActionListener()
        {

            @Override
            public void action()
            {
                KeyInputPicker keyPicker = new KeyInputPicker()
                {
                    @Override
                    public void pick(KeyInput keyInput)
                    {
                        String old = box.getText().trim();
                        if (old.length() > 0) {
                            old = old + ",";
                        }
                        box.setText(old + keyInput.toString());
                    }
                };
                keyPicker.show();
            }

        });

        container.addChild(box);
        container.addChild(keysButton);

        return container;
    }
}
