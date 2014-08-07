/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.HashMap;
import java.util.Map;

import uk.co.nickthecoder.itchy.editor.SceneDesignerRole;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.role.PlainRole;
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

    public String id;
    
    public Costume costume;

    public int x;

    public int y;

    double direction;

    double heading;

    double alpha;

    double scale;

    int zOrder;

    public String startEvent = "default";

    public ClassName roleClassName;

    public ClassName makeupClassName;

    public RGBA colorize;

    public double activationDelay;

    public Map<String, String> customPropertyStrings = new HashMap<String, String>();

    public Map<String, String> makeupPropertyStrings = new HashMap<String, String>();

    protected SceneActor()
    {
        this.alpha = 255;
        this.makeupClassName = new ClassName(Makeup.class, NullMakeup.class.getName());
    }

    protected SceneActor( Actor actor )
    {
        Role actualRole = ((SceneDesignerRole) actor.getRole()).actualRole;

        this.id = actualRole.getId();
        this.x = (int) actor.getX();
        this.y = (int) actor.getY();
        this.alpha = actor.getAppearance().getAlpha();
        this.direction = actor.getAppearance().getDirection();
        this.heading = actor.getHeading();
        this.zOrder = actor.getZOrder();
        this.scale = actor.getAppearance().getScale();
        this.roleClassName = new ClassName(Role.class, actualRole.getClassName().name);
        this.makeupClassName = actor.getAppearance().getMakeupClassName();
        this.colorize = actor.getAppearance().getColorize() == null ? null : new RGBA(actor.getAppearance().getColorize());
        this.activationDelay = actor.getActivationDelay();
        this.startEvent = actor.getStartEvent();

        for (AbstractProperty<Role, ?> property : actualRole.getProperties()) {
            try {
                String value = property.getStringValue(actualRole);
                this.customPropertyStrings.put(property.key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Makeup makeup = actor.getAppearance().getMakeup();
        for (AbstractProperty<Makeup, ?> property : makeup.getProperties()) {
            try {
                String value = property.getStringValue(makeup);
                this.makeupPropertyStrings.put(property.key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateActor( Actor actor, Resources resources, boolean designMode )
    {
        
        actor.setStartEvent(this.startEvent);
        actor.moveTo(this.x, this.y);
        actor.setZOrder(this.zOrder);
        actor.getAppearance().setAlpha(this.alpha);
        actor.getAppearance().setDirection(this.direction);
        actor.setHeading(this.heading);
        actor.getAppearance().setScale(this.scale);
        actor.getAppearance().setColorize(this.colorize == null ? null : new RGBA(this.colorize));
        ClassName roleClassName = this.roleClassName;
        actor.setActivationDelay(this.activationDelay);
        actor.getAppearance().setMakeup(this.makeupClassName);

        if ((this.activationDelay == 0) && (!designMode)) {
            actor.event(this.startEvent);
        }

        if (roleClassName == null) {
            if (actor.getCostume() == null) {
                roleClassName = new ClassName(Role.class, PlainRole.class.getName());
            } else {
                roleClassName = actor.getCostume().roleClassName;
            }
        }

        Role actualRole;

        if (designMode) {

            SceneDesignerRole role = new SceneDesignerRole();
            actor.setRole(role);

            try {
                role.setRoleClassName(resources, roleClassName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            actualRole = role.actualRole;

        } else {

            try {
                actualRole = AbstractRole.createRole(resources, roleClassName);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        for (AbstractProperty<Role, ?> property : actualRole.getProperties()) {
            String value = this.customPropertyStrings.get(property.key);
            if (value != null) {
                try {
                    property.setValueByString(actualRole, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Makeup makeup = actor.getAppearance().getMakeup();

        for (AbstractProperty<Makeup, ?> property : makeup.getProperties()) {
            String value = this.makeupPropertyStrings.get(property.key);
            if (value != null) {
                try {
                    property.setValueByString(makeup, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        actualRole.setId( this.id );


        if (!designMode) {
            if (this.activationDelay > 0) {
                actualRole = new DelayedActivation(this.activationDelay, actualRole);
            }
            actor.setRole(actualRole);
        }

    }

    public abstract Actor createActor( Resources resources, boolean designActor );

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SceneActor result = (SceneActor) super.clone();

        result.customPropertyStrings = new HashMap<String, String>();
        for (String key : this.customPropertyStrings.keySet()) {
            result.customPropertyStrings.put(key, this.customPropertyStrings.get(key));
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
        if (!StringUtils.equals(this.roleClassName, other.roleClassName)) {
            return false;
        }
        if (!StringUtils.equals(this.colorize, other.colorize)) {
            return false;
        }

        return this.customPropertyStrings.equals(other.customPropertyStrings);
    }

}
