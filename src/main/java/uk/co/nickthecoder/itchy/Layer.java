package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Rect;

/**
 * Layers are stacked together to form a {@link Layout}, which is the large scale structure of how your
 */
public class Layer implements Comparable<Layer>, NamedSubject<Layer>, Cloneable
{
    protected static final List<Property<Layer, ?>> properties = new ArrayList<Property<Layer, ?>>();

    static {
        properties.add(new StringProperty<Layer>("name").allowBlank(false));
        properties.add(new IntegerProperty<Layer>("zOrder"));
        properties.add(new IntegerProperty<Layer>("position.x"));
        properties.add(new IntegerProperty<Layer>("position.y").hint("0 is the top of the screen"));
        properties.add(new IntegerProperty<Layer>("position.width"));
        properties.add(new IntegerProperty<Layer>("position.height"));
        properties.add(new ClassNameProperty<Layer>(View.class, "viewClassName"));
        properties.add(new ClassNameProperty<Layer>(Stage.class, "stageClassName"));
        properties.add(new ClassNameProperty<Layer>(StageConstraint.class, "stageConstraintClassName"));
    }

    /**
     * The name of the Layer.
     * Used by {@link Layout#findLayer(String)}.
     */
    private String name;

    /**
     * The position of the Layer on the screen. These are screen coodinates, with (0,0) at the top left, and the
     * Y axis pointing downwards.
     */
    public Rect position;

    /**
     * High values obscure Layers with smaller zOrders.
     */
    public int zOrder;

    private ClassName viewClassName;

    private ClassName stageClassName;

    private ClassName stageConstraintClassName;

    private View view;

    /**
     * Create an unnamed Layer.
     * 
     * @param view
     */
    public Layer(View view)
    {
        this();
        this.view = view;
    }

    /**
     * Creates an unnamed Layer with a new StageView, and a new ZOrderStage.
     * This constructor is used by the {@link Editor}. If you want to create Layer's dynamically within your game,
     * use the other constructor : {@link #Layer(View)}.
     * 
     * @priority 5
     */
    public Layer()
    {
        name = "";
        Game game = Itchy.getGame();
        position = new Rect(0, 0, game.getWidth(), game.getHeight());
        viewClassName = new ClassName(View.class, StageView.class.getName());
        stageClassName = new ClassName(Stage.class, ZOrderStage.class.getName());
        stageConstraintClassName = new ClassName(StageConstraint.class, NullStageConstraint.class.getName());

        updateView();
        updateStage();
        updateStageConstraint();
    }

    /**
     * A simple getter, returns the layer's name.
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * A simple setter.
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
    public ClassName getViewClassName()
    {
        return this.viewClassName;
    }

    /**
     * Used internally by Itchy.
     * 
     * @param className
     * @priority 5
     */
    public void setViewClassName(ClassName className)
    {
        this.viewClassName = className;
        updateView();
    }

    private void updateView()
    {
        Resources resources = Itchy.getGame().resources;
        try {
            view = (View) viewClassName.createInstance(resources);
            view.setPosition(this.position);

            setStageClassName(this.stageClassName);
        } catch (Exception e) {
            Itchy.handleException(e);
        }
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
    public ClassName getStageClassName()
    {
        return this.stageClassName;
    }

    /**
     * Used internally by Itchy.
     * 
     * @param className
     * @priority 5
     */
    public void setStageClassName(ClassName className)
    {
        this.stageClassName = className;
        updateStage();
    }

    private void updateStage()
    {
        Resources resources = Itchy.getGame().resources;
        if (view instanceof StageView) {
            StageView stageView = (StageView) view;
            try {
                stageView.setStage((Stage) stageClassName.createInstance(resources));
                setStageConstraintClassName(stageConstraintClassName);
            } catch (Exception e) {
                Itchy.handleException(e);
            }
        }
    }

    /**
     * Used internally by Itchy.
     * 
     * @return
     * @priority 5
     */
    public ClassName getStageConstraintClassName()
    {
        return this.stageConstraintClassName;
    }

    /**
     * Used internally by Itchy.
     * 
     * @param className
     * @priority 5
     */
    public void setStageConstraintClassName(ClassName className)
    {
        this.stageConstraintClassName = className;
        updateStageConstraint();
    }

    private void updateStageConstraint()
    {
        Resources resources = Itchy.getGame().resources;
        if (view instanceof StageView) {
            StageView stageView = (StageView) view;
            try {
                StageConstraint sc = (StageConstraint) stageConstraintClassName.createInstance(resources);
                stageView.getStage().setStageConstraint(sc);
            } catch (Exception e) {
                Itchy.handleException(e);
            }
        }
    }

    /**
     * @return This layer's View (all Layers should have a view, and therefore null is not expected).
     */
    public View getView()
    {
        return view;
    }

    /**
     * 
     * @return This Layer's {@link View} if it is a {@link StageView}, or null otherwise.
     */
    public StageView getStageView()
    {
        if (view instanceof StageView) {
            return (StageView) view;
        }
        return null;
    }

    /**
     * @return The {@link View}'s stage if the View is a {@link StageView}, otherwise null.
     */
    public Stage getStage()
    {
        if (view instanceof StageView) {
            return ((StageView) view).getStage();
        }
        return null;
    }

    /**
     * Defines the natural order of Layers, using their Z-Orders.
     * @priority 3
     */
    @Override
    public int compareTo(Layer other)
    {
        return this.zOrder - other.zOrder;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public List<Property<Layer, ?>> getProperties()
    {
        return properties;
    }

    /**
     * Used internally by Itchy.
     * 
     * @priority 5
     */
    @Override
    public Layer clone()
    {
        Layer result;
        try {
            result = (Layer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        result.position = new Rect(this.position);

        result.setViewClassName(new ClassName(View.class, this.viewClassName.name));
        result.setStageClassName(new ClassName(Stage.class, this.stageClassName.name));
        result.setStageConstraintClassName(new ClassName(StageConstraint.class, this.stageConstraintClassName.name));

        try {
            Property.copyProperties(this.getView(), result.getView());
            if (this.getStage() != null) {
                Property.copyProperties(this.getStage(), result.getStage());
                Property.copyProperties(this.getStage().getStageConstraint(), result.getStage().getStageConstraint());
            }
        } catch (Exception e) {
            Itchy.handleException(e);
        }

        return result;
    }

}
