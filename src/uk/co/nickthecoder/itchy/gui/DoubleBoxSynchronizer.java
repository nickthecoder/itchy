package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.util.BeanHelper;

public class DoubleBoxSynchronizer implements ComponentChangeListener
{
    private final DoubleBox doubleBox;
    private final Object subject;
    private final String propertyName;

    public DoubleBoxSynchronizer( DoubleBox doubleBox, Object subject, String propertyName )
    {
        this.doubleBox = doubleBox;
        this.subject = subject;
        this.propertyName = propertyName;

        doubleBox.addChangeListener(this);
        try {
            doubleBox.setValue((Double) BeanHelper.getProperty(subject, propertyName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changed()
    {
        try {
            BeanHelper.setProperty(this.subject, this.propertyName, this.doubleBox.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
