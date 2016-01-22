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
import uk.co.nickthecoder.jame.event.MouseEvent;

public class TextArea extends ClickableContainer implements ContainerLayout, KeyListener, TextWidget
{
    private static final RGBA ANY_COLOR = new RGBA(0, 0, 0);

    private List<Label> labels;

    private final PlainContainer caret;

    private int boxWidthPixels;

    protected int boxWidth;

    protected int boxHeightPixels;

    protected int boxHeight;

    private int caretIndex;

    private int currentLine = 0;

    /**
     * The amount the text is scrolled left/right to ensure that the caret is visible.
     */
    private int scrollX = 0;

    private int scrollY = 0;

    private PlainContainer labelsContainer;

    private final List<ComponentChangeListener> changeListeners;

    public TextArea( String str )
    {
        if (str == null) {
            str = "";
        }

        this.setType("textArea");
        this.setLayout(this);

        this.labelsContainer = new PlainContainer();
        this.addChild(this.labelsContainer);

        this.labelsContainer.setLayout(new VerticalLayout());

        this.labels = new ArrayList<Label>();

        this.caret = new PlainContainer();
        this.caret.setType("caret");
        this.addChild(this.caret);

        this.caretIndex = str.length();

        this.setFill(true, true);

        this.boxWidth = 30;
        this.boxHeight = 5;

        this.caret.setVisible(false);
        this.focusable = true;

        this.changeListeners = new ArrayList<ComponentChangeListener>();

        this.setText(str);
    }

    @Override
    public void setText( String text )
    {
        if (text == null) {
            text = "";
        }

        String[] lines = text.split("\n");
        if (lines.length == 0) {
            lines = new String[] { "" };
        }

        for (Label label : this.labels) {
            label.remove();
        }
        this.labels.clear();

        for (String line : lines) {
            Label label = new Label(line);
            this.labels.add(label);
            this.labelsContainer.addChild(label);
            label.setExpansion(1.0);
        }

        // TO DO Do something better than set it to zero?
        this.caretIndex = 0;
        this.update();

    }

