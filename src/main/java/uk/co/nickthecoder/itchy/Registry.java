/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NamedComparator;

public class Registry
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
    }

    public Registry( Registry parent )
    {
        this.parent = parent;
        this.classNames = new HashMap<Class<?>, Set<String>>();
        this.eases = new HashMap<String, Ease>();
        this.animationsByName = new HashMap<String, Animation>();
        this.animationsByTagName = new HashMap<String, Animation>();
    }

    public boolean contains( ClassName className )
    {
        Class<?> baseClass = className.getClass();
        return this.containsClassName(baseClass, className.name);
    }

    private boolean containsClassName( Class<?> baseClass, String name )
    {
        Set<String> set = this.classNames.get(baseClass);
        if ((set != null) && set.contains(name)) {
            return true;
        } else {
            return this.parent == null ? false : this.parent.containsClassName(baseClass, name);
        }
    }

    public void add( ClassName className )
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

    public Set<String> getClassNames( Class<?> baseClass )
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

    public void add( Animation animation )
    {
        this.animationsByName.put(animation.getName(), animation);
        this.animationsByTagName.put(animation.getTagName(), animation);
    }

    public Animation getAnimationByName( String name )
    {
        Animation result = this.animationsByName.get(name);
        if (result == null) {
            return null;
        }
        return result.copy();
    }

    public Animation getAnimationByTagName( String name )
    {
        Animation result = this.animationsByTagName.get(name);
        if (result == null) {
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
        TreeSet<Animation> result = new TreeSet<Animation>(new NamedComparator());
        result.addAll(this.animationsByName.values());
        return result;
    }

    public void add( String name, Ease ease )
    {
        this.eases.put(name, ease);
    }

    public Ease getEase( String name )
    {
        return this.eases.get(name);
    }

    public String getEaseName( Ease ease )
    {
        for (String name : this.eases.keySet()) {
            if (this.eases.get(name) == ease) {
                return name;
            }
        }
        return null;
    }

    public Map<String, Ease> getEases()
    {
        return Collections.unmodifiableMap(this.eases);
    }

}
