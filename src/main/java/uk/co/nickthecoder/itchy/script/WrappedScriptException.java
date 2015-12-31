/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import javax.script.ScriptException;

/**
 * Created to wrap jython exceptions so that debugging  will be easier.
 *
 */
public class WrappedScriptException extends ScriptException
{
    private static final long serialVersionUID = -3640222493801595275L;
    
    private String extraMessage;
    
    public WrappedScriptException( Exception e )
    { 
        super(e);
    }
    
    public WrappedScriptException( Exception e, String message )
    {
        super(e);
        this.extraMessage = message;
    }
    
    public String getMessage()
    {
        if( extraMessage != null) {
            return extraMessage;
        } else {
            return super.getMessage();
        }
    }
}
