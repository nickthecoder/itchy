/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.TextArea;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.TextWidget;

public class StringProperty<S> extends Property<S, String>
{
    private boolean multiLine = false;
    
    public StringProperty( String key )
    {
        super(key);
    }

    @Override
    public String getDefaultValue()
    {
        return "";
    }

    /**
     * A fluent way to set the multiLine attribute to true
     * @return this
     */
    public StringProperty<S> multiLine()
    {
        this.multiLine = true;
        return this;
    }
    
    /**
     * A fluent way to set the multiLine attribute
     * @return this
     */
    public StringProperty<S> multiLine(boolean value)
    {
        this.multiLine = value;
        return this;
    }
    
    @Override
    public Component createComponent( final S subject, boolean autoUpdate )
    {
        if (this.multiLine) {
        
            TextArea textArea = new TextArea(this.getSafeValue(subject));
            this.addChangeListener(textArea, subject, autoUpdate);
            return textArea;
            
        } else {
            
            TextBox box = new TextBox(this.getSafeValue(subject));
            this.addChangeListener(box, subject, autoUpdate);
            return box;
        }
    }

    protected void addChangeListener( final TextWidget widget, final S subject, boolean autoUpdate )
    {
        if (autoUpdate) {
            widget.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        if (! widget.hasStyle("error")) {
                            StringProperty.this.updateSubject(subject, (Component) widget);
                        }
                    } catch (Exception e) {
                        // Do nothing
                    }
                }
            });
        }
    }

    @Override
    public void addValidator( Component component, ComponentValidator validator)
    {
        TextWidget textWidget = (TextWidget) component;
        textWidget.addValidator(validator);
    }

    @Override
    public void addChangeListener( Component component, ComponentChangeListener listener )
    {
        TextWidget textWidget = (TextWidget) component;
        textWidget.addChangeListener(listener);
    }

    @Override
    public void updateSubject( S subject, Component component ) throws Exception
    {
        TextWidget textWidget = (TextWidget) component;
        try {
            this.setValue(subject, textWidget.getText());
            textWidget.removeStyle("error");
        } catch (Exception e) {
            textWidget.addStyle("error");
            throw e;
        }
    }

    @Override
    public void updateComponent( S subject, Component component ) throws Exception
    {
        TextBox textBox = (TextBox) component;
        textBox.setText(this.getValue(subject));
    }

    @Override
    public String parse( String value )
    {
        return value;
    }

    @Override
    public String getErrorText( Component component )
    {
        return null;
    }

}
