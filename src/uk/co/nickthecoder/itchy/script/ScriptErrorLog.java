/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

/**
 * All errors from scripts should be logged here, which will make them visible to the game
 * programmer.
 *<p>
 * Scripts will often have errors, which will cause them to fail, but this should never cause
 * Itchy to fail. Instead, the error should be logged here.
 * <p>
 * Later version of ScriptErrorLog will have a GUI component :
 * <p>
 * When an error is logged, an icon will appear on the game's popup layer. Clicking on it will
 * show the error log, as well as the option to clear the log.
 */
public class ScriptErrorLog
{
    ScriptErrorLog()
    {
    }

    public void log( String message )
    {
        System.err.println(message);
    }
}
