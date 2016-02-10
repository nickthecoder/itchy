/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Itchy;

public class Animations
{
    public static void registerAnimations()
    {
        Itchy.registry.add(new AlphaAnimation());
        Itchy.registry.add(new ClipAnimation());
        Itchy.registry.add(new ColorAnimation());
        Itchy.registry.add(new CompoundAnimation(true));
        Itchy.registry.add(new CompoundAnimation(false));
        Itchy.registry.add(new DelayAnimation());
        Itchy.registry.add(new ForwardsAnimation());
        Itchy.registry.add(new FramedAnimation());
        Itchy.registry.add(new HeadAnimation());
        Itchy.registry.add(new MoveAnimation());
        Itchy.registry.add(new TurnAnimation());
        Itchy.registry.add(new TurnToAnimation());
        Itchy.registry.add(new ScaleAnimation());
        Itchy.registry.add(new BeanAnimation());
    };

}
