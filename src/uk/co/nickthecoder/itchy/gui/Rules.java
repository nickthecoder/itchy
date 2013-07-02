package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Loadable;
import uk.co.nickthecoder.itchy.Resources;

public class Rules extends Loadable
{

    public Resources resources;

    private final List<Rule> rules;

    public Rules()
    {
        this.rules = new ArrayList<Rule>();
        this.resources = new Resources();
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

    @Override
    public void load( String filename ) throws Exception
    {
        super.load(filename);
        RulesReader loader = new RulesReader(this);
        loader.load(filename);
    }

    public void addRule( Rule rule )
    {
        this.rules.add(rule);
    }

    @Override
    protected void actualSave( String filename ) throws Exception
    {
    }

    @Override
    protected void checkSave( String filename ) throws Exception
    {
    }

}
