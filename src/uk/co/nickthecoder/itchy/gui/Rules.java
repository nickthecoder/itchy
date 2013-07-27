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

public class Rules extends Loadable
{
    public Resources resources;

    private final List<Rule> rules;

    public Rules( File file )
        throws Exception
    {
        super( file );
        this.rules = new ArrayList<Rule>();
        this.resources = new Resources();
        this.load();
    }

    public void style( Component component )
    {
        Rule accumalator = new Rule(null);

        for (Rule rule : this.rules) {
            if (rule.matches(component)) {
                accumalator.merge(rule);
            }
        }

        accumalator.apply(component);
    }

    public void load() throws Exception
    {
        RulesReader loader = new RulesReader(this);
        loader.load(getFilename());
    }

    public void addRule( Rule rule )
    {
        this.rules.add(rule);
    }

    public void merge( Rules other )
    {
        for ( Rule rule : other.rules ) {
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

}
