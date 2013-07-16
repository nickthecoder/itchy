/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

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
        return wrapped.absolutePath();
    }

    @Override
    public void addNodeChangeListener( NodeChangeListener ncl )
    {
        wrapped.addNodeChangeListener(ncl);
    }

    @Override
    public void addPreferenceChangeListener( PreferenceChangeListener pcl )
    {
        wrapped.addPreferenceChangeListener(pcl);
    }

    @Override
    public String[] childrenNames()
    {
        try {
            return wrapped.childrenNames();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear()
    {
        try {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportNode( OutputStream os ) throws IOException
    {
        try {
            wrapped.exportNode( os );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportSubtree( OutputStream os ) throws IOException
    {
        try {
            wrapped.exportSubtree(os);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush()
    {
        try {
            wrapped.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get( String key, String def )
    {
        return wrapped.get(key, def);
    }

    @Override
    public boolean getBoolean( String key, boolean def )
    {
        return wrapped.getBoolean(key, def);
    }

    @Override
    public byte[] getByteArray( String key, byte[] def )
    {
        return wrapped.getByteArray(key, def);
    }

    @Override
    public double getDouble( String key, double def )
    {
        return wrapped.getDouble(key, def);
    }

    @Override
    public float getFloat( String key, float def )
    {
        return wrapped.getFloat(key, def);
    }

    @Override
    public int getInt( String key, int def )
    {
        return wrapped.getInt(key, def);
    }

    @Override
    public long getLong( String key, long def )
    {
        return wrapped.getLong(key, def);
    }

    @Override
    public boolean isUserNode()
    {
        return wrapped.isUserNode();
    }

    @Override
    public String[] keys()
    {
        try {
            return wrapped.keys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name()
    {
        return wrapped.name();
    }

    @Override
    public AutoFlushPreferences node( String pathName )
    {
        return new AutoFlushPreferences( wrapped.node(pathName) );
    }

    @Override
    public boolean nodeExists( String pathName )
    {
        try {
            return wrapped.nodeExists(pathName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AutoFlushPreferences parent()
    {
        return new AutoFlushPreferences( wrapped.parent() );
    }

    @Override
    public void put( String key, String value )
    {
        wrapped.put(key, value);
        this.flush();
    }

    @Override
    public void putBoolean( String key, boolean value )
    {
        wrapped.putBoolean(key, value);
        this.flush();
    }

    @Override
    public void putByteArray( String key, byte[] value )
    {
        wrapped.putByteArray(key, value);
        this.flush();
    }

    @Override
    public void putDouble( String key, double value )
    {
        wrapped.putDouble(key, value);
        this.flush();
    }

    @Override
    public void putFloat( String key, float value )
    {
        wrapped.putFloat(key, value);
        this.flush();
    }

    @Override
    public void putInt( String key, int value )
    {
        wrapped.putInt(key, value);
        this.flush();
    }

    @Override
    public void putLong( String key, long value )
    {
        wrapped.putLong(key, value);
        this.flush();
    }

    @Override
    public void remove( String key )
    {
        wrapped.remove(key);
        this.flush();
    }

    @Override
    public void removeNode() throws BackingStoreException
    {
        wrapped.removeNode();
        this.flush();
    }

    @Override
    public void removeNodeChangeListener( NodeChangeListener ncl )
    {
        wrapped.removeNodeChangeListener(ncl);
    }

    @Override
    public void removePreferenceChangeListener( PreferenceChangeListener pcl )
    {
        wrapped.removePreferenceChangeListener(pcl);
    }

    @Override
    public void sync()
    {
        try {
            wrapped.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String toString()
    {
        return wrapped.toString();
    }

}
