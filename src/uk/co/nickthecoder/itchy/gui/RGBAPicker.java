/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.RGBA;

public class RGBAPicker extends Window
{

    private TextBox target;

    private IntegerBox red;

    private IntegerBox green;

    private IntegerBox blue;

    private Slider redSlider;

    private Slider greenSlider;

    private Slider blueSlider;

    private IntegerBox alpha;

    private Slider alphaSlider;

    private TextBox rgbaText;

    private RGBA color;

    private String originalValue;

    private boolean includeAlpha = false;

    private Swatch swatch;

    private ComponentChangeListener rgbChange;

    public RGBAPicker( boolean includeAlpha )
    {
        this(includeAlpha, new TextBox(""));
    }

    public RGBAPicker( boolean includeAlpha, TextBox target )
    {
        super("Color Picker");
        this.includeAlpha = includeAlpha;

        this.originalValue = target.getText();

        this.target = target;

        this.clientArea.setLayout(new VerticalLayout());

        this.rgbChange = new ComponentChangeListener() {
            @Override
            public void changed()
            {
                RGBAPicker.this.updateFromRGB();
            }
        };

        createRGBForm();
        createLowerForm();
        createButtonBar();
    }

    private GridLayout rgbGrid;
    private GridLayout rgbaGrid;

    private final void createRGBForm()
    {
        Container rgbForm = new Container();
        rgbForm.setYAlignment(0.5);
        rgbForm.setFill(true, false);
        rgbForm.setXSpacing(10);
        rgbForm.setYSpacing(4);

        this.color = null;
        try {
            this.color = RGBA.parse(this.target.getText());
        } catch (Exception e) {
        }
        if (this.color == null) {
            this.color = RGBA.WHITE;
        }

        this.red = new IntegerBox(this.color.r, 3).range(0, 255);
        this.green = new IntegerBox(this.color.g, 3).range(0, 255);
        this.blue = new IntegerBox(this.color.b, 3).range(0, 255);

        this.redSlider = new Slider(this.color.r).range(0, 255).link(this.red);
        this.greenSlider = new Slider(this.color.g).range(0, 255).link(this.green);
        this.blueSlider = new Slider(this.color.b).range(0, 255).link(this.blue);

        this.redSlider.setExpansion(1);
        this.greenSlider.setExpansion(1);
        this.blueSlider.setExpansion(1);

        this.rgbGrid = new GridLayout(rgbForm, 3);
        rgbForm.setLayout(this.rgbGrid);
        this.rgbGrid.addRow("Red", this.redSlider, this.red);
        this.rgbGrid.addRow("Green", this.greenSlider, this.green);
        this.rgbGrid.addRow("Blue", this.blueSlider, this.blue);

        this.red.addChangeListener(this.rgbChange);
        this.green.addChangeListener(this.rgbChange);
        this.blue.addChangeListener(this.rgbChange);

        this.clientArea.addChild(rgbForm);
    }

    private void createLowerForm()
    {
        Container rgbaForm = new Container();
        rgbaForm.setYAlignment(0.5);
        rgbaForm.setFill(true, false);

        this.rgbaGrid = new GridLayout(rgbaForm, 3);
        rgbaForm.setLayout(this.rgbaGrid);
        rgbaForm.setXSpacing(10);
        rgbaForm.setYSpacing(4);
        this.rgbaGrid.groupWith(this.rgbGrid);

        this.alpha = new IntegerBox(this.color.a, 3);
        this.alpha.addChangeListener(this.rgbChange);
        this.alphaSlider = new Slider(this.color.a).range(0, 255).link(this.alpha);
        this.alphaSlider.setExpansion(1);
        if (this.includeAlpha) {
            this.rgbaGrid.addRow("Alpha", this.alphaSlider, this.alpha);
        }

        this.rgbaText = new TextBox("");
        this.rgbaText.setBoxWidth(8);
        this.rgbaText.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                updateFromRGBA();
                RGBAPicker.this.target.setText(RGBAPicker.this.rgbaText.getText());
            }
        });

        this.swatch = new Swatch(200, 30);

        this.rgbaGrid.addRow(this.includeAlpha ? "RGBA" : "RGB", this.rgbaText, null);

        this.clientArea.addChild(rgbaForm);
        this.clientArea.addChild(this.swatch);

        updateFromRGB();
    }

    private final void createButtonBar()
    {
        Container buttonBar = new Container();
        buttonBar.addStyle("buttonBar");
        buttonBar.setXAlignment(0.5f);

        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                RGBAPicker.this.onOk();
            }
        });
        buttonBar.addChild(ok);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                RGBAPicker.this.onCancel();
            }
        });
        buttonBar.addChild(cancel);

        this.clientArea.addChild(buttonBar);

    }

    /**
     * Used to ensure that a change of the RBGA text field doesn't cause itself to be updated again.
     */
    private boolean updating;

    private void updateFromRGBA()
    {
        this.updating = true;
        try {
            RGBA color = RGBA.parse(this.rgbaText.getText());
            this.red.setValue(color.r);
            this.green.setValue(color.g);
            this.blue.setValue(color.b);
            this.alpha.setValue(color.a);
        } catch (Exception e) {
            // Do nothing
        } finally {
            this.updating = false;
        }
    }

    private void updateFromRGB()
    {
        try {
            this.color = new RGBA(this.red.getValue(), this.green.getValue(), this.blue.getValue(),
                this.alpha.getValue());

            this.redSlider.setValue(this.red.getValue());
            this.greenSlider.setValue(this.green.getValue());
            this.blueSlider.setValue(this.blue.getValue());

            if (!this.updating) {
                this.rgbaText.setText(this.includeAlpha ?
                    this.color.getRGBACode() :
                    this.color.getRGBCode());
            }

            this.swatch.setSwatchColor(this.color);

        } catch (Exception e) {
            // Do nothing
        }
    }

    private void onOk()
    {
        this.hide();
    }

    private void onCancel()
    {
        this.target.setText(this.originalValue);
        this.hide();
    }

}