    @Override
    public String getText()
    {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Label label : this.labels) {
            if (!first) {
                buffer.append("\n");
            }
            first = false;
            buffer.append(label.getText());
        }
        return buffer.toString();
    }

    @Override
    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }

    @Override
    public int getBoxWidth()
    {
        return this.boxWidth;
    }

    @Override
    public void setBoxWidth( int value )
    {
        this.boxWidth = value;
    }

    private int getLineNumber( MouseEvent e )
    {
        // TO DO Work out which is the right label based on the y coordinate.
        return 0;
    }

    private Label getLabel( MouseEvent e )
    {
        return this.labels.get(getLineNumber(e));
    }

    private Label getCurrentLabel()
    {
        return this.labels.get(this.currentLine);
    }

    @Override
    public void onClick( MouseButtonEvent ke )
    {
        this.focus();
        if (ke == null) {
            return;
        }

        Label label = getLabel(ke);

        try {
            TrueTypeFont ttf = label.getFont().getSize(label.getFontSize());

            int index;
            for (index = 0; index <= label.getText().length(); index++) {
                String text = label.getText().substring(0, index);
                Surface surface = ttf.renderBlended(text, ANY_COLOR);
                int width = surface.getWidth();
                surface.free();
                if (width > ke.x - this.scrollX) {
                    this.setCaretPosition(index - 1);
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setCaretPosition(label.getText().length());

    }

    @Override
    public void onFocus( boolean focus )
    {
        this.caret.setVisible(focus);
    }

    @Override
    public void onKeyDown( KeyboardEvent ke )
    {
        Label label = getCurrentLabel();

        if (ke.symbol == Keys.HOME) {
            this.caretIndex = 0;
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.END) {
            this.caretIndex = label.getText().length();
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

        if (ke.symbol == Keys.UP) {
            if (this.currentLine > 0) {
                this.currentLine--;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.DOWN) {
            if (this.currentLine < this.labels.size() - 1) {
                this.currentLine++;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.RIGHT) {
            if (this.caretIndex < label.getText().length()) {
                this.caretIndex++;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.RETURN) {
            addReturn();
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.BACKSPACE) {
            if (this.caretIndex > 0) {
                int pos = this.caretIndex;
                label.setText(label.getText().substring(0, pos - 1) + label.getText().substring(pos));
                this.caretIndex--;
                this.update();
            } else {
                if (this.currentLine > 0) {
                    this.mergeLines();
                    this.update();
                }
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.DELETE) {
            if (this.caretIndex < label.getText().length()) {
                label.setText(label.getText().substring(0, this.caretIndex) + label.getText().substring(this.caretIndex + 1));
                this.update();
            } else {
                if (this.currentLine < this.labels.size() - 1) {
                    this.currentLine++;
                    this.mergeLines();
                    this.update();
                }
            }
            ke.stopPropagation();
        }

        if ((ke.symbol == Keys.v) && (Itchy.isCtrlDown())) {

            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String str = (String) clipboard.getData(DataFlavor.stringFlavor);

                this.setText(label.getText().substring(0, this.caretIndex) + str + label.getText().substring(this.caretIndex));

                this.caretIndex += str.length();

            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ke.stopPropagation();
        }

        if ((ke.c >= 32)) {
            label.setText(label.getText().substring(0, this.caretIndex) + ke.c + label.getText().substring(this.caretIndex));
            this.caretIndex++;
            ke.stopPropagation();
        }

        super.onKeyDown(ke);
    }

    private void addReturn()
    {
        Label label = this.getCurrentLabel();
        String old = label.getText();
        String partA = old.substring(0, this.caretIndex);

        String partB = this.caretIndex >= old.length() ? "" : old.substring(this.caretIndex);

        label.setText(partA);
        Label newLabel = new Label(partB);

        this.labels.add(this.currentLine + 1, newLabel);
        this.labelsContainer.addChild(this.currentLine + 1, newLabel);

        this.currentLine++;
        this.caretIndex = 0;
    }

    private void mergeLines()
    {
        Label label1 = this.labels.get(this.currentLine - 1);
        Label label2 = this.labels.get(this.currentLine);

        this.caretIndex = label1.getText().length();

        label1.setText(label1.getText() + label2.getText());
        label2.remove();
        this.labels.remove(this.currentLine);
        this.currentLine--;
        this.update();
    }

    @Override
    public void onKeyUp( KeyboardEvent ke )
    {
    }

    public void setCaretPosition( int index )
    {
        this.caretIndex = index;
        this.forceLayout();
        this.invalidate();
    }

    private void update()
    {
        int length = this.getCurrentLabel().getText().length();
        if (this.caretIndex > length) {
            this.caretIndex = length;
        }
        this.forceLayout();
        this.invalidate();

        fireChangeEvent();
    }

    public void fireChangeEvent()
    {
        for (ComponentChangeListener listener : this.changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void calculateRequirements( PlainContainer c )
    {
        int maxWidth = 0;
        for (Label label : this.labels) {
            int len = label.getText().length();
            if (len > maxWidth) {
                maxWidth = len;
            }
        }

        try {
            if (this.labels.size() == 0) {
                this.boxWidthPixels = 0;
                this.boxHeightPixels = 0;
            } else {
                TrueTypeFont ttf = this.labels.get(0).getFont().getSize(this.labels.get(0).getFontSize());

                Surface surface = ttf.renderBlended("M", ANY_COLOR);
                this.boxWidthPixels = surface.getWidth() * this.boxWidth;
                this.boxHeightPixels = surface.getHeight() * this.boxHeight;
                surface.free();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setNaturalWidth(this.boxWidthPixels + this.getPaddingLeft() + this.getPaddingRight());
        this.setNaturalHeight(this.boxHeightPixels + this.getPaddingTop() + this.getPaddingBottom());

    }

    @Override
    public void layout( PlainContainer c )
    {
        int width = this.boxWidthPixels;
        int height = this.boxHeightPixels;

        int lineHeight = this.boxHeightPixels / this.boxHeight;
        if (this.labels.size() > 0) {
            Label label = this.labels.get(0);
            lineHeight = label.getNaturalHeight() + label.getMarginTop() + label.getMarginBottom();
        }

        int caretX = 0;

        Label label = getCurrentLabel();

        try {
            String text = label.getText();
            if (text.length() > this.caretIndex) {
                text = text.substring(0, this.caretIndex);
            }
            Surface surface = label.getFont().getSize(label.getFontSize()).renderBlended(text, ANY_COLOR);

            caretX = surface.getWidth();
            surface.free();

        } catch (Exception e) {
            e.printStackTrace();
        }

        int caretWidth = this.caret.getRequiredWidth();
        int caretTotalX = this.scrollX + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;

        // We will jump the scroll by half the width of the textbox.
        while (caretTotalX + caretWidth > width) {
            this.scrollX -= width / 2;
            caretTotalX = this.scrollX + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
        }

        while (caretTotalX < 0) {
            this.scrollX += width / 2;
            if (this.scrollX > 0) {
                this.scrollX = 0;
            }
            caretTotalX = this.scrollX + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
        }

        if (this.scrollX < 0) {
            // Now lets check that we aren't too far
            int right = this.scrollX + label.getMarginLeft() + label.getNaturalWidth() + label.getMarginRight();
            if (right < width) {
                this.scrollX += width - right;
                caretTotalX = this.scrollX + this.getPaddingLeft() + this.caret.getMarginLeft() + caretX;
            }
        }

        int caretHeight = lineHeight - this.caret.getPaddingTop() - this.caret.getPaddingBottom();
        int caretY = this.getPaddingTop() + this.caret.getPaddingTop() + this.currentLine * lineHeight + this.scrollY;
        if (caretY < 0) {
            this.scrollY -= caretY;
        }

        if (caretY + caretHeight > height) {
            this.scrollY -= (caretY + caretHeight) - height;
        }
        caretY = this.getPaddingTop() + this.caret.getPaddingTop() + this.currentLine * lineHeight + this.scrollY;

        this.caret.setPosition(caretTotalX, caretY, caretWidth, caretHeight);

        this.labelsContainer.setPosition(
            this.scrollX + this.getPaddingLeft() + this.labelsContainer.getMarginLeft(),
            this.scrollY + this.getPaddingTop() + this.labelsContainer.getMarginTop(),
            width + (this.scrollX < 0 ? this.getPaddingLeft() : 0),
            this.labelsContainer.getNaturalHeight());

    }

}
