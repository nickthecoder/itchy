/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.editor.SceneDesignerBehaviour;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.StringUtils;
import uk.co.nickthecoder.jame.RGBA;

public abstract class SceneActor implements Cloneable
{
    public static SceneActor createSceneActor( Actor actor )
    {
        if (actor.getAppearance().getPose() instanceof TextPose) {
            return new TextSceneActor(actor);
        } else if (actor.getCostume() != null) {
            return new CostumeSceneActor(actor);
        } else {
            return null;
        }
    }

    public int x;

    public int y;

    double direction;

    double heading;

    double alpha;

    double scale;

    int zOrder;
    
    public String startEvent = "default";

    public ClassName behaviourClassName;

    public RGBA colorize;

    public double activationDelay;

    public HashMap<String, Object> customProperties = new HashMap<String, Object>();

    protected SceneActor()
    {
        this.alpha = 255;
    }

    protected SceneActor( Actor actor )
    {
        this.x = (int) actor.getX();
        this.y = (int) actor.getY();
        this.alpha = actor.getAppearance().getAlpha();
        this.direction = actor.getAppearance().getDirection();
        this.heading = actor.getHeading();
        this.zOrder = actor.getZOrder();
        this.scale = actor.getAppearance().getScale();
        this.behaviourClassName = ((SceneDesignerBehaviour) actor.getBehaviour())
            .getBehaviourClassName();
        this.colorize = actor.getAppearance().getColorize() == null ? null : new RGBA(actor
            .getAppearance().getColorize());
        this.activationDelay = actor.getActivationDelay();
        this.startEvent = actor.getStartEvent();

        Behaviour actualBehaviour = ((SceneDesignerBehaviour) actor.getBehaviour()).actualBehaviour;

        for (AbstractProperty<Behaviour, ?> property : actualBehaviour.getProperties()) {
            try {
                Object value = property.getValue(actualBehaviour);
                this.customProperties.put(property.key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateActor( Actor actor, Resources resources, boolean designMode )
    {
        actor.moveTo(this.x, this.y);
        actor.setZOrder(this.zOrder);
        actor.getAppearance().setAlpha(this.alpha);
        actor.getAppearance().setDirection(this.direction);
        actor.setHeading(this.heading);
        actor.getAppearance().setScale(this.scale);
        actor.getAppearance().setColorize(this.colorize == null ? null : new RGBA(this.colorize));
        ClassName behaviourClassName = this.behaviourClassName;
        actor.setActivationDelay(this.activationDelay);

        if (behaviourClassName == null) {
            if (actor.getCostume() == null) {
                behaviourClassName = new ClassName(NullBehaviour.class.getName());
            } else {
                behaviourClassName = actor.getCostume().behaviourClassName;
            }
        }

        Behaviour actualBehaviour;

        if (designMode) {

            SceneDesignerBehaviour behaviour = new SceneDesignerBehaviour();
            actor.setBehaviour(behaviour);

            try {
                behaviour.setBehaviourClassName(resources, behaviourClassName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            actualBehaviour = behaviour.actualBehaviour;

        } else {

            try {
                actualBehaviour = Behaviour.createBehaviour(resources, behaviourClassName);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        for (AbstractProperty<Behaviour, ?> property : actualBehaviour.getProperties()) {
            Object value = this.customProperties.get(property.key);
            if (value != null) {
                try {
                    property.setValue(actualBehaviour, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!designMode) {
            actor.setBehaviour(actualBehaviour);
        }

    }

    public abstract Actor createActor( Resources resources, boolean designActor );

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SceneActor result = (SceneActor) super.clone();

        result.customProperties = new HashMap<String, Object>();
        for (String key : this.customProperties.keySet()) {
            result.customProperties.put(key, this.customProperties.get(key));
        }

        return result;
    }

    public SceneActor copy()
    {
        try {
            return (SceneActor) this.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if (!(obj instanceof SceneActor)) {
            return false;
        }
        SceneActor other = (SceneActor) obj;

        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.direction != other.direction) {
            return false;
        }
        if (this.heading != other.heading) {
            return false;
        }
        if (this.scale != other.scale) {
            return false;
        }
        if (this.activationDelay != other.activationDelay) {
            return false;
        }
        if (!StringUtils.equals(this.startEvent, other.startEvent)) {
            return false;
        }
        if (!StringUtils.equals(this.behaviourClassName, other.behaviourClassName)) {
            return false;
        }
        if (!StringUtils.equals(this.colorize, other.colorize)) {
            return false;
        }

        return this.customProperties.equals(other.customProperties);
    }

}
