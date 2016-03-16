package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.editor.SceneDesigner;
import uk.co.nickthecoder.itchy.property.LayerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;

/**
 * Layouts are the large scale organisation of what your game looks like on the screen. Layouts are made up of a stack
 * of {@link Layer}s, where each Layer has a {@link View}. The most common type of View is a {@link StageView}, and this
 * is where all of the {@link Actor}s are drawn. Other types of View are possible, such as {@link RGBAView}, which
 * draws a solid colour (often used as a background).
 * <p>
 * Each Layer has a Z-Order, which determines the order that the Layers are drawn, and therefore which appear on top.
 * High value Z-Orders are drawn last, and therefore obscure Layers with lower Z-Orders.
 * <p>
 * You create Layouts and their Layers within the {@link Editor}, so there is little of your game code related to
 * Layouts. However, if you write a game with an option of a split screen (for two players), then you will need to
 * manipulate the Layers within your code, rather then from the Editor.
 * <p>
 * The most commonly used methods are {@link #findStageView(String)} and {@link #findStage(String)}.
 * <p>
 * A typical game may have three Layers, a background Layer, a mid layer (where all of the main Actors live), and a top
 * layer (often called a "glass" layer) for messages, scores and anything else that should not be obscured. In a simple
 * game all of the layers will cover the whole of the screen. However, some games will split the screen into different
 * sections. For example, the demo game "The-Mings" has a main View covering most of the screen, and a small View at the
 * bottom of the screen, containing many buttons.
 * <p>
 * {@link Game} has a Layout, which is created each time a {@link Scene} is loaded.
 */
public class Layout implements NamedSubject<Layout>, Cloneable
{
    protected static final List<Property<Layout, ?>> properties = new ArrayList<Property<Layout, ?>>();

    static {
        properties.add(new StringProperty<Layout>("name"));
    }

    private List<Property<Layout, ?>> customProperties;

    HashMap<String, String> renamedLayers;

    /**
     * Used internally by Itchy
     * 
     * @priority 5
     */
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

    /**
     * The name of this Layout.
     * 
     * @priority 5
     */
    public String name = "";

    private List<Layer> layers;

    /**
     * The current editing layer when starting the {@link SceneDesigner}.
     * 
     * @priority 5
     */
    public Layer defaultLayer;

    /**
     * Speeds up findStage.
     */
    private Map<String, Stage> stageMap;

    /**
     * Create an empty Layout without any Layers.
     */
    public Layout()
    {
        layers = new ArrayList<Layer>();
        stageMap = new HashMap<String, Stage>();
    }

    /**
     * The Layout names are used only within the {@link Editor} and {@link SceneDesigner}.
     * 
     * @return The name of the Layout.
     * @priority 5
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * A simple setter.
     * 
     * @priority 5
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns a list of all the Layers, in no particular order.
     * See {@link #getLayersByZOrder()} for an ordered list.
     * 
     * @return A list of all Layers. If there are no Layers, and empty list is returned.
     */
    public List<Layer> getLayers()
    {
        return layers;
    }

    /**
     * 
     * @return A list of all Layers, ordered by their Z-Order, highest first.
     */
    public List<Layer> getLayersByZOrder()
    {
        Collections.sort(layers);
        return layers;
    }

    /**
     * Adds a Layer.
     * 
     * @param layer
     */
    public void addLayer(Layer layer)
    {
        layers.add(layer);
        if (this.defaultLayer == null) {
            this.defaultLayer = layer;
        }
    }

    /**
     * Removes a Layer. If the Layer is not within the Layout, then nothing happens, and no exception is thrown.
     * 
     * @param layer
     */
    public void removeLayer(Layer layer)
    {
        layers.remove(layer);
    }

    /**
     * Finds a Layer by the Layer's name.
     * Note. it is possible for two Layers to share the same name, in which case, it is undefined which Layer will
     * be returned. Therefore it is highly recommended to choose unique names for your Layers.
     * 
     * @param name
     *            The name of the Layer to look for.
     * @return The Layer with the given name, or null if none matches.
     */
    public Layer findLayer(String name)
    {
        for (Layer layer : layers) {
            if (layer.name.equals(name)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Used internally by Itchy.
     * Finds a Layer by name, but if none is found, then try to return a suitable alternative;
     * the {@link #defaultLayer}, but if there is no default Layer, then look for any Layer with a {@link Stage}.
     * If no Stages are found, then null is returned.
     * 
     * @param name
     *            The name of the Layer to look for
     * @return The named Layer, or a suitable alternative, or null.
     * @priority 5
     */
    public Layer findSafeLayer(String name)
    {
        Layer layer = findLayer(name);
        if (layer != null) {
            return layer;
        }

        if (this.defaultLayer != null) {
            return this.defaultLayer;
        }

        for (Layer layer2 : this.layers) {
            if (layer2.getStage() != null) {
                return layer2;
            }
        }
        return null;
    }

    /**
     * Looks for a {@link Layer} with a particular name, and returns that Layer's {@link View}.
     * 
     * @param name
     *            The name of the Layer to look for
     * @return The named Layer's View, or null if the named Layer was not found.
     */
    public View findView(String name)
    {
        Layer layer = findLayer(name);
        if (layer == null) {
            return null;
        }
        return layer.getView();
    }

    /**
     * Looks for a {@link Layer} with a particular name, and returns that Layer's View if it is a {@link StageView}
     * 
     * @param name
     * @return The named Layer's View, or null if the named Layer was not found, or the View was not a StageView.
     */
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
     * Tries hard to find a stage with the given name. If the named layer isn't found, or isn't a stage,
     * then try the default layer, and if that fails, then return the first layer with a stage.
     * If all that fails, then null will be returned.
     */
    public Stage findSafeStage(String name)
    {
        Stage stage = findStage(name);
        if (stage != null) {
            return stage;
        }

        stage = defaultLayer.getStage();
        if (stage != null) {
            return stage;
        }

        for (Layer layer : layers) {
            stage = layer.getStage();
            if (stage != null) {
                return stage;
            }
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

    /**
     * Used while loading - if a layer has been renamed since the scene was last saved, then we need to
     * translate from the old name to the new name.
     */
    public String getNewLayerName(String name)
    {
        if (renamedLayers == null) {
            return name;
        }

        String origName = renamedLayers.get(name);
        return origName == null ? name : origName;
    }

    /**
     * Used internally by Itchy.
     * 
     * @param layer
     * @param oldName
     * @priority 5
     */
    public void renameLayer(Layer layer, String oldName)
    {
        String newName = layer.getName();
        if (renamedLayers == null) {
            renamedLayers = new HashMap<String, String>();
        }

        if ((oldName == null) || oldName.equals(newName)) {
            return;
        }

        // If the layer has been renamed already, we want to map from the ORIGINAL name, not the intermediate name
        // (oldName).
        for (String name : renamedLayers.keySet()) {
            if (renamedLayers.get(name).equals(oldName)) {
                oldName = name;
                break;
            }
        }
        if (oldName.equals(newName)) {
            renamedLayers.remove(oldName);
        } else {
            renamedLayers.put(oldName, newName);
        }
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
    public boolean renamePending()
    {
        return renamedLayers == null ? false : renamedLayers.size() > 0;
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
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

    /**
     * Used for debugging only.
     * 
     * @return
     * @priority 5
     */
    public void dump()
    {
        Resources.dump("Layout");
        for (Layer layer : this.layers) {
            Resources.dump("   ", layer.name, layer.getView(), layer.getStage(), layer.getStage() == null ? "" : (layer
                .getStage().getActors().size() + " actors"));
        }
        Resources.dump("");
    }
}
