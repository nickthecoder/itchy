package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;

public class PlainCostumeFeatures implements CostumeFeatures
{
    private final static List<Property<CostumeFeatures, ?>> EMPTY_PROPERTIES =
        new ArrayList<Property<CostumeFeatures, ?>>();

    @Override
    public List<Property<CostumeFeatures, ?>> getProperties()
    {
        return EMPTY_PROPERTIES;
    }

}
