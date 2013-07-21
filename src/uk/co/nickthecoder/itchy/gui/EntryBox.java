/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class EntryBox<E extends EntryBox<?>> extends ClickableContainer implements Layout, KeyListener
{
    private static final RGBA ANY_COLOR = new RGBA(0, 0, 0);

    private final Label label;

    private final Container caret;

    private int boxWidthPixels;

    protected int boxWidth;

    private int caretIndex;

    private final List<ComponentChangeListener> changeListeners;

    public EntryBox( String str )
    {
        if (str == null) {
            str = "";
        }

        this.setType("textBox");
        this.label = new Label(str);
        this.addChild(this.label);
        this.label.setExpansion(1.0);

        this.caret = new Container();
        this.caret.setType("caret");
        this.addChild(this.caret);

        this.caretIndex = str.length();

        this.setFill(true, true);
        this.setLayout(this);

        this.boxWidth = 30;

        this.caret.setVisible(false);
        this.focusable = true;

        this.changeListeners = new ArrayList<ComponentChangeListener>();
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }

    public int getBoxWidth()
    {
        return this.boxWidth;
    }

    public void setBoxWidth( int value )
    {
        this.boxWidth = value;
    }

    public void onClick()
    {
    }

    @Override
    public void onClick( MouseButtonEvent ke )
    {
        this.focus();
        if (ke == null) {
            return;
        }
        try {
            TrueTypeFont ttf = this.label.getFont().getSize(this.label.getFontSize());

            int index;
            for (index = 0; index <= this.label.getText().length(); index++) {
                String text = this.label.getText().substring(0, index);
                Surface surface = ttf.renderBlended(text, ANY_COLOR);
                int width = surface.getWidth();
                surface.free();
                if (width > ke.x) {
                    this.setCaretPosition(index - 1);
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setCaretPosition(this.label.getText().length());

    }

    @Override
    public void onFocus( boolean focus )
    {
        this.caret.setVisible(focus);
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.HOME) {
            this.caretIndex = 0;
            this.update();
            return true;
        }

        if (ke.symbol == Keys.END) {
            this.caretIndex = this.label.getText().length();
            this.update();
            return true;
        }

        if (ke.symbol == Keys.LEFT) {
            if (this.caretIndex > 0) {
                this.caretIndex--;
                this.update();
            }
            return true;
        }

        if (ke.symbol == Keys.RIGHT) {
            if (this.caretIndex < this.label.getText().length()) {
                this.caretIndex++;
                this.update();
            }
            return true;
        }

        if (ke.symbol == Keys.BACKSPACE) {
            if (this.caretIndex > 0) {
                int pos = this.caretIndex; // setText will change the caretIndex
                                           // if at the end - don't want to do
                                           // it twice!
                this.caretIndex--;
                this.setEntryText(this.label.getText().substring(0, pos - 1) +
                        this.label.getText().substring(pos));
            }
            return true;
        }

        if (ke.symbol == Keys.DELETE) {
            if (this.caretIndex < this.label.getText().length()) {
                this.setEntryText(this.label.getText().substring(0, this.caretIndex) +
                        this.label.getText().substring(this.caretIndex + 1));
            }
            return true;
        }

        if ((ke.symbol == Keys.v) && (Itchy.singleton.isCtrlDown())) {

            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String str = (String) clipboard.getData(DataFlavor.stringFlavor);

                this.setEntryText(this.label.getText().substring(0, this.caretIndex) + str +
                        this.label.getText().substring(this.caretIndex));

                this.caretIndex += str.length();

            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if ((ke.c >= 32)) {
            if (this.setEntryText(this.label.getText().substring(0, this.caretIndex) + ke.c +
                    this.label.getText().substring(this.caretIndex))) {
                this.caretIndex++;
            }
            return true;
        }

        return super.onKeyDown(ke);
    }

    @Override
    public boolean onKeyUp( KeyboardEvent ke )
    {
        return false;
    }

    public void setCaretPosition( int index )
    {
        this.caretIndex = index;
        this.forceLayout();
        this.invalidate();
    }

    /**
     * 
     * @param text
     * @return true if the change was allowed (sub classes of TextBox, such as IntegerBox may
     *         prevent arbitrary text)
     */
    protected boolean setEntryText( String text )
    {
        this.label.setText(text);
        if (this.caretIndex > text.length()) {
            this.caretIndex = text.length();
        }
        this.update();
        return true;
    }

    private void update()
    {
        this.forceLayout();
        this.invalidate();

        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    public String getText()
    {
        return this.label.getText();
    }

    @Override
    public void calculateRequirements( Container c )
    {
        try {
            TrueTypeFont ttf = this.label.getFont().getSize(this.label.getFontSize());

            Surface surface = ttf.renderBlended("M", ANY_COLOR);
            this.boxWidthPixels = surface.getWidth() * this.boxWidth;
            surface.free();

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setNaturalWidth(this.boxWidthPixels + this.getPaddingLeft() + this.getPaddingRight());
        
        this.setNaturalHeight(
            this.label.getRequiredHeight() + this.label.getMarginTop() +
            this.label.getMarginBottom() + this.getPaddingTop() + this.getPaddingBottom());
    }

    @Override
    public void layout( Container c )
    {
        int width = this.boxWidthPixels;
        int height = this.label.getRequiredHeight();

        int caretX = 0;
        
        try {
            String text = this.label.getText().substring(0, this.caretIndex);
            Surface surface = this.label.getFont().getSize(this.label.getFontSize())
                    .renderBlended(text, ANY_COLOR);
            
            caretX = surface.getWidth();
            surface.free();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.label.setPosition(
            this.getPaddingLeft() + this.label.getMarginLeft(),
            this.getPaddingTop() + this.label.getMarginTop(),
            width,
            height);
        
        this.caret.setPosition(
            this.getPaddingLeft() + this.caret.getMarginLeft() + caretX,
            this.getPaddingTop() + this.caret.getPaddingTop(),
            this.caret.getRequiredWidth(),
            height - this.caret.getPaddingTop() - this.caret.getPaddingBottom());
    }

}
