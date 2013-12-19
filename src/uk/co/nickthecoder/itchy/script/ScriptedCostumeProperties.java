/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ScriptedCostumeProperties extends CostumeProperties
{
    private final static HashMap<String, List<AbstractProperty<CostumeProperties, ?>>> allProperties = new HashMap<String, List<AbstractProperty<CostumeProperties, ?>>>();

    public final ScriptProperties propertyValues;

    private ClassName className;

    public final Object values;

    public static void addProperty(
        String name, String propertyName, String label, Class<?> klass )
    {
        List<AbstractProperty<CostumeProperties, ?>> properties = allProperties.get(name);
        if (properties == null) {
            properties = new ArrayList<AbstractProperty<CostumeProperties, ?>>();
            allProperties.put(name, properties);
        } else {
            // If the property was previously defined, remove it.
            for (Iterator<AbstractProperty<CostumeProperties, ?>> i = properties.iterator(); i.hasNext();) {
                AbstractProperty<CostumeProperties, ?> property = i.next();
                if (property.key.equals(propertyName)) {
                    i.remove();
                }
            }

        }

        AbstractProperty<CostumeProperties, ?> property = AbstractProperty.createProperty(
            klass, "propertyValues." + propertyName, propertyName, label, true, false, true);
        if (property != null) {
            properties.add(property);
        }

    }

    public ScriptedCostumeProperties( ClassName className, ScriptLanguage language,
        Object scriptInstance )
    {
        this.className = className;
        this.propertyValues = new ScriptProperties(language, scriptInstance);
        this.values = scriptInstance;
    }

    @Override
    public List<AbstractProperty<CostumeProperties, ?>> getProperties()
    {
        String name = ScriptManager.getName(this.className);

        List<AbstractProperty<CostumeProperties, ?>> result = allProperties.get(name);
        if (result == null) {
            result = new ArrayList<AbstractProperty<CostumeProperties, ?>>();
            allProperties.put(name, result);
        }
        return result;
    }

}
