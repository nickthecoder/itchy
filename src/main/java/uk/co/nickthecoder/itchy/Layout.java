package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.itchy.property.LayerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

public class Layout implements NamedSubject<Layout>, Cloneable
{
    protected static final List<Property<Layout, ?>> properties = new ArrayList<Property<Layout, ?>>();

    static {
        properties.add(new StringProperty<Layout>("name"));
    }

    private List<Property<Layout, ?>> customProperties;

    @Override
    public List<Property<Layout, ?>> getProperties()
    {
        if (customProperties == null) {
            customProperties = new ArrayList<Property<Layout, ?>>();
            customProperties.addAll(properties);
            customProperties.add(new LayerProperty<Layout>("defaultLayer")
            {
                @Override
                public Layout getLayout()
                {
                    return Layout.this;
                }
            });
        }
        return customProperties;
    }

    public String name = "";

    private List<Layer> layers;

    /**
     * The active layer when starting the SceneDesigner
     */
    public Layer defaultLayer;
    
    /**
     * Speeds up findStage.
     */
    private Map<String, Stage> stageMap;

    public Layout()
    {
        layers = new ArrayList<Layer>();
        stageMap = new HashMap<String, Stage>();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    public List<Layer> getLayers()
    {
        return layers;
    }

    public List<Layer> getLayersByZOrder()
    {
        Collections.sort(layers);
        return layers;
    }

    public void addLayer(Layer layer)
    {
        layers.add(layer);
    }

    public void removeLayer(Layer layer)
    {
        layers.remove(layer);
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

    public StageView findStageView(String name)
    {
        View view = findView(name);
        if (view instanceof StageView) {
            return (StageView) view;
        }
        return null;
    }

    public Stage findStage(String name)
    {
        Stage result = stageMap.get(name);
        if (result != null) {
            return result;
        }

        StageView stageView = findStageView(name);
        if (stageView != null) {
            result = stageView.getStage();
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
        if (other == null) {
            return;
        }

        for (Layer layer : other.layers) {
            layers.add(layer);
        }
    }

    @Override
    public Layout clone()
    {
        Layout result;
        try {
            result = (Layout) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen, so this is just to stop the compiler whining.
            throw new RuntimeException(e);
        }

        result.stageMap = new HashMap<String, Stage>();
        result.layers = new ArrayList<Layer>();

        for (Layer layer : layers) {
            result.addLayer(layer.clone());
        }

        if (result.defaultLayer != null) {
            result.defaultLayer = result.findLayer(result.defaultLayer.name);
        }
        return result;
    }

    public void dump()
    {
        Resources.dump("Layout");
        for ( Layer layer : this.layers ) {
            Resources.dump( "   ", layer.name, layer.getView(), layer.getStage(), layer.getStage() == null ? "" : (layer.getStage().getActors().size() + " actors") );
        }
        Resources.dump("");
    }
}
