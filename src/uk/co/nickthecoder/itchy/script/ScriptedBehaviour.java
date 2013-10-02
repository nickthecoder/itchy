/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.util.AbstractProperty;

public class ScriptedBehaviour extends Behaviour
{
    private final static HashMap<String, List<AbstractProperty<Behaviour, ?>>> allProperties = new HashMap<String, List<AbstractProperty<Behaviour, ?>>>();
    
    private String filename;

    private ScriptLanguage language;

    public final Object scriptBehaviour;

    public final ScriptProperties propertyValues;
    
    public ScriptedBehaviour( String filename, ScriptLanguage language, Object scriptInstance )
    {
        this.filename = filename;
        this.language = language;
        this.scriptBehaviour = scriptInstance;
        this.propertyValues = new ScriptProperties(this.language, scriptInstance);
    }
    
    public List<AbstractProperty<Behaviour, ?>> getProperties()
    {
        String name = language.manager.getName( this.filename );

        List<AbstractProperty<Behaviour, ?>> result = allProperties.get(name);
        if (result == null) {
            result = new ArrayList<AbstractProperty<Behaviour, ?>>();
            allProperties.put(name, result);
        }
        return result;
    }

    public static void declareBehaviourProperty( 
        String behaviourName, String propertyName, String label, Object defaultValue, Class<?> klass )
    {
        System.out.println( "Declaring property for " + behaviourName + " propName : " + propertyName + " Lable : " + label + " defaultValue : " + defaultValue + " Class : " + klass );

        List<AbstractProperty<Behaviour, ?>> properties = allProperties.get(behaviourName);
        if (properties == null) {
            properties = new ArrayList<AbstractProperty<Behaviour, ?>>();
            allProperties.put(behaviourName, properties);
        }
        
        AbstractProperty<Behaviour,?> property = AbstractProperty.createProperty(
            klass, "propertyValues." + propertyName, propertyName, label, true, false, true);
        System.out.println( "Created property " + property );
        if ( property != null) {
            properties.add(property);
        }
        
    }
    
    @Override
    public String getClassName()
    {
        return this.filename;
    }

    private void handleException( Exception e )
    {
        e.printStackTrace();
    }

    @Override
    public void onAttach()
    {
        try {
            this.language.onAttach(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onDetach()
    {
        try {
            this.language.onDetach(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onActivate()
    {
        try {
            this.language.onActivate(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onDeactivate()
    {
        try {
            this.language.onDeactivate(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onMessage( String message )
    {
        try {
            this.language.onMessage(this, message);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void onKill()
    {
        try {
            this.language.onKill(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void tick()
    {
        try {
            this.language.tick(this);
        } catch (Exception e) {
            handleException(e);
        }
    }

}
