/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ScriptedStageConstraint implements ScriptedObject, StageConstraint
{
    private ClassName className;

    private ShimmedScriptLanguage language;

    public final Object stageConstraintScript;

    public ScriptedStageConstraint( ClassName className, ShimmedScriptLanguage language, Object scriptInstance )
    {
        this.className = className;
        this.language = language;
        this.stageConstraintScript = scriptInstance;
    }

    @Override
    public ScriptLanguage getLanguage()
    {
        return this.language;
    }

    public ClassName getClassName()
    {
        return this.className;
    }

    @Override
    public Object getScriptedObject()
    {
        return this.stageConstraintScript;
    }

    @Override
    public double constrainX( double requestedX, double requestedY )
    {
        return this.language.constrainX( this, requestedX, requestedY );
    }

    @Override
    public double constrainY( double requestedX, double requestedY )
    {
        return this.language.constrainY( this, requestedX, requestedY );
    }
}
