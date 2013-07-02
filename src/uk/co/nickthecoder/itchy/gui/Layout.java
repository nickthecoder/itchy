package uk.co.nickthecoder.itchy.gui;

public interface Layout
{

    /**
     * Calculates the required width and height of the container based on the required widths and
     * heights of its children, plus any margins and spacing.
     */
    public void calculateRequirements( Container container );

    /**
     * Calculates the position and sizes of a Containers children. This can make use of the
     * containers actual width and height.
     */
    public void layout( Container container );

}
