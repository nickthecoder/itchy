package uk.co.nickthecoder.itchy.gui;

public interface DragTarget extends Component
{
    public boolean accept( Object source );
    
    public void complete( Object source );
}
