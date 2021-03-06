/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

public class XMLException extends Exception
{
    private static final long serialVersionUID = 7745217099603670954L;

    public XMLException( Exception e )
    {
        super(e);
    }

    public XMLException( String message )
    {
        super(message);
    }

}
