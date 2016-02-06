package uk.co.nickthecoder.itchy.role;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeFeatures;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MouseListenerView;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.ViewMouseListener;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

/**
 * A numeric slider, which can be dragged with the mouse.
 * Values are doubles, with arbitrary minimum and maximum values.
 * 
 * This role's pose should be an image of the slider WITHOUT the slider button.
 * The role's costume must be given a "Companion", with an event name of "button".
 * 
 * The offset of the slider button pose should be its center.
 * The offset of the slider pose determines where the button goes when it is at the minimum value.
 * The role's costume has a property called "extent", which defines the length of the slider.
 * i.e. how far in pixels the slider button can move.
 * 
 * Sliders can point in any direction (horizontal, vertical or any diagonal) using the slider actor's direction
 * property.
 * 
 * See the "Customise" scene of the demo game "Rain" for example usage.
 */
public class SliderRole extends AbstractRole implements ViewMouseListener
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add( new DoubleProperty<Role>("minimum"));
        properties.add( new DoubleProperty<Role>("maximum"));
        properties.add( new DoubleProperty<Role>("value"));
        properties.add( new StringProperty<Role>("access"));
    }
    
    protected static final List<Property<CostumeFeatures, ?>> costumeProperties = new ArrayList<Property<CostumeFeatures, ?>>();

    static {
        costumeProperties.add( new IntegerProperty<CostumeFeatures>("extent"));
    }
    
    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }
    
    private SliderButton sliderButton;
    
    public double minimum = 0;

    public double maximum = 10;
    
    private double value = 0;
    
    public String access;
    
    private double extent;
    
    private BeanHelper beanHelper;
    
    @Override
    public void onBirth()
    {
        this.sliderButton = new SliderButton();
        getActor().createCompanion("button").setRole( this.sliderButton );
        this.sliderButton.getActor().setDirection(getActor().getDirection());
        this.extent = ((SliderFeatures) getCostumeFeatures()).extent;
        
        if ((this.access == null) || (this.access.isEmpty())) {
            this.beanHelper = null;
            // Update the button's position
            this.setValue(this.value);        
        } else {
            this.beanHelper = new BeanHelper(Itchy.getGame(), this.access);            
            try {
                this.setValue( (double) this.beanHelper.get() );
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
    }

    public double getValue()
    {
        return this.value;
    }
    
    public void setValue( double value )
    {
        this.value = constrainValue( value );
        if (this.beanHelper != null) {
            try {
                this.beanHelper.set(this.value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        double distance = (this.value - minimum) / (maximum - minimum) * this.extent;
        if ( this.sliderButton != null ) {
            this.sliderButton.getActor().moveTo(getActor());
            this.sliderButton.getActor().moveForwards(distance);
        }
    }
    
    public double constrainValue( double value )
    {
        if (value < this.minimum) {
            return this.minimum;
        }
        if (value > this.maximum) {
            return this.maximum;
        }
        return value;
    }
    
    @Override
    public void onMouseDown(MouseListenerView view, MouseButtonEvent event)
    {
        this.sliderButton.onMouseDown( view, event );
    }

    @Override
    public void onMouseUp(MouseListenerView view, MouseButtonEvent event)
    {
        this.sliderButton.onMouseUp( view, event );
    }

    @Override
    public void onMouseMove(MouseListenerView view, MouseMotionEvent event)
    {
        this.sliderButton.onMouseMove( view, event );
    }

    @Override
    public boolean isMouseListener()
    {
        return true;
    }

    
    @Override
    public CostumeFeatures createCostumeFeatures( Costume costume )
    {
        return new SliderFeatures( costume );
    }

    
    public class SliderFeatures extends CostumeFeatures
    {
        @Override
        public List<Property<CostumeFeatures, ?>> getProperties()
        {
            return costumeProperties;
        }
        
        // The amount of travel of the slider. We cannot deduce it from the pose, because it will have margins.
        public int extent = 100;
        
        public SliderFeatures(Costume costume)
        {
            super(costume);
        }
    }
    
    public class SliderButton extends AbstractRole
    {
        private boolean dragging = false;
        
        public void onMouseDown(MouseListenerView view, MouseButtonEvent event)
        {
            if (getActor().hitting(event.x, event.y)) {
                dragging = true;
                event.stopPropagation();
            }
        }

        public void onMouseUp(MouseListenerView view, MouseButtonEvent event)
        {
            dragging = false;
        }

        public void onMouseMove(MouseListenerView view, MouseMotionEvent event)
        {
            if ( dragging ) {
                
                double dx = event.x - SliderRole.this.getActor().getX();
                double dy = event.y - SliderRole.this.getActor().getY();
                
                double angle = Math.atan2(dy,  dx) - SliderRole.this.getActor().getDirection();
                
                double distance = Math.sqrt( dx * dx + dy * dy ) * Math.cos(angle);
                
                SliderRole.this.setValue( minimum + distance/extent * ( maximum - minimum ) );
                event.stopPropagation();
            }
        }

    }
}
