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

    private final List<ComponentValidator> validators;

    public TextArea(String str)
    {
        if (str == null) {
            str = "";
        }

        this.setType("textArea");
        this.setLayout(this);

        labelsContainer = new PlainContainer();
        this.addChild(labelsContainer);

        labelsContainer.setLayout(new VerticalLayout());

        labels = new ArrayList<Label>();

        caret = new PlainContainer();
        caret.setType("caret");
        this.addChild(caret);

        caretIndex = str.length();

        this.setFill(true, true);

        boxWidth = 30;
        boxHeight = 5;

        caret.setVisible(false);
        focusable = true;

        changeListeners = new ArrayList<ComponentChangeListener>();
        validators = new ArrayList<ComponentValidator>();

        this.setText(str);
    }

    @Override
    public void setText(String text)
    {
        if (text == null) {
            text = "";
        }

        String[] lines = text.split("\n");
        if (lines.length == 0) {
            lines = new String[] { "" };
        }

        for (Label label : labels) {
            label.remove();
        }
        labels.clear();

        for (String line : lines) {
            Label label = new Label(line);
            labels.add(label);
            labelsContainer.addChild(label);
            label.setExpansion(1.0);
        }

        // TO DO Do something better than set it to zero?
        caretIndex = 0;
        this.update();

    }

    @Override
    public String getText()
    {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Label label : labels) {
            if (!first) {
                buffer.append("\n");
            }
            first = false;
            buffer.append(label.getText());
        }
        return buffer.toString();
    }

    @Override
    public void addChangeListener(ComponentChangeListener listener)
    {
        changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(ComponentChangeListener listener)
    {
        changeListeners.remove(listener);
    }

    @Override
    public void addValidator(ComponentValidator validator)
    {
        validators.add(validator);
    }

    @Override
    public void removeValidator(ComponentValidator validator)
    {
        validators.remove(validator);
    }

    @Override
    public int getBoxWidth()
    {
        return boxWidth;
    }

    @Override
    public void setBoxWidth(int value)
    {
        boxWidth = value;
    }

    private int getLineNumber(MouseEvent e)
    {
        // TO DO Work out which is the right label based on the y coordinate.
        return 0;
    }

    private Label getLabel(MouseEvent e)
    {
        return labels.get(getLineNumber(e));
    }

    private Label getCurrentLabel()
    {
        return labels.get(currentLine);
    }

    @Override
    public void onClick(MouseButtonEvent ke)
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
                if (width > ke.x - scrollX) {
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
    public void onFocus(boolean focus)
    {
        caret.setVisible(focus);
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
        Label label = getCurrentLabel();

        if (ke.symbol == Keys.HOME) {
            caretIndex = 0;
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.END) {
            caretIndex = label.getText().length();
            this.update();
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.LEFT) {
            if (caretIndex > 0) {
                caretIndex--;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.UP) {
            if (currentLine > 0) {
                currentLine--;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.DOWN) {
            if (currentLine < labels.size() - 1) {
                currentLine++;
                this.update();
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.RIGHT) {
            if (caretIndex < label.getText().length()) {
                caretIndex++;
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
            if (caretIndex > 0) {
                int pos = caretIndex;
                label.setText(label.getText().substring(0, pos - 1) + label.getText().substring(pos));
                caretIndex--;
                this.update();
            } else {
                if (currentLine > 0) {
                    this.mergeLines();
                    this.update();
                }
            }
            ke.stopPropagation();
        }

        if (ke.symbol == Keys.DELETE) {
            if (caretIndex < label.getText().length()) {
                label.setText(label.getText().substring(0, caretIndex) + label.getText().substring(caretIndex + 1));
                this.update();
            } else {
                if (currentLine < labels.size() - 1) {
                    currentLine++;
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

                this.setText(label.getText().substring(0, caretIndex) + str + label.getText().substring(caretIndex));

                caretIndex += str.length();

            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ke.stopPropagation();
        }

        if ((ke.c >= 32)) {
            label.setText(label.getText().substring(0, caretIndex) + ke.c + label.getText().substring(caretIndex));
            caretIndex++;
            ke.stopPropagation();
        }

        super.onKeyDown(ke);
    }

    private void addReturn()
    {
        Label label = this.getCurrentLabel();
        String old = label.getText();
        String partA = old.substring(0, caretIndex);

        String partB = caretIndex >= old.length() ? "" : old.substring(caretIndex);

        label.setText(partA);
        Label newLabel = new Label(partB);

        labels.add(currentLine + 1, newLabel);
        labelsContainer.addChild(currentLine + 1, newLabel);

        currentLine++;
        caretIndex = 0;
    }

    private void mergeLines()
    {
        Label label1 = labels.get(currentLine - 1);
        Label label2 = labels.get(currentLine);

        caretIndex = label1.getText().length();

        label1.setText(label1.getText() + label2.getText());
        label2.remove();
        labels.remove(currentLine);
        currentLine--;
        this.update();
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
    }

    public void setCaretPosition(int index)
    {
        caretIndex = index;
        this.forceLayout();
        this.invalidate();
    }

    private void update()
    {
        int length = this.getCurrentLabel().getText().length();
        if (caretIndex > length) {
            caretIndex = length;
        }
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
        for (ComponentChangeListener listener : changeListeners) {
            listener.changed();
        }
    }

    @Override
    public void calculateRequirements(PlainContainer c)
    {
        int maxWidth = 0;
        for (Label label : labels) {
            int len = label.getText().length();
            if (len > maxWidth) {
                maxWidth = len;
            }
        }

        try {
            if (labels.size() == 0) {
                boxWidthPixels = 0;
                boxHeightPixels = 0;
            } else {
                TrueTypeFont ttf = labels.get(0).getFont().getSize(labels.get(0).getFontSize());

                Surface surface = ttf.renderBlended("M", ANY_COLOR);
                boxWidthPixels = surface.getWidth() * boxWidth;
                boxHeightPixels = surface.getHeight() * boxHeight;
                surface.free();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setNaturalWidth(boxWidthPixels + this.getPaddingLeft() + this.getPaddingRight());
        this.setNaturalHeight(boxHeightPixels + this.getPaddingTop() + this.getPaddingBottom());

    }

    @Override
    public void layout(PlainContainer c)
    {
        int width = boxWidthPixels;
        int height = boxHeightPixels;

        int lineHeight = boxHeightPixels / boxHeight;
        if (labels.size() > 0) {
            Label label = labels.get(0);
            lineHeight = label.getNaturalHeight() + label.getMarginTop() + label.getMarginBottom();
        }

        int caretX = 0;

        Label label = getCurrentLabel();

        try {
            String text = label.getText();
            if (text.length() > caretIndex) {
                text = text.substring(0, caretIndex);
            }
            Surface surface = label.getFont().getSize(label.getFontSize()).renderBlended(text, ANY_COLOR);

            caretX = surface.getWidth();
            surface.free();

        } catch (Exception e) {
            e.printStackTrace();
        }

        int caretWidth = caret.getRequiredWidth();
        int caretTotalX = scrollX + this.getPaddingLeft() + caret.getMarginLeft() + caretX;

        // We will jump the scroll by half the width of the textbox.
        while (caretTotalX + caretWidth > width) {
            scrollX -= width / 2;
            caretTotalX = scrollX + this.getPaddingLeft() + caret.getMarginLeft() + caretX;
        }

        while (caretTotalX < 0) {
            scrollX += width / 2;
            if (scrollX > 0) {
                scrollX = 0;
            }
            caretTotalX = scrollX + this.getPaddingLeft() + caret.getMarginLeft() + caretX;
        }

        if (scrollX < 0) {
            // Now lets check that we aren't too far
            int right = scrollX + label.getMarginLeft() + label.getNaturalWidth() + label.getMarginRight();
            if (right < width) {
                scrollX += width - right;
                caretTotalX = scrollX + this.getPaddingLeft() + caret.getMarginLeft() + caretX;
            }
        }

        int caretHeight = lineHeight - caret.getPaddingTop() - caret.getPaddingBottom();
        int caretY = this.getPaddingTop() + caret.getPaddingTop() + currentLine * lineHeight + scrollY;
        if (caretY < 0) {
            scrollY -= caretY;
        }

        if (caretY + caretHeight > height) {
            scrollY -= (caretY + caretHeight) - height;
        }
        caretY = this.getPaddingTop() + caret.getPaddingTop() + currentLine * lineHeight + scrollY;

        caret.setPosition(caretTotalX, caretY, caretWidth, caretHeight);

        labelsContainer.setPosition(
            scrollX + this.getPaddingLeft() + labelsContainer.getMarginLeft(),
            scrollY + this.getPaddingTop() + labelsContainer.getMarginTop(),
            width + (scrollX < 0 ? this.getPaddingLeft() : 0),
            labelsContainer.getNaturalHeight());

    }

}
