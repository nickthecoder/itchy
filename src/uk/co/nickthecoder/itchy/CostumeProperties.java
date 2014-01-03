/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class CostumeProperties implements PropertySubject<CostumeProperties>
{
    public static CostumeProperties createProperties( ScriptManager scriptManager, ClassName className )
    {
        try {
            if (ScriptManager.isScript(className)) {
                return scriptManager.createCostumeProperties(className);
            } else {
                @SuppressWarnings("unchecked")
                Class<CostumeProperties> klass = (Class<CostumeProperties>) Class.forName(className.name);
                return klass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CostumeProperties();
        }
    }

    public static boolean isValidClassName( String className )
    {

        try {
            Class<?> klass = Class.forName(className);
            klass.asSubclass(CostumeProperties.class);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<AbstractProperty<CostumeProperties, ?>> getProperties()
    {
        return AbstractProperty.findAnnotations(this.getClass());
    }
    
    public ClassName getClassName()
    {
        return new ClassName( CostumeProperties.class, this.getClass().getName() );
    }
}
