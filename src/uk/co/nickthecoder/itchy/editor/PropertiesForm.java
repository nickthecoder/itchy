/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.StringUtils;

/**
 * Uses an object's properties to build a form, validates it, and updates the subject with the newly
 * entered values.
 */
public class PropertiesForm<S>
{
    public S subject;

    public Container container;

    public GridLayout grid;

    public boolean autoUpdate;

    private Map<String, Component> componentMap;

    List<AbstractProperty<S, ?>> properties;

    public PropertiesForm( S subject, List<AbstractProperty<S, ?>> properties )
    {
        this.subject = subject;
        this.properties = properties;

        this.container = new Container();
        this.container.setType("form");
        this.grid = new GridLayout(this.container, 2);
        this.container.setLayout(this.grid);
    }

    public Container createForm()
    {
        this.componentMap = new HashMap<String, Component>();

        for (AbstractProperty<S, ?> property : this.properties) {
            Component component = property.createComponent(this.subject, this.autoUpdate);
            this.componentMap.put(property.key, component);
            this.grid.addRow(property.label, hint(component, property.hint));
        }

        return this.container;
    }

    /**
     * Takes an input component, and adds a optional hint text to its right.
     * 
     * @param input
     *        The input component
     * @param hint
     *        The optional hint (may be null or blank).
     * @return If the hint is blank, then 'input' is returned, otherwise a Container containing
     *         'input' and the hint as a Label.
     */
    private static Component hint( Component input, String hint )
    {
        if (StringUtils.isBlank(hint)) {
            return input;
        } else {
            Container container = new Container();
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
        return this.getErrorMessage() == null;
    }

    /**
     * @return An error message is one or more properties have been entered incorrectly. Null if all
     *         properties are ok.
     */
    public String getErrorMessage()
    {
        for (AbstractProperty<S, ?> property : this.properties) {
            Component component = this.componentMap.get(property.key);
            String errorMessage = property.getErrorText(component);
            if (errorMessage != null) {
                return errorMessage;
            }
        }
        return null;
    }

    /**
     * Updates the subject with the values entered by the user. No need to call this when using
     * autoUpdate=true.
     * 
     * @throws Exception
     *         If one of the properties couldn't be updated.
     */
    public void update()
    {
        // No need to do anything when autoUpdate, as the changes are made as the user makes them
        // (not on "Ok" button).
        if (this.autoUpdate) {
            return;
        }

        for (AbstractProperty<S, ?> property : this.properties) {
            Component component = this.componentMap.get(property.key);
            try {
                property.update(this.subject, component);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the GUI component used to enter the property value.
     * 
     * @param propertyKey
     *        The key for the property. See {@link AbstractProperty#key}.
     * @return The component.
     */
    public Component getComponent( String propertyKey )
    {
        return this.componentMap.get(propertyKey);
    }

    public void addComponentChangeListener( String propertyKey, ComponentChangeListener listener )
    {
        Component component = this.componentMap.get(propertyKey);
        for (AbstractProperty<S, ?> property : this.properties) {
            if (property.key.equals(propertyKey)) {
                property.addChangeListener(component, listener);
                return;
            }
        }
    }
}