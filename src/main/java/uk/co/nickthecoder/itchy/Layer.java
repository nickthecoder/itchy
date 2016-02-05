package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.ClassNameProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Rect;

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

    public String name;

    public Rect position;

    /**
     * High values are placed above those with smaller zOrders.
     */
    public int zOrder;

    private ClassName viewClassName;

    private ClassName stageClassName;

    private ClassName stageConstraintClassName;

    private View view;

    public Layer()
    {
        name = "";
        position = new Rect(0, 0, 1, 1);
        viewClassName = new ClassName(View.class, StageView.class.getName());
        stageClassName = new ClassName(View.class, ZOrderStage.class.getName());
        stageConstraintClassName = new ClassName(StageConstraint.class, NullStageConstraint.class.getName());
        
        updateView();
        updateStage();
        updateStageConstraint();
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
    
    public ClassName getViewClassName()
    {
        return this.viewClassName;
    }
    
    public void setViewClassName( ClassName className )
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

    public ClassName getStageClassName()
    {
        return this.stageClassName;
    }
    
    public void setStageClassName( ClassName className )
    {
        this.stageClassName = className;
        updateStage();
    }
    
    private void updateStage()
    {
        Resources resources = Itchy.getGame().resources;
        if ( view instanceof StageView) {
            StageView stageView = (StageView) view;
            try {
                stageView.setStage( (Stage) stageClassName.createInstance(resources));
                setStageConstraintClassName( stageConstraintClassName );
            } catch (Exception e) {
                Itchy.handleException(e);
            }
        }
    }
    
    public ClassName getStageConstraintClassName()
    {
        return this.stageConstraintClassName;
    }
    
    public void setStageConstraintClassName( ClassName className )
    {
        this.stageConstraintClassName = className;
        updateStageConstraint();
    }
    
    private void updateStageConstraint()
    {
        Resources resources = Itchy.getGame().resources;
        if ( view instanceof StageView) {
            StageView stageView = (StageView) view;
            try {
                StageConstraint sc = (StageConstraint) stageConstraintClassName.createInstance(resources);
                stageView.getStage().setStageConstraint(sc);
            } catch (Exception e) {
                Itchy.handleException(e);
            }
        }
    }
    

    public View getView()
    {
        return view;
    }
    
    public StageView getStageView()
    {
        if (view instanceof StageView) {
            return (StageView) view;
        }
        return null;
    }

    public Stage getStage()
    {
        if ( view instanceof StageView ) {
            return ((StageView) view).getStage();
        }
        return null;
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

        result.setViewClassName(new ClassName( View.class, this.viewClassName.name));
        result.setStageClassName(new ClassName( Stage.class, this.stageClassName.name));
        result.setStageConstraintClassName(new ClassName( StageConstraint.class, this.stageConstraintClassName.name));

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
