/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public class CostumeProperties implements PropertySubject<CostumeProperties>, Cloneable
{
    private Costume costume;
    
	public CostumeProperties( )
	{
	}
	
	/**
	 * This should only be set by Itchy, not by any game code.
	 * It is set immediately after the constructor is called. Its done like this to make the constructor
	 * easy for game programers. i.e. There is no need for boiler plate code to call super.
	 */
	void setCostume( Costume costume )
	{
	    this.costume = costume;
	}
	
	public Costume getCostume()
	{
	    return this.costume;
	}
	
	private final static List<Property<CostumeProperties, ?>> EMPTY_PROPERTIES =
			new ArrayList<Property<CostumeProperties, ?>>();
	
	@Override
    public List<Property<CostumeProperties, ?>> getProperties()
    {
		return  EMPTY_PROPERTIES;
    }

}
