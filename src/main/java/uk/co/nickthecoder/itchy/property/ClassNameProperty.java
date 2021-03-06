/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.property;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ClassNameProperty<S> extends Property<S, ClassName>
{
    /**
     * The required type for the property, either Role, SceneDirector, CostumeProperty or Game.
     */
    private Class<?> baseClass;

    public ClassNameProperty(Class<?> klass, String key)
    {
        super(key);
        this.baseClass = klass;
        this.defaultValue = new ClassName(this.baseClass, "");
    }

    @Override
    public Component createUnvalidatedComponent(final S subject)
    {
        ClassName className = this.getSafeValue(subject);
        ScriptManager scriptManager = Itchy.getGame().getScriptManager();

        final ClassNameBox classNameBox = new ClassNameBox(scriptManager, className, this.baseClass);

        return classNameBox;
    }

    @Override
    public void addChangeListener(Component component, ComponentChangeListener listener)
    {
        ClassNameBox classNameBox = (ClassNameBox) component;
        classNameBox.addChangeListener(listener);
    }

    @Override
    public void addValidator(Component component, ComponentValidator validator)
    {
        ClassNameBox classNameBox = (ClassNameBox) component;
        classNameBox.addValidator(validator);
    }

    @Override
    public ClassName getValueFromComponent(Component component)
    {
        ClassNameBox classNameBox = (ClassNameBox) component;
        return classNameBox.getClassName();
    }

    @Override
    public void updateComponentValue(ClassName value, Component component)
    {
        ClassNameBox classNameBox = (ClassNameBox) component;

        classNameBox.setClassName(value);
    }

    @Override
    public ClassName parse(String value)
    {
        return new ClassName(this.baseClass, value);
    }

    @Override
    public String getStringValue(S subject) throws Exception
    {
        return getValue(subject).name;
    }

}
