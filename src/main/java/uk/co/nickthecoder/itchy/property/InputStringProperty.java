package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.InputInterface;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.InputPicker;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.TextWidget;

public class InputStringProperty<S> extends StringProperty<S>
{
    public InputStringProperty(String key)
    {
        super(key);
    }

    @Override
    public String getDefaultValue()
    {
        return "";
    }
    
    @Override
    public Component createUnvalidatedComponent(final S subject)
    {
        final TextWidget textWidget = (TextWidget) super.createUnvalidatedComponent(subject);
        
        PlainContainer container = new PlainContainer();
        container.addStyle("combo");
        
        GuiButton keysButton = new GuiButton("+");
        keysButton.addActionListener(new ActionListener()
        {

            @Override
            public void action()
            {
                InputPicker keyPicker = new InputPicker()
                {
                    @Override
                    public void pick(InputInterface input)
                    {
                        String old = textWidget.getText().trim();
                        if (old.length() > 0) {
                            old = old + ",";
                        }
                        textWidget.setText(old + input.toString());
                    }
                };
                keyPicker.show();
            }

        });

        container.addChild(textWidget);
        container.addChild(keysButton);

        return container;
    }
    
    @Override
    public boolean isValid( Component component )
    {
        TextWidget textWidget = getTextWidgetFromComponent(component);
        try {
            new Input().setKeysString(textWidget.getText());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected TextWidget getTextWidgetFromComponent(Component component)
    {
        return (TextWidget) ((Container) component).getChildren().get(0);
    }

}
