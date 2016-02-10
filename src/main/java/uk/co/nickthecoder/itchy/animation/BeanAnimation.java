package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.BeanHelper;

public class BeanAnimation extends NumericAnimation
{

    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add( new StringProperty<Animation>( "access" ).hint( "The role's property (must be a double)") );
        properties.add( new DoubleProperty<Animation>( "target" ) );
        properties.addAll( NumericAnimation.properties );
    }

    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    /**
     * The name of the property to get/set during the animation
     */
    public String access;
    
    /**
     * The final value of the property
     */
    public double target;

    /**
     * The value of the bean's property at the start of the animation
     */
    private double initialValue;
    
    private BeanHelper beanHelper;

    
    public BeanAnimation()
    {
        this(200,Eases.linear);
    }
    
    public BeanAnimation(int ticks, Ease ease)
    {
        super(ticks, ease);
    }


    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        this.beanHelper = new BeanHelper(actor.getRole(), this.access );
        try {
            this.initialValue = (double) beanHelper.get();
        } catch (Exception e) {
            Itchy.handleException(e);
        }
    }
    
    @Override
    public void tick(Actor actor, double amount, double delta)
    {
        double value = this.initialValue + (this.target - this.initialValue) * amount;
        try {
            beanHelper.set(value);
        } catch (Exception e) {
            Itchy.handleException(e);
        }
    }

    @Override
    public String getName()
    {
        return "Bean";
    }
}
