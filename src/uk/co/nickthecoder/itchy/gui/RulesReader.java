package uk.co.nickthecoder.itchy.gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Renderable;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.JameRuntimeException;
import uk.co.nickthecoder.jame.RGBA;

public class RulesReader
{
    private final Rules rules;

    public RulesReader( Rules rules )
    {
        this.rules = rules;
    }

    public void load( String filename )
        throws Exception
    {
        System.out.println( "Loading gui rules :" + filename );
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( filename ) ) );
        try {
            XMLTag document = XMLTag.openDocument( reader );
            this.readRules( document.getTag( "rules" ) );

            System.out.println( "Loaded gui rules : " + filename );
            reader.close();

        } finally {
            reader.close();
        }
    }

    private void readRules( XMLTag rulesTag )
        throws XMLException
    {
        for ( Iterator<XMLTag> i = rulesTag.getTags( "resources" ); i.hasNext(); ) {
            XMLTag resourcesTag = i.next();
            this.readResources( resourcesTag );
        }

        for ( Iterator<XMLTag> i = rulesTag.getTags( "rule" ); i.hasNext(); ) {
            XMLTag ruleTag = i.next();
            this.readRule( ruleTag );
        }
    }

    public void readResources( XMLTag resourcesTag )
        throws XMLException
    {
        String filename = resourcesTag.getAttribute( "filename" );
        try {
            this.rules.resources.load( this.rules.resolveFilename( filename ) );
        } catch ( Exception e ) {
            throw new XMLException( "Failed to read resource file : " + filename + "(" + e.getMessage() +")");
        }
    }

    public void readRule( XMLTag ruleTag )
        throws XMLException
    {
        String criteria = ruleTag.getAttribute( "criteria" );
        Rule rule = this.createRule( criteria );


        if ( ruleTag.hasAttribute( "font" ) ) {
            String fontName = ruleTag.getAttribute( "font" );
            rule.font = this.rules.resources.getFont( fontName );
        }

        if ( ruleTag.hasAttribute( "fontSize" ) ) {
            rule.fontSize = ruleTag.getIntAttribute( "fontSize" );
        }

        if ( ruleTag.hasAttribute( "margin" ) ) {
            int margin = ruleTag.getIntAttribute( "margin" );
            rule.marginTop = margin;
            rule.marginRight = margin;
            rule.marginBottom = margin;
            rule.marginLeft = margin;
        }

        if ( ruleTag.hasAttribute( "marginTop" ) ) {
            rule.marginTop = ruleTag.getIntAttribute( "marginTop" );
        }
        if ( ruleTag.hasAttribute( "marginRight" ) ) {
            rule.marginRight = ruleTag.getIntAttribute( "marginRight" );
        }
        if ( ruleTag.hasAttribute( "marginBottom" ) ) {
            rule.marginBottom = ruleTag.getIntAttribute( "marginBottom" );
        }
        if ( ruleTag.hasAttribute( "marginLeft" ) ) {
            rule.marginLeft = ruleTag.getIntAttribute( "marginLeft" );
        }


        if ( ruleTag.hasAttribute( "width" ) ) {
            rule.minimumWidth = ruleTag.getIntAttribute( "width" );
            rule.maximumWidth = ruleTag.getIntAttribute( "width" );
        }
        if ( ruleTag.hasAttribute( "height" ) ) {
            rule.minimumHeight = ruleTag.getIntAttribute( "height" );
            rule.maximumHeight = ruleTag.getIntAttribute( "height" );
        }

        if ( ruleTag.hasAttribute( "minimumWidth" ) ) {
            rule.minimumWidth = ruleTag.getIntAttribute( "minimumWidth" );
        }
        if ( ruleTag.hasAttribute( "minimumHeight" ) ) {
            rule.minimumHeight = ruleTag.getIntAttribute( "minimumHeight" );
        }
        if ( ruleTag.hasAttribute( "maximumWidth" ) ) {
            rule.maximumWidth = ruleTag.getIntAttribute( "maximumWidth" );
        }
        if ( ruleTag.hasAttribute( "maximumHeight" ) ) {
            rule.maximumHeight = ruleTag.getIntAttribute( "maximumHeight" );
        }



        if ( ruleTag.hasAttribute( "padding" ) ) {
            int padding = ruleTag.getIntAttribute( "padding" );
            rule.paddingTop = padding;
            rule.paddingRight = padding;
            rule.paddingBottom = padding;
            rule.paddingLeft = padding;
        }

        if ( ruleTag.hasAttribute( "paddingTop" ) ) {
            rule.paddingTop = ruleTag.getIntAttribute( "paddingTop" );
        }
        if ( ruleTag.hasAttribute( "paddingRight" ) ) {
            rule.paddingRight = ruleTag.getIntAttribute( "paddingRight" );
        }
        if ( ruleTag.hasAttribute( "paddingBottom" ) ) {
            rule.paddingBottom = ruleTag.getIntAttribute( "paddingBottom" );
        }
        if ( ruleTag.hasAttribute( "paddingLeft" ) ) {
            rule.paddingLeft = ruleTag.getIntAttribute( "paddingLeft" );
        }

        if ( ruleTag.hasAttribute( "spacing" ) ) {
            rule.spacing = ruleTag.getIntAttribute( "spacing" );
        }

        if ( ruleTag.hasAttribute( "color" ) ) {
            String colorString = ruleTag.getAttribute( "color" );
            try {
                RGBA color = RGBA.parse( colorString );
                rule.color = color;
            } catch ( JameException e ) {
                throw new XMLException( e.getMessage() );
            }
        }

        if ( ruleTag.hasAttribute( "background" ) ) {
            String backgroundName = ruleTag.getAttribute( "background" );

            Renderable background = null;
            if ( ( "".equals( backgroundName ) ) ) {

                background = Rule.NO_BACKGROUND;

            } else {

                background = this.rules.resources.getNinePatch( backgroundName );
                if ( background == null ) {
                    throw new XMLException( "Background not found : " + ruleTag.getAttribute( "background" ) );
                }
            }
            rule.background = background;
        }

        if ( ruleTag.hasAttribute( "image" ) ) {
            String imageName = ruleTag.getAttribute( "image" );

            Pose pose = this.rules.resources.getPose( imageName );
            if ( pose == null ) {
                throw new XMLException( "Pose not found : " + imageName );
            }
            try {
                rule.image = pose.getSurface();
            } catch ( JameRuntimeException e ) {
                throw new XMLException( "Failed to use pose " + imageName );
            }
        }

        this.rules.addRule( rule );
    }

    private Rule createRule( String stringCriteria )
    {
        stringCriteria = stringCriteria.trim();
        String[] parts = stringCriteria.split( "  *" );

        List<RuleCriteria> criteria = new ArrayList<RuleCriteria>();

        for ( String part : parts ) {
            if ( "?".equals( part ) ) {
                criteria.add( new RuleCriteria( false  ) );
            } else if ( "*".equals( part ) ) {
                criteria.add( new RuleCriteria( true  ) );
            } else {
                int dot = part.indexOf( "." );
                if ( dot < 0 ) {
                    criteria.add( new RuleCriteria( part, null ) );
                } else if ( dot == 0 ) {
                    criteria.add( new RuleCriteria( null, part.substring( 1 )) );
                } else {
                    criteria.add( new RuleCriteria( part.substring( 0, dot  ), part.substring( dot + 1  ) ) );
                }
            }
        }

        System.out.println( "Rule : " + criteria );
        return new Rule( criteria );
    }

}
