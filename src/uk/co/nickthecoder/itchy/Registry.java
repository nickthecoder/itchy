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

import uk.co.nickthecoder.itchy.animation.Ease;
import uk.co.nickthecoder.itchy.util.ClassName;

public class Registry
{
    private Registry parent;

    private Map<Class<?>, Set<String>> classNames;

    private Map<String, Ease> eases;

    public Registry()
    {
        this(null);
    }

    public Registry( Registry parent )
    {
        this.parent = parent;
        this.classNames = new HashMap<Class<?>, Set<String>>();
        this.eases = new HashMap<String, Ease>();
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
        if (baseClass == Role.class) {
            System.out.println("Asking for roles");
        }
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
        return getClassNames(CostumeProperties.class);
    }

    public Set<String> getSceneDirectorClassNames()
    {
        return getClassNames(SceneDirector.class);
    }

    public Set<String> getDirectorClassNames()
    {
        return getClassNames(Director.class);
    }

    public void registerAnimation()
    {
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
