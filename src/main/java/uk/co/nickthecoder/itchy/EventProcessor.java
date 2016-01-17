package uk.co.nickthecoder.itchy;

public interface EventProcessor extends Runnable
{
    public void begin();
    
    public void end();
}
