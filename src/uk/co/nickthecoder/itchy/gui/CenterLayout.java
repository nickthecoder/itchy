package uk.co.nickthecoder.itchy.gui;

import java.util.List;

public class CenterLayout implements Layout
{

    @Override
    public void calculateRequirements( Container container )
    {
        int width = 0;
        int height = 0;

        List<Component> children = container.getChildren();

        for (Component child : children) {
            if (width < child.getRequiredWidth()) {
                width = child.getRequiredWidth();
            }
            if (height < child.getRequiredHeight()) {
                height = child.getRequiredHeight();
            }
        }

        container.setNaturalWidth(width + container.getPaddingLeft() + container.getPaddingRight());
        container.setNaturalHeight(height + container.getPaddingTop() +
                container.getPaddingBottom());
    }

    @Override
    public void layout( Container container )
    {
        int midX = container.getWidth() / 2;
        int midY = container.getHeight() / 2;

        for (Component child : container.getChildren()) {

            int width = child.getRequiredWidth();
            int height = child.getRequiredHeight();

            child.setPosition(midX - width / 2, midY - height / 2, width, height);

        }
    }

}
