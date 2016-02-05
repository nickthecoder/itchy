/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.TextArea;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.TextWidget;

public class StringProperty<S> extends Property<S, String>
{
    public boolean multiLine = false;

    public boolean allowBlank = true;

    public StringProperty(String key)
    {
        super(key);
        this.defaultValue = "";
    }

    /**
     * A fluent way to set the multiLine attribute to true
     * 
     * @return this
     */
    public StringProperty<S> multiLine()
    {
        this.multiLine = true;
        return this;
    }

    /**
     * A fluent way to set the allowBlank attribute
     * 
     * @return this
     */
    public StringProperty<S> allowBlank(boolean value)
    {
        this.allowBlank = value;
        return this;
    }

    /**
     * A fluent way to set the multiLine attribute
     * 
     * @return this
     */
    public StringProperty<S> multiLine(boolean value)
    {
        this.multiLine = value;
        return this;
    }

    @Override
    public Component createUnvalidatedComponent(final S subject)
    {
        if (this.multiLine) {

            TextArea textArea = new TextArea(this.getSafeValue(subject));
            return textArea;

        } else {

            TextBox box = new TextBox(this.getSafeValue(subject));
            return box;
        }
    }

    @Override
    public void addValidator(Component component, ComponentValidator validator)
    {
        TextWidget textWidget = getTextWidgetFromComponent(component);
        textWidget.addValidator(validator);
    }

    @Override
    public void addChangeListener(Component component, ComponentChangeListener listener)
    {
        TextWidget textWidget = getTextWidgetFromComponent(component);
        textWidget.addChangeListener(listener);
    }

    @Override
    public String getValueFromComponent(Component component)
    {
        TextWidget textWidget = getTextWidgetFromComponent(component);
        return textWidget.getText();
    }

    @Override
    public void updateComponentValue(String value, Component component)
    {
        TextWidget textWidget = getTextWidgetFromComponent(component);
        textWidget.setText(value);
    }

    @Override
    public String parse(String value)
    {
        return value;
    }

    /**
     * Returns the TextWidget that was created by createComponent.
     * Subclasses will have different implementations.
     * 
     * @param comonent
     * @return
     */
    protected TextWidget getTextWidgetFromComponent(Component component)
    {
        return (TextWidget) component;
    }

    @Override
    public boolean isValid( Component component )
    {
        if (!allowBlank) {
            String value = getValueFromComponent( component );
            if ( (value != null) && (value.isEmpty()) ) {
                return false;
            }
        }
        
        return super.isValid(component);
    }
}
