/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.Animations;
import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.animation.Eases;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.util.ClassName;

public final class Registry
{
    private Registry parent;

    /**
     * The key is the base class, such as Role, or Director.
     * The values are the known implementations, such as "alien.py" or "uk.co.nickthecoder.itchy.PlainDirector"
     */
    private Map<Class<?>, Set<String>> classNames;

    private Map<String, Ease> eases;

    private Map<String, Animation> animationsByName;

    private Map<String, Animation> animationsByTagName;

    public Registry()
    {
        this(null);

        this.add(new ClassName(Director.class, PlainDirector.class.getName()));

        this.add(new ClassName(SceneDirector.class, PlainSceneDirector.class.getName()));

        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.PlainRole.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.LinkButton.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.MessageButton.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.NumberValue.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.TextValue.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.QuitButton.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.ProgressBar.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.SceneButton.class));
        this.add(new ClassName(Role.class, uk.co.nickthecoder.itchy.role.SliderRole.class));

        this.add(new ClassName(CostumeFeatures.class, PlainCostumeFeatures.class));

        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.NullMakeup.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Shadow.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Scale.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.Textured.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.PictureFrame.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.SimpleFrame.class));
        this.add(new ClassName(Makeup.class, uk.co.nickthecoder.itchy.makeup.ScaledBackground.class));

        this.add(new ClassName(View.class, uk.co.nickthecoder.itchy.RGBAView.class));
        this.add(new ClassName(View.class, uk.co.nickthecoder.itchy.StageView.class));
        this.add(new ClassName(View.class, uk.co.nickthecoder.itchy.WrappedStageView.class));

        this.add(new ClassName(Stage.class, uk.co.nickthecoder.itchy.ZOrderStage.class));

        this.add(new ClassName(StageConstraint.class, uk.co.nickthecoder.itchy.NullStageConstraint.class));
        this.add(new ClassName(StageConstraint.class, uk.co.nickthecoder.itchy.GridStageConstraint.class));

        Eases.registerEases(this);

        Animations.registerAnimations(this);
    }

    public Registry(Registry parent)
    {
        this.parent = parent;
        this.classNames = new HashMap<Class<?>, Set<String>>();
        this.eases = new HashMap<String, Ease>();
        this.animationsByName = new HashMap<String, Animation>();
        this.animationsByTagName = new HashMap<String, Animation>();
    }

    public boolean contains(ClassName className)
    {
        Class<?> baseClass = className.getClass();
        return this.containsClassName(baseClass, className.name);
    }

    private boolean containsClassName(Class<?> baseClass, String name)
    {
        Set<String> set = this.classNames.get(baseClass);
        if ((set != null) && set.contains(name)) {
            return true;
        } else {
            return this.parent == null ? false : this.parent.containsClassName(baseClass, name);
        }
    }

    public final void add(ClassName className)
    {
        if (this.contains(className)) {
            return;
        }
        Set<String> set = this.classNames.get(className.baseClass);
        if (set == null) {
            set = new TreeSet<String>();
            this.classNames.put(className.baseClass, set);
        }
        set.add(className.name);
    }

    public Set<String> getClassNames(Class<?> baseClass)
    {
        Set<String> set = this.classNames.get(baseClass);
        if (set == null) {
            if (this.parent == null) {
                return Collections.emptySet();
            } else {
                return this.parent.getClassNames(baseClass);
            }
        }

        if (this.parent == null) {
            return Collections.unmodifiableSet(set);
        }

        TreeSet<String> combined = new TreeSet<String>();
        combined.addAll(set);
        combined.addAll(this.parent.getClassNames(baseClass));
        return combined;
    }

    public Set<String> getRoleClassNames()
    {
        return getClassNames(Role.class);
    }

    public Set<String> getCostumePropertiesClassNames()
    {
        return getClassNames(CostumeFeatures.class);
    }

    public Set<String> getSceneDirectorClassNames()
    {
        return getClassNames(SceneDirector.class);
    }

    public Set<String> getDirectorClassNames()
    {
        return getClassNames(Director.class);
    }

    public void add(Animation animation)
    {
        this.animationsByName.put(animation.getName(), animation);
        this.animationsByTagName.put(animation.getTagName(), animation);
    }

    public Animation getAnimationByName(String name)
    {
        Animation result = this.animationsByName.get(name);
        if (result == null) {
            if (parent != null) {
                return parent.getAnimationByName(name);
            }
            return null;
        }
        return result.copy();
    }

    public Animation getAnimationByTagName(String name)
    {
        Animation result = this.animationsByTagName.get(name);
        if (result == null) {
            if (parent != null) {
                return parent.getAnimationByTagName(name);
            }
            return null;
        }
        return result.copy();
    }

    public Map<String, Animation> getAnimationsByName()
    {
        return Collections.unmodifiableMap(this.animationsByName);
    }

    public Map<String, Animation> getAnimationsByTagName()
    {
        return Collections.unmodifiableMap(this.animationsByTagName);
    }

    public Set<Animation> getAnimations()
    {
        TreeSet<Animation> result = new TreeSet<Animation>(new Comparator<Animation>()
        {
            @Override
            public int compare(Animation o1, Animation o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
        result.addAll(this.animationsByName.values());
        if (parent != null) {
            result.addAll(parent.getAnimations());
        }
        return result;
    }

    public void add(String name, Ease ease)
    {
        this.eases.put(name, ease);
    }

    public Ease getEase(String name)
    {
        Ease result = this.eases.get(name);
        if ((result == null) && (parent != null)) {
            return parent.getEase(name);
        }
        return result;
    }

    public String getEaseName(Ease ease)
    {
        for (String name : this.eases.keySet()) {
            if (this.eases.get(name) == ease) {
                return name;
            }
        }
        if (this.parent != null) {
            return parent.getEaseName(ease);
        }
        return null;
    }

    public Map<String, Ease> getEases()
    {
        Map<String, Ease> result = Collections.unmodifiableMap(this.eases);

        if (this.parent == null) {
            return result;
        }

        Map<String, Ease> combined = new HashMap<String, Ease>();
        combined.putAll(result);
        combined.putAll(parent.getEases());
        return combined;
    }

}
