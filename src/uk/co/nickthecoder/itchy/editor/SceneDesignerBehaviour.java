package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Behaviour;

public class SceneDesignerBehaviour extends Behaviour
{
    public Behaviour actualBehaviour;

    public void setBehaviourClassName( String name )
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> klass = Class.forName( name );
        this.actualBehaviour = (Behaviour) klass.newInstance();
    }

    public String getBehaviourClassName()
    {
        return this.actualBehaviour.getClass().getName();
    }

    @Override
    public void tick()
    {
    }

}
