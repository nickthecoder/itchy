package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class Layout implements PropertySubject<Layout>
{
    protected static final List<Property<Layout, ?>> properties = new ArrayList<Property<Layout,?>>();

    static {
        properties.add(new StringProperty<Layout>("name"));
    }
    
    public String name = "";

    public TreeSet<Layer> layers;

    /**
     * Speeds up findStage.
     */
    private Map<String, Stage> stageMap;

    public Layout()
    {
        this.layers = new TreeSet<Layer>();
        this.stageMap = new HashMap<String, Stage>();
    }

    public String getName()
    {
        return this.name;
    }
    
    public void addLayer( Layer layer )
    {
        this.layers.add( layer );
    }
    
    public void removeLayer( Layer layer )
    {
        this.layers.remove(layer);
    }
    
    public Layer findLayer(String name)
    {
        for (Layer layer : layers) {
            if (layer.name.equals(name)) {
                return layer;
            }
        }
        return null;
    }

    public View findView(String name)
    {
        Layer layer = findLayer(name);
        if (layer == null) {
            return null;
        }
        return layer.getView();
    }

    public Stage findStage(String name)
    {
        if (stageMap.containsKey(name)) {
            return stageMap.get(name);
        }

        View view = findView(name);
        if (view == null) {
            return null;
        }
        if (view instanceof StageView) {
            Stage result = ((StageView) view).getStage();
            stageMap.put(name, result);
            return result;
        }
        return null;
    }

    /**
     * Adds all of the other layout's layers to this layout.
     * 
     * Note, if two layers share the same name, then both layers will exist in the merged layout, as independent layers,
     * and findLayer will only return one of them (which one is not guaranteed). Therefore, it is highly recommended to
     * avoid name clashes.
     * 
     * @param other
     *            Not altered in any way
     */
    public void merge(Layout other)
    {
        for (Layer layer : other.layers) {
            this.layers.add(layer);
        }
    }

    @Override
    public List<Property<Layout, ?>> getProperties()
    {
        return properties;
    }

}
