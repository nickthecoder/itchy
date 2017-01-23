/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.AbstractInput;
import uk.co.nickthecoder.itchy.InputInterface;
import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.MouseInput;
import uk.co.nickthecoder.jame.event.ScanCode;
import uk.co.nickthecoder.jame.event.MouseButton;

public abstract class InputPicker extends Window
{
    // TODO Should we use scancodes or symbols (or both)
    public ScanCode key;

    public MouseButton mouseButton;

    public InputPicker()
    {
        super("Pick a Key");

        this.clientArea.setLayout(new VerticalLayout());

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);
        this.clientArea.setFill(true, false);

        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener()
        {

            @Override
            public void action()
            {
                AbstractInput input = null;

                if (InputPicker.this.key != null) {
                    input = new KeyInput(InputPicker.this.key);
                } else if (InputPicker.this.mouseButton != null) {
                    input = new MouseInput(InputPicker.this.mouseButton);
                }
                if (input == null) {
                    return;
                }

                InputPicker.this.hide();

                input.click = InputPicker.this.click.getValue();
                input.ctrlModifier = InputPicker.this.ctrl.getValue();
                input.shiftModifier = InputPicker.this.shift.getValue();
                input.altModifier = InputPicker.this.alt.getValue();

                InputPicker.this.pick(input);
            }

        });
        buttons.addChild(ok);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener()
        {

            @Override
            public void action()
            {
                InputPicker.this.hide();
            }

        });
        buttons.addChild(cancel);

        this.clientArea.addChild(createForm());
        this.clientArea.addChild(buttons);

    }

    ScanCode[][] main = new ScanCode[][]
    {
        {
            ScanCode.ESCAPE,
            ScanCode.F1,
            ScanCode.F2,
            ScanCode.F3,
            ScanCode.F4,
            ScanCode.F5,
            ScanCode.F6,
            ScanCode.F7,
            ScanCode.F8,
            ScanCode.F9,
            ScanCode.F10,
            ScanCode.F11,
            ScanCode.F12,
    },
        {
            // TODO ADD back quote to input picker. ScanCode.BACKQUOTE,
            ScanCode.KEY_1,
            ScanCode.KEY_2,
            ScanCode.KEY_3,
            ScanCode.KEY_4,
            ScanCode.KEY_5,
            ScanCode.KEY_6,
            ScanCode.KEY_7,
            ScanCode.KEY_8,
            ScanCode.KEY_9,
            ScanCode.KEY_0,
            ScanCode.MINUS,
            ScanCode.EQUALS,
            ScanCode.BACKSPACE
    },
        {
            ScanCode.TAB,
            ScanCode.Q,
            ScanCode.W,
            ScanCode.E,
            ScanCode.R,
            ScanCode.T,
            ScanCode.Y,
            ScanCode.U,
            ScanCode.I,
            ScanCode.O,
            ScanCode.P,
            ScanCode.LEFTBRACKET,
            ScanCode.RIGHTBRACKET,
            ScanCode.RETURN
    },
        {
            ScanCode.CAPSLOCK,
            ScanCode.A,
            ScanCode.S,
            ScanCode.D,
            ScanCode.F,
            ScanCode.G,
            ScanCode.H,
            ScanCode.J,
            ScanCode.K,
            ScanCode.L,
            ScanCode.SEMICOLON // TODO Add quote and hash keys to input picker,
            //ScanCode.QUOTE,
            //ScanCode.HASH
    },
        {
            ScanCode.LSHIFT,
            ScanCode.BACKSLASH,
            ScanCode.Z,
            ScanCode.X,
            ScanCode.C,
            ScanCode.V,
            ScanCode.B,
            ScanCode.N,
            ScanCode.M,
            ScanCode.COMMA,
            ScanCode.PERIOD,
            ScanCode.SLASH,
            ScanCode.RSHIFT
    },
        {
            ScanCode.LCTRL,
            // TODO Add to input picker : ScanCode.LSUPER,
            ScanCode.LALT,
            ScanCode.SPACE,
            ScanCode.RALT,
            // TODO Add to input picker : ScanCode.RSUPER,
            ScanCode.RCTRL,
    }
    };

    ScanCode[][] middle = new ScanCode[][]
    {
        {
            ScanCode.PRINTSCREEN,
            ScanCode.SCROLLLOCK,
            ScanCode.PAUSE
    },
        {
        },
        {
            ScanCode.INSERT,
            ScanCode.HOME,
            ScanCode.PAGEUP,
        },
        {
            ScanCode.DELETE,
            ScanCode.END,
            ScanCode.PAGEDOWN
        },
        {
        },
        {
            ScanCode.UP,
        },
        {
            ScanCode.LEFT,
            ScanCode.DOWN,
            ScanCode.RIGHT
        }
    };

    ScanCode[][] keypad = new ScanCode[][] {
        {
        },
        {
            ScanCode.NUMLOCK,
            ScanCode.KP_DIVIDE,
            ScanCode.KP_MULTIPLY,
        },
        {
            ScanCode.KP_7,
            ScanCode.KP_8,
            ScanCode.KP_9,
            ScanCode.KP_MINUS
        },
        {
            ScanCode.KP_4,
            ScanCode.KP_5,
            ScanCode.KP_6,
            ScanCode.KP_PLUS
        },
        {
            ScanCode.KP_3,
            ScanCode.KP_2,
            ScanCode.KP_1
        },
        {
            ScanCode.KP_0,
            ScanCode.KP_PERIOD,
            ScanCode.KP_ENTER
        }
    };

    ScanCode[][][] keyboard = new ScanCode[][][] {
        this.main, this.middle, this.keypad
    };

    protected PlainContainer createForm()
    {
        PlainContainer result = new PlainContainer();
        result.setLayout(new VerticalLayout());
        result.setYSpacing(20);

        PlainContainer keyboardContainer = new PlainContainer();
        result.addChild(keyboardContainer);
        keyboardContainer.setLayout(new HorizontalLayout());
        keyboardContainer.setXSpacing(20);

        ButtonGroup buttonGroup = new ButtonGroup();

        for (ScanCode[][] s : this.keyboard) {
            PlainContainer section = new PlainContainer();
            section.setLayout(new VerticalLayout());
            keyboardContainer.addChild(section);

            for (ScanCode[] r : s) {
                PlainContainer row = new PlainContainer();
                if (r.length == 0) {
                    row.setMinimumHeight(20); // Arbitrary spacing
                } else {
                    row.addStyle("combo");
                    row.setLayout(new HorizontalLayout());

                    for (ScanCode key : r) {
                        AbstractComponent button = createKeyButton(buttonGroup, key);
                        row.addChild(button);
                    }
                }
                section.addChild(row);
            }
        }

        PlainContainer mouseButtons = new PlainContainer();
        mouseButtons.setXSpacing(4);
        mouseButtons.setYAlignment(0.5);
        mouseButtons.addChild(new Label("Moues Buttons : "));
        for (MouseButton mouseButton : MouseButton.values()) {
            Component button = createMouseButton(buttonGroup, mouseButton);
            mouseButtons.addChild(button);
        }
        result.addChild(mouseButtons);

        PlainContainer modifiers = new PlainContainer();
        modifiers.setXSpacing(40);
        result.addChild(modifiers);
        modifiers.setYAlignment(0.5);

        modifiers.addChild(new Label("Modifiers : "));

        this.ctrl = new CheckBox();
        modifiers.addChild(makeModifier(this.ctrl, "ctrl"));

        this.shift = new CheckBox();
        modifiers.addChild(makeModifier(this.shift, "shift"));

        this.alt = new CheckBox();
        modifiers.addChild(makeModifier(this.alt, "alt"));

        this.meta = new CheckBox();
        modifiers.addChild(makeModifier(this.meta, "meta"));

        modifiers.addChild(new Label(""));
        this.click = new CheckBox();
        modifiers.addChild(makeModifier(this.click, "Click ?"));

        return result;
    }

    protected AbstractComponent makeModifier(AbstractComponent component, String label)
    {
        PlainContainer container = new PlainContainer();
        container.setLayout(new HorizontalLayout());
        container.setXSpacing(10);
        container.setYAlignment(0.5);
        container.addChild(component);
        container.addChild(new Label(label));
        return container;
    }

    public CheckBox click;
    public CheckBox ctrl;
    public CheckBox shift;
    public CheckBox alt;
    public CheckBox meta;

    protected AbstractComponent createKeyButton(ButtonGroup buttonGroup, final ScanCode key)
    {
        ToggleButton button = new ToggleButton(key.label);
        buttonGroup.add(button);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                InputPicker.this.key = key;
                InputPicker.this.mouseButton = null;
            }
        });
        return button;
    }

    protected AbstractComponent createMouseButton(ButtonGroup buttonGroup, final MouseButton mouseButton)
    {
        ToggleButton button = new ToggleButton(mouseButton.label);
        buttonGroup.add(button);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                InputPicker.this.key = null;
                InputPicker.this.mouseButton = mouseButton;
            }
        });
        return button;
    }

    public abstract void pick(InputInterface input);

}
