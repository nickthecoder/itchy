/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.animation.ProfilePickerButton;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;

public class ProfileProperty<S> extends AbstractProperty<S, Profile>
{

    public ProfileProperty( String label, String access, String key )
    {
        super( label, access, key );
    }
    public ProfileProperty( String label, String access )
    {
        super(label, access);
    }

    @Override
    public Component createComponent( S subject, boolean autoUpdate,
        ComponentChangeListener listener ) throws Exception
    {
        ProfilePickerButton button = new ProfilePickerButton(getValue(subject));
        return button;
    }

    @Override
    public void update( S subject, Component component ) throws Exception
    {
        ProfilePickerButton button = (ProfilePickerButton) component;
        setValue(subject, button.getValue());
    }

    @Override
    public Profile parse( String value )
    {
        Profile profile = NumericAnimation.getProfile(value);
        if ( profile == null ) {
            throw new RuntimeException( "Named Profile not found : " + value );
        }
        return profile;
    }
        
}
