/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import uk.co.nickthecoder.itchy.property.AbstractProperty;

/**
 * Works in the same way as {@link java.util.prefs.Preferences}, but there is no need to {@link #flush()} as this is done for every write.
 * This makes it less efficient, but easier to use. If you want to write large amounts of data, consider using
 * {@link java.util.prefs.Preferences} directly.
 */
public class AutoFlushPreferences extends Preferences
{
    private Preferences wrapped;

    public AutoFlushPreferences( Preferences toWrap )
    {
        this.wrapped = toWrap;
    }

    @Override
    public String absolutePath()
    {
        return this.wrapped.absolutePath();
    }

    @Override
    public void addNodeChangeListener( NodeChangeListener ncl )
    {
        this.wrapped.addNodeChangeListener(ncl);
    }

    @Override
    public void addPreferenceChangeListener( PreferenceChangeListener pcl )
    {
        this.wrapped.addPreferenceChangeListener(pcl);
    }

    @Override
    public String[] childrenNames()
    {
        try {
            return this.wrapped.childrenNames();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear()
    {
        try {
            this.wrapped.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportNode( OutputStream os ) throws IOException
    {
        try {
            this.wrapped.exportNode(os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportSubtree( OutputStream os ) throws IOException
    {
        try {
            this.wrapped.exportSubtree(os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush()
    {
        try {
            this.wrapped.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get( String key, String def )
    {
        return this.wrapped.get(key, def);
    }

    @Override
    public boolean getBoolean( String key, boolean def )
    {
        return this.wrapped.getBoolean(key, def);
    }

    @Override
    public byte[] getByteArray( String key, byte[] def )
    {
        return this.wrapped.getByteArray(key, def);
    }

    @Override
    public double getDouble( String key, double def )
    {
        return this.wrapped.getDouble(key, def);
    }

    @Override
    public float getFloat( String key, float def )
    {
        return this.wrapped.getFloat(key, def);
    }

    @Override
    public int getInt( String key, int def )
    {
        return this.wrapped.getInt(key, def);
    }

    @Override
    public long getLong( String key, long def )
    {
        return this.wrapped.getLong(key, def);
    }

    @Override
    public boolean isUserNode()
    {
        return this.wrapped.isUserNode();
    }

    @Override
    public String[] keys()
    {
        try {
            return this.wrapped.keys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name()
    {
        return this.wrapped.name();
    }

    @Override
    public AutoFlushPreferences node( String pathName )
    {
        return new AutoFlushPreferences(this.wrapped.node(pathName));
    }

    @Override
    public boolean nodeExists( String pathName )
    {
        try {
            return this.wrapped.nodeExists(pathName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AutoFlushPreferences parent()
    {
        return new AutoFlushPreferences(this.wrapped.parent());
    }

    @Override
    public void put( String key, String value )
    {
        this.wrapped.put(key, value);
        this.flush();
    }

    @Override
    public void putBoolean( String key, boolean value )
    {
        this.wrapped.putBoolean(key, value);
        this.flush();
    }

    @Override
    public void putByteArray( String key, byte[] value )
    {
        this.wrapped.putByteArray(key, value);
        this.flush();
    }

    @Override
    public void putDouble( String key, double value )
    {
        this.wrapped.putDouble(key, value);
        this.flush();
    }

    @Override
    public void putFloat( String key, float value )
    {
        this.wrapped.putFloat(key, value);
        this.flush();
    }

    @Override
    public void putInt( String key, int value )
    {
        this.wrapped.putInt(key, value);
        this.flush();
    }

    @Override
    public void putLong( String key, long value )
    {
        this.wrapped.putLong(key, value);
        this.flush();
    }

    @Override
    public void remove( String key )
    {
        this.wrapped.remove(key);
        this.flush();
    }

    @Override
    public void removeNode() throws BackingStoreException
    {
        this.wrapped.removeNode();
        this.flush();
    }

    @Override
    public void removeNodeChangeListener( NodeChangeListener ncl )
    {
        this.wrapped.removeNodeChangeListener(ncl);
    }

    @Override
    public void removePreferenceChangeListener( PreferenceChangeListener pcl )
    {
        this.wrapped.removePreferenceChangeListener(pcl);
    }

    @Override
    public void sync()
    {
        try {
            this.wrapped.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public <S> void load( S subject, List<AbstractProperty<S, ?>> properties )
    {
        for (AbstractProperty<S, ?> property : properties) {
            try {
                String value = this.get(property.key, null);
                if (value != null) {
                    property.setValue(subject, value);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public <S> void save( S subject, List<AbstractProperty<S, ?>> properties )
    {
        for (AbstractProperty<S, ?> property : properties) {
            try {
                String value = property.getStringValue(subject);
                this.put(property.key, value);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    @Override
    public String toString()
    {
        return this.wrapped.toString();
    }

}
