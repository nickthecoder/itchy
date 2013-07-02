package uk.co.nickthecoder.itchy;

public class CostumeSceneActor extends SceneActor
{
    public Costume costume;

    public CostumeSceneActor( Costume costume )
    {
        this(costume, "default");
    }

    public CostumeSceneActor( Costume costume, String startEvent )
    {
        this.costume = costume;
        this.startEvent = startEvent;
    }

    public CostumeSceneActor( Actor actor )
    {
        super(actor);
        this.costume = actor.getCostume();

    }

    @Override
    public Actor createActor( boolean designActor )
    {
        Actor actor = new Actor(this.costume, this.startEvent);
        this.updateActor(actor, designActor);
        return actor;
    }

}
