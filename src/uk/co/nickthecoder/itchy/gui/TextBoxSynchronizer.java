package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.util.BeanHelper;

public class TextBoxSynchronizer implements ComponentChangeListener
{
    private final TextBox textBox;
    private final Object subject;
    private final String propertyName;

    public TextBoxSynchronizer( TextBox textBox, Object subject, String propertyName )
    {
        this.textBox = textBox;
        this.subject = subject;
        this.propertyName = propertyName;

        textBox.addChangeListener(this);
        try {
            textBox.setEntryText((String) BeanHelper.getProperty(subject, propertyName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changed()
    {
        try {
            BeanHelper.setProperty(this.subject, this.propertyName, this.textBox.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
