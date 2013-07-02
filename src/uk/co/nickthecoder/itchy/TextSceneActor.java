package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;

public class TextSceneActor extends SceneActor
{
    public Font font;

    public int fontSize;

    public String text;

    public RGBA color;

    protected TextSceneActor( Actor actor )
    {
        super(actor);
        TextPose pose = (TextPose) actor.getAppearance().getPose();
        this.font = pose.getFont();
        this.fontSize = (int) pose.getFontSize();
        this.text = pose.getText();
        this.color = new RGBA(pose.getColor());
    }

    public TextSceneActor( Font font, int fontSize, String text )
    {
        this.font = font;
        this.fontSize = fontSize;
        this.text = text;
        this.color = new RGBA(255, 255, 255);
    }

    @Override
    public Actor createActor( boolean designActor )
    {
        TextPose pose = new TextPose(this.text, this.font, this.fontSize);
        pose.setColor(this.color);
        Actor actor = new Actor(pose);
        this.updateActor(actor, designActor);
        return actor;
    }

}
