/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Stage;

/**
 * Creates duplicate images of the Actor as it moves, which gradually fade out. So you can see faded out versions of what the actor looked
 * liked on previous frames.
 * <p>
 * When an old image fades out completely, it emits the death event {@link #ONION_SKIN_DEATH}.
 * <p>
 * This is named after the stop frame animation technique, where a thin pieces of paper are stacked, with each piece of paper holding one
 * frame, but the paper is so thin, that the layers below can still be seen.
 */
public class OnionSkin extends Companion
{
    public static String ONION_SKIN_DEATH = "onionSkinDeath";

    private int frameCounter = 0;

    public int every = 5; // Create a new onion skin image every N frames.

    public double fade = 5; // Fade each onion skin image by this much each frame

    public OnionSkin( Actor following )
    {
        super(following);
    }


    @Override
    public void tick()
    {
        this.frameCounter++;
        if (this.frameCounter >= this.every) {
            this.frameCounter = 0;
            createOnionSkin();
        }
    }


    protected void createOnionSkin()
    {
        Appearance appearance = this.source.getAppearance();
        
        Actor actor = new Actor(this.source.getCostume());
        actor.getAppearance().setPose(appearance.getPose());
        actor.getAppearance().setDirection(this.source.getDirection());

        actor.moveTo(this.source);
        actor.getAppearance().setAlpha(this.alpha / 255 * appearance.getAlpha());

        Role role = new OnionSkinFade();
        actor.setRole(role);
        actor.setZOrder(this.source.getZOrder() - 1);
        Stage stage = this.source.getStage();
        if (stage != null) {
            stage.add(actor);
        }
    }

    @Override
    public Actor createActor()
    {
        Actor result = super.createActor();
        result.getAppearance().setAlpha(0);
        return result;
    }

    public class OnionSkinFade extends AbstractRole
    {
        @Override
        public void tick()
        {
            double alpha = getActor().getAppearance().getAlpha() - OnionSkin.this.fade;
            if (alpha <= 0) {
                deathEvent("onionSkinDeath");
            } else {
                getActor().getAppearance().setAlpha(alpha);
            }
        }
    }
    

    public static abstract class AbstractOnionSkinBuilder<C extends OnionSkin, B extends AbstractOnionSkinBuilder<C, B>>
        extends Companion.AbstractCompanionBuilder<C, B>
    {

        public B every( int every )
        {
            this.companion.every = every;
            return getThis();
        }

        public B fade( double fade )
        {
            this.companion.fade = fade;
            return getThis();
        }

    }
}
