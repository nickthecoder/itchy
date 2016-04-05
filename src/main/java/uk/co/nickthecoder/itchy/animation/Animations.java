/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Registry;

public class Animations
{
    public static void registerAnimations(Registry registry)
    {
        registry.add(new AlphaAnimation());
        registry.add(new ClipAnimation());
        registry.add(new ColorAnimation());
        registry.add(new CompoundAnimation(true));
        registry.add(new CompoundAnimation(false));
        registry.add(new DelayAnimation());
        registry.add(new ForwardsAnimation());
        registry.add(new FramedAnimation());
        registry.add(new HeadAnimation());
        registry.add(new MoveAnimation());
        registry.add(new TurnAnimation());
        registry.add(new TurnToAnimation());
        registry.add(new ScaleAnimation());
        registry.add(new BeanAnimation());
    };

}
