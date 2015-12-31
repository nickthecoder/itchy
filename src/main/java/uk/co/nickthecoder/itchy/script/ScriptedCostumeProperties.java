/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.List;

import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ScriptedCostumeProperties extends CostumeProperties implements ScriptedObject
{
    private ShimmedScriptLanguage language;

    private ClassName className;

    public final Object costumePropertiesScript;

    public ScriptedCostumeProperties( ClassName className, ShimmedScriptLanguage language,
        Object scriptInstance )
    {
        this.language = language;
        this.className = className;
        this.costumePropertiesScript = scriptInstance;
    }

    @Override
    public ClassName getClassName()
    {
        return this.className;
    }

    @Override
    public List<AbstractProperty<CostumeProperties, ?>> getProperties()
    {
        return this.language.getProperties(this);
    }

    @Override
    public Object getScriptedObject()
    {
        return this.costumePropertiesScript;
    }

    @Override
    public ScriptLanguage getLanguage()
    {
        return this.language;
    }
}
