/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Join three eases together into a single compound ease.
 * Imagine an Ease as a graph which goes from (0,0) to (1,1), a linear Ease would be a straight line,
 * and a EaseInOut Ease would be a slanted S shape.
 * A CompoundEase is a set of these graphs all joined together. Each section is an ease which goes
 * from 0 to 1, but the CompoundEase, can change the destination for all but the last Ease.
 * For example, if we joined two LinearEases together, which the first destination was 0.5, then it
 * would be identical to a single LinearEase. However, if we made the first destination 0.1, then the
 * first part of the animation would change slowly (as the gradient is shallow), and the second half would
 * be quick (going from 0.1 to 1 in the same time as the first half took to go from 0 to 0.1).  
 */
public class CompoundEase extends AbstractEase
{
    private List<Section> sections;
        
    private double totalWidth;
        
    public CompoundEase()
    {
        sections = new ArrayList<Section>();
        totalWidth = 0;
    }

    /**
     * Note, the last ease to be added should always have a destination of 1.
     * @param ease
     * @param width The amount of time that this ease is to be used relative to the other added eases
     * @param destination 
     */
    public CompoundEase addEase( Ease ease, double width, double destination )
    {        
        totalWidth += width;
        double prevY;
        if ( sections.isEmpty() ) {
            prevY = 0;
        } else {
            Section prevSection = sections.get(sections.size()-1);
            prevY = prevSection.y0 + prevSection.actualHeight;
        }
        sections.add( new Section( ease, width, prevY, destination ) );

        double accumulatedWidth = 0;
        for ( Section section : sections ) {
            
            section.actualWidth = section.width / totalWidth;
            section.x0 = accumulatedWidth;
            accumulatedWidth += section.actualWidth;
        }
        return this;
    }
    
    @Override
    public double amount( double amount )
    {
        for ( Section section : sections ) {
            if ( section.x0 + section.actualWidth >= amount ) {
                return section.amount( amount );
            }
        }
        return 1;
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append( "CompoundEase : \n" );
        for (Section section: sections) {
            buffer.append( section.ease )
                .append( " x0 : " ).append(section.x0)
                .append( " width : " ).append(section.actualWidth)
                .append( " y0 : " ).append(section.y0)
                .append( " height : " ).append(section.actualHeight)
                .append( "\n");
        }
        return buffer.toString();
    }
    
    private class Section
    {
        Ease ease;
        double width;
        
        double actualWidth;
        double actualHeight;
        
        double x0;
        double y0;

        public Section( Ease ease, double width, double y0, double y1 )
        {
            this.ease = ease;
            this.width = width;
            this.y0 = y0;
            this.actualHeight = y1 - y0;
            // x0 and actualWidth are calculated by parent class every time a section is added.
        }
        
        public double amount( double amount )
        {
            amount = (amount - this.x0) / this.actualWidth;
            
            double result = this.ease.amount( amount );
            
            return result * actualHeight + y0;
        }
    }
}
