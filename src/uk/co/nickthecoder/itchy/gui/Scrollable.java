package uk.co.nickthecoder.itchy.gui;

public interface Scrollable
{
    /**
     * Scrolls the viewport if needed to ensure that the component is visible
     * 
     * @param component
     *        A descendant of this VerticalScroll
     */
    public void ensureVisible( Component component );

}
