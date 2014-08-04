/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

/**
 * A common interface for TextArea and TextBox, the latter can handle multi-line text, and
 * is therefore a very different implementation. However, they share some common attributes,
 * and it can be handy to be unaware if the 
 *
 */
public interface TextWidget extends ComponentInterface
{
    public String getText();
    
    public void setText(String text);
    
    public void setBoxWidth( int width );
    
    public int getBoxWidth();
    
    public void addChangeListener( ComponentChangeListener listener );

    public void removeChangeListener( ComponentChangeListener listener );
    
}
