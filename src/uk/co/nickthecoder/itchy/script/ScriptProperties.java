/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

/**
 * Allows a script to hold properties, which can be edited with the scene designer.
 * 
 * The properties are held in the script's object, but appear as a Map
 */
public class ScriptProperties implements Map<String, Object>
{
    private static final String NOT_IMPLEMENTED = "Not implemented";

    private ScriptLanguage language;

    private final Object inst;

    public ScriptProperties( ShimmedScriptLanguage language, Object inst )
    {
        this.language = language;
        this.inst = inst;
    }

    @Override
    public Object get( Object name )
    {
        try {
            return this.language.getProperty(this.inst, name.toString());
        } catch (ScriptException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object put( String key, Object value )
    {
        Object oldValue = this.get(key);
        try {
            this.language.putProperty(this.inst, key, value);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return oldValue;
    }

    @Override
    public void clear()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean containsKey( Object arg0 )
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean containsValue( Object arg0 )
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean isEmpty()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public Object remove( Object arg0 )
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public int size()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public Set<String> keySet()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public void putAll( Map<? extends String, ? extends Object> m )
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

    @Override
    public Collection<Object> values()
    {
        throw new RuntimeException(NOT_IMPLEMENTED);
    }

}
