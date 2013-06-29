package uk.co.nickthecoder.itchy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import uk.co.nickthecoder.itchy.editor.SceneDesignerBehaviour;
import uk.co.nickthecoder.itchy.util.NullBehaviour;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.XMLException;
import uk.co.nickthecoder.itchy.util.XMLTag;
import uk.co.nickthecoder.jame.RGBA;

public class SceneReader
{
    private final Resources resources;

    private Scene scene;

    public SceneReader( Resources resources )
    {
        this.resources = resources;
    }

    public Scene load( String filename )
        throws Exception
    {
        System.out.println( "Loading scene : " + filename );
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( filename ) ) );
        this.scene = new Scene();
        try {
            XMLTag document = XMLTag.openDocument( reader );
            this.readScene( document.getTag( "scene" ) );

        } finally {
            reader.close();
        }
        return this.scene;
    }

    private void readScene( XMLTag resourcesTag )
        throws Exception
    {

        for ( Iterator<XMLTag> i = resourcesTag.getTags(); i.hasNext(); ) {
            XMLTag tag = i.next();
            if ( tag.getName() == "actor" ) {
                this.readActor( tag );
            } else if ( tag.getName() == "text" ) {
                this.readText( tag );
            }
        }
    }

    private void readActor( XMLTag actorTag )
        throws Exception
    {
        String costumeName = actorTag.getAttribute( "costume" );
        Costume costume = this.resources.getCostume( costumeName );
        if ( costume == null ) {
            throw new XMLException( "Costume not found : " + costumeName );
        }

        String startEvent = actorTag.getAttribute( "start" );

        CostumeSceneActor sceneActor = new CostumeSceneActor( costume, startEvent );
        sceneActor.behaviourClassName = costume.behaviourClassName;
        this.readSceneActorAttributes( actorTag, sceneActor );

        this.scene.sceneActors.add( sceneActor );
    }

    private void readText( XMLTag textTag )
        throws Exception
    {
        String fontName = textTag.getAttribute( "font" );
        int fontSize = textTag.getIntAttribute( "size" );
        String text = textTag.getAttribute( "text" );
        Font font = this.resources.getFont( fontName );

        if ( font == null ) {
            throw new XMLException( "Font not found : " + fontName );
        }

        TextSceneActor sceneActor = new TextSceneActor( font, fontSize, text );
        sceneActor.behaviourClassName = NullBehaviour.class.getName();

        String colorString = textTag.getOptionalAttribute( "color", "#ffffff" );
        try {
            sceneActor.color = RGBA.parse( colorString );
        } catch (Exception e) {
            throw new XMLException( "Illegal color : " + colorString );
        }


        this.readSceneActorAttributes( textTag, sceneActor );

        this.scene.sceneActors.add( sceneActor );
    }

    private void readSceneActorAttributes( XMLTag actorTag, SceneActor sceneActor )
        throws Exception
    {
        sceneActor.x = actorTag.getIntAttribute( "x" );
        sceneActor.y = actorTag.getIntAttribute( "y" );
        sceneActor.direction = actorTag.getDoubleAttribute( "direction" );
        sceneActor.scale = actorTag.getOptionalDoubleAttribute( "scale", 1 );

        if ( actorTag.hasAttribute( "colorize" ) ) {
            try {
                sceneActor.colorize = RGBA.parse( actorTag.getAttribute( "colorize" ) );
            } catch ( Exception e ) {
                throw new XMLException( "Illegal color : " + actorTag.getAttribute( "colorize" ) );
            }
        }

        if ( actorTag.hasAttribute( "behaviour" ) ) {
            sceneActor.behaviourClassName = actorTag.getAttribute( "behaviour" );
        }

        HashMap<String,String> properties = new HashMap<String,String>();
        for ( Iterator<XMLTag> i = actorTag.getTags( "property" ); i.hasNext(); ) {
            XMLTag tag = i.next();

            String name = tag.getAttribute( "name" );
            String value = tag.getAttribute( "value" );
            properties.put( name, value );
        }
        Actor actor = sceneActor.createActor( true );
        SceneDesignerBehaviour sda = (SceneDesignerBehaviour) actor.getBehaviour();

        for ( Property<Behaviour,?> property : sda.actualBehaviour.getProperties() ) {
            if ( properties.containsKey( property.access ) ) {
                sceneActor.customProperties.put(  property.access, property.parse( properties.get( property.access ) ) );
            }
        }
    }
}
