/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Loadable;
import uk.co.nickthecoder.itchy.Resources;

public class Stylesheet extends Loadable
{
    public Resources resources;

    private final List<StyleRule> rules;

    public Stylesheet( File file )
        throws Exception
    {
        super( file );
        this.rules = new ArrayList<StyleRule>();
        this.resources = new Resources();
        this.load();
    }

    public void style( Component component )
    {
        // TODO Remove
        if (component.getStyles().contains("TEST")) {
            System.out.println("Styling TEST component");
            StyleRule rule = this.rules.get(82);
            System.out.println("Checking match : " + rule );
            if (rule.matches(component)) {
                System.out.println("Yes it matches");
            }
        }
        
        StyleRule accumalator = new StyleRule(null);

        for (StyleRule rule : this.rules) {
            
            if (rule.matches(component)) {
                
                // TODO Remove
                if (component.getStyles().contains("TEST")) {
                    System.out.println( "Matching rule : " + rule );
                }

                accumalator.merge(rule);
            }
        }

        
        accumalator.apply(component);

        // TODO Remove
        if (component.getStyles().contains("TEST")) {
            System.out.println("\n");
        }
    }

    public void load() throws Exception
    {
        StyleSheetReader loader = new StyleSheetReader(this);
        loader.load(getFilename());
    }

    public void addRule( StyleRule rule )
    {
        this.rules.add(rule);
    }

    public void merge( Stylesheet other )
    {
        for ( StyleRule rule : other.rules ) {
            this.addRule( rule );
        }
    }
    
    @Override
    protected void actualSave( File file ) throws Exception
    {
    }

    @Override
    protected void checkSave( File file ) throws Exception
    {
    }

    public void debug()
    {
        System.out.println("\nStylesheet");
        int i = 0;
        for (StyleRule rule : this.rules) {
            System.out.println( "#" + i + " : " + rule );
            i ++;
        }
    }
}
