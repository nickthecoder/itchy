/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
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
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.TrueTypeFont;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class EntryBox<E extends EntryBox<?>> extends ClickableContainer implements ContainerLayout, KeyListener
{
    private static final RGBA ANY_COLOR = new RGBA(0, 0, 0);

    private final Label label;

    private final PlainContainer caret;

    private int boxWidthPixels;

    protected int boxWidth;

    private int caretIndex;

    private boolean readOnly = false;


    /**
     * The amount the text is scrolled left/right to ensure that the caret is visible.
     */
    private int scroll = 0;

    private final List<ComponentChangeListener> changeListeners;

    private final List<ComponentValidator> validators;

    public EntryBox(String str)
    {
        if (str == null) {
            str = "";
        }

        this.setType("textBox");
        this.label = new Label(str);
        this.addChild(this.label);
        // this.label.setExpansion(1.0);

        this.caret = new PlainContainer();
        this.caret.setType("caret");
        this.addChild(this.caret);

        this.caretIndex = str.length();

        this.setFill(true, true);
        this.setLayout(this);

        this.boxWidth = 30;

        this.caret.setVisible(false);
        focusable = true;

        this.changeListeners = new ArrayList<ComponentChangeListener>();
        this.validators = new ArrayList<ComponentValidator>();
    }

    public void addChangeListener(ComponentChangeListener listener)
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener(ComponentChangeListener listener)
    {
        this.changeListeners.remove(listener);
    }

    public void addValidator( ComponentValidator validator )
    {
        this.validators.add(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }

    public int getBoxWidth()
    {
        return this.boxWidth;
    }

    public void setBoxWidth(int value)
    {
        this.boxWidth = value;
    }

    public void setReadOnly(boolean value)
    {
        this.readOnly = value;
        this.addStyle("readOnly", this.readOnly);
        this.reStyle();
    }

    public boolean getReadOnly()
    {
        return this.readOnly;
    }

    @Override
    public void onClick(MouseButtonEvent ke)
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
                if (width > ke.x - this.scroll) {
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
    public void onFocus(boolean focus)
    {
        this.caret.setVisible(focus);
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
        if (ke.symbol == Keys.HOME) {
            this.caretIndex = 0;
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.END) {
            this.caretIndex = this.label.getText().length();
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.LEFT) {
            if (this.caretIndex > 0) {
                this.caretIndex--;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.RIGHT) {
            if (this.caretIndex < this.label.getText().length()) {
                this.caretIndex++;
                this.update();
            }
            ke.stopPropagation();
        }

        if (!readOnly) {

            if (ke.symbol == Keys.BACKSPACE) {
                if (this.caretIndex > 0) {
                    // setText will change the caretIndex if at the end - don't want to do it twice!
                    int pos = this.caretIndex;
                    this.caretIndex--;
                    this.setEntryText(this.label.getText().substring(0, pos - 1) +
                        this.label.getText().substring(pos));
                }
                ke.stopPropagation();
            }

            if (ke.symbol == Keys.DELETE) {
                if (this.caretIndex < this.label.getText().length()) {
                    this.setEntryText(this.label.getText().substring(0, this.caretIndex) +
                        this.label.getText().substring(this.caretIndex + 1));
                }
                ke.stopPropagation();
            }

            if ((ke.symbol == Keys.v) && (Itchy.isCtrlDown())) {

                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    String str = (String) clipboard.getData(DataFlavor.stringFlavor);

                    insert(str);

                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ke.stopPropagation();
            }

            if ((ke.c >= 32)) {
                insert(ke.c);
                ke.stopPropagation();
            }

        }

        super.onKeyDown(ke);
    }

    private boolean insert(char text)
    {
        return insert(Character.toString(text));
    }

    private boolean insert(String text)
    {
        String old = this.label.getText();
        String newString;
        if (this.caretIndex >= old.length()) {
            newString = old + text;
        } else {
            newString = old.substring(0, this.caretIndex) + text + old.substring(this.caretIndex);
        }
        boolean result = this.setEntryText(newString);
        if (result) {
            this.caretIndex += text.length();
        }
        return result;

    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }

    public void setCaretPosition(int index)
    {
        this.caretIndex = index;
        this.forceLayout();
        this.invalidate();
    }

    /**
     *
     * @param text
     * @return true if the change was allowed (sub classes of TextBox, such as IntegerBox may prevent arbitrary text)
     */
    protected boolean setEntryText(String text)
    {
        if (text == null) {
            text = "";
        }
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

        fireChangeEvent();
    }

    public void fireChangeEvent()
    {
        this.removeStyle("error");
        for (ComponentValidator validator : this.validators) {
            if ( ! validator.isValid() ) {
                this.addStyle("error");
            }
        }
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    public String getText()
    {
        return this.label.getText();
    }

    @Override
    public void calculateRequirements(PlainContainer c)
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
    public void layout(PlainContainer c)
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

        int caretWidth = this.caret.getRequiredWidth();
        int caretTotalX = this.scroll + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;

        // We will jump the scroll by half the width of the textbox.
        while (caretTotalX + caretWidth > width) {
            this.scroll -= width / 2;
            caretTotalX = this.scroll + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
        }

        while (caretTotalX < 0) {
            this.scroll += width / 2;
            if (this.scroll > 0) {
                this.scroll = 0;
            }
            caretTotalX = this.scroll + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
        }

        if (this.scroll < 0) {
            // Now lets check that we aren't too far
            int right = this.scroll + this.label.getMarginLeft() + this.label.getNaturalWidth()
                + this.label.getMarginRight();
            if (right < width) {
                this.scroll += width - right;
                caretTotalX = this.scroll + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
            }
        }

        this.caret.setPosition(
            caretTotalX,
            this.getPaddingTop() + this.caret.getPaddingTop(),
            caretWidth,
            height - this.caret.getPaddingTop() - this.caret.getPaddingBottom());

        this.label.setPosition(
            this.scroll + this.getPaddingLeft() + this.label.getMarginLeft(),
            this.getPaddingTop() + this.label.getMarginTop(),
            this.label.getNaturalWidth(),
            height);

    }

}
