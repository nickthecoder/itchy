/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.property.PropertySubject;

public class CostumeProperties implements PropertySubject<CostumeProperties>, Cloneable
{
	public CostumeProperties( )
	{
	}
	
	private final static List<AbstractProperty<CostumeProperties, ?>> EMPTY_PROPERTIES =
			new ArrayList<AbstractProperty<CostumeProperties, ?>>();
	
	@Override
    public List<AbstractProperty<CostumeProperties, ?>> getProperties()
    {
		return  EMPTY_PROPERTIES;
    }

}
