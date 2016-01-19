package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Rect;

public class Layer implements Comparable<Layer>, PropertySubject<Layer>
{
    protected static final List<Property<Layer, ?>> properties = new ArrayList<Property<Layer, ?>>();

    static {
        properties.add(new StringProperty<Layer>("name"));
        properties.add(new IntegerProperty<Layer>("zOrder"));
        properties.add(new IntegerProperty<Layer>("position.x"));
        properties.add(new IntegerProperty<Layer>("position.y").hint("0 is the top of the screen"));
        properties.add(new IntegerProperty<Layer>("position.width"));
        properties.add(new IntegerProperty<Layer>("position.height"));
        properties.add(new ClassNameProperty<Layer>(View.class, "viewClassName"));
        properties.add(new ClassNameProperty<Layer>(Stage.class, "stageClassName"));
    }

    public String name;

    public Rect position;

    /**
     * High values are placed above those with smaller zOrders.
     */
    public int zOrder;

    public ClassName viewClassName;

    public ClassName stageClassName;

    private String cachedViewClassname;

    private View view;

    public Layer()
    {
        name = "";
        position = new Rect(0, 0, 1, 1);
        viewClassName = new ClassName(View.class, StageView.class.getName());
        stageClassName = new ClassName(View.class, ZOrderStage.class.getName());
    }

    public View getView()
    {
        if ((view == null) || (!cachedViewClassname.equals(viewClassName.name))) {
            try {
                Resources resources = Itchy.getGame().resources;
                view = (View) viewClassName.createInstance(resources);
                if ( view instanceof StageView) {
                    StageView stageView = (StageView) view;
                    stageView.setStage( (Stage) stageClassName.createInstance(resources));
                }
            } catch (Exception e) {
                Itchy.handleException(e);
                return null;
            }
            cachedViewClassname = viewClassName.name;
        }
        return view;
    }

    @Override
    public int compareTo(Layer other)
    {
        return this.zOrder - other.zOrder;
    }

    @Override
    public List<Property<Layer, ?>> getProperties()
    {
        return properties;
    }

}
