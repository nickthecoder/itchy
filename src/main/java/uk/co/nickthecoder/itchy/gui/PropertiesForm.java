/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.StringUtils;

/**
 * Uses an object's properties to build a form, validates it, and updates the subject with the newly entered values.
 */
public class PropertiesForm<S>
{
    public S subject;

    public PlainContainer container;

    public GridLayout grid;

    public boolean autoUpdate;

    private Map<String, Component> componentMap;

    List<Property<S, ?>> properties;

    private Map<String, Object> revertValues;

    public PropertiesForm(S subject, List<Property<S, ?>> properties)
    {
        this.subject = subject;
        this.properties = properties;

        this.container = new PlainContainer();
        this.container.setType("form");
        this.container.setYAlignment(0.5);
        this.grid = new GridLayout(this.container, 2);
        this.container.setLayout(this.grid);
        this.revertValues = new HashMap<String, Object>();
    }

    public Container createForm()
    {
        try {
            this.componentMap = new HashMap<String, Component>();

            for (Property<S, ?> property : this.properties) {
                Component component = createComponent(property);
                this.componentMap.put(property.key, component);
                this.grid.addRow(property.label, hint(component, property.hint));
                this.revertValues.put(property.key, property.getValue(subject));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return this.container;
    }

    /**
     * When using autoUpdate, check if any of the fields have been altered.
     * 
     * @return True iff any of the fields have been changed.
     */
    public boolean hasChanged()
    {
        try {
            for (Property<S, ?> property : this.properties) {
                Object newValue = property.getValue(subject);
                Object oldValue = this.revertValues.get(property.key);
                if (newValue == oldValue)
                    continue;

                if (newValue == null) {
                    return true;
                }

                if (!newValue.equals(oldValue)) {
                    return true;
                }

            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Sets the subject's values back to the values they had when the form was first created.
     */
    public void revert()
    {
        revert(this.subject);
    }

    /**
     * Sets the subject's values back to the values they had when the form was first created.
     * 
     * @param subject
     *            The subject who's values are reverted.
     */
    public void revert(S subject)
    {
        try {
            for (Property<S, ?> property : this.properties) {
                property.setValue(subject, this.revertValues.get(property.key));
                // Update the component, so that ComponentChangeListeners can detect the changed value.
                property.updateComponent(subject, getComponent(property.key));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getRevertValue(String key)
    {
        return revertValues.get(key);
    }

    protected Component createComponent(final Property<S, ?> property)
    {
        final Component component = property.createComponent(this.subject, this.autoUpdate);
        
        return component;
    }

    /**
     * Takes an input component, and adds a optional hint text to its right.
     * 
     * @param input
     *            The input component
     * @param hint
     *            The optional hint (may be null or blank).
     * @return If the hint is blank, then 'input' is returned, otherwise a Container containing 'input' and the hint as
     *         a Label.
     */
    private static Component hint(Component input, String hint)
    {
        if (StringUtils.isBlank(hint)) {
            return input;
        } else {
            PlainContainer container = new PlainContainer();
            container.setYAlignment(0.5);
            container.addChild(input);
            Label label = new Label(hint);
            label.addStyle("hint");
            container.addChild(label);
            return container;
        }
    }

    /**
     * @return True iff all of the properties values entered are valid.
     */
    public boolean isOk()
    {
        for (Property<S, ?> property : this.properties) {
            Component component = this.componentMap.get(property.key);
            if (!property.isValid(component)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the subject with the values entered by the user. No need to call this when using autoUpdate=true.
     * 
     * @throws Exception
     *             If one of the properties couldn't be updated.
     */
    public void update()
    {
        // No need to do anything when autoUpdate, as the changes are made as the user makes them
        // (not on "Ok" button).
        if (this.autoUpdate) {
            return;
        }

        for (Property<S, ?> property : this.properties) {
            Component component = this.componentMap.get(property.key);
            try {
                property.updateSubject(this.subject, component);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the GUI component used to enter the property value.
     * 
     * @param propertyKey
     *            The key for the property. See {@link Property#key}.
     * @return The component.
     */
    public Component getComponent(String propertyKey)
    {
        return this.componentMap.get(propertyKey);
    }

    public void addComponentChangeListener(String propertyKey, ComponentChangeListener listener)
    {
        Component component = this.componentMap.get(propertyKey);
        for (Property<S, ?> property : this.properties) {
            if (property.key.equals(propertyKey)) {
                property.addChangeListener(component, listener);
                return;
            }
        }
    }

    public void addValidator(String propertyKey, ComponentValidator validator)
    {
        Component component = this.componentMap.get(propertyKey);
        for (Property<S, ?> property : this.properties) {
            if (property.key.equals(propertyKey)) {
                property.addValidator(component, validator);
                return;
            }
        }
    }
}
