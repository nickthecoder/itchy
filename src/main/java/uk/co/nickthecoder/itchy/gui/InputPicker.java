/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.AbstractInput;
import uk.co.nickthecoder.itchy.InputInterface;
import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.MouseInput;
import uk.co.nickthecoder.jame.event.Key;
import uk.co.nickthecoder.jame.event.MouseButton;

public abstract class InputPicker extends Window
{
    public Key key;

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
                input.metaModifier = InputPicker.this.meta.getValue();

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

    Key[][] main = new Key[][]
    {
        {
            Key.ESCAPE,
            Key.F1,
            Key.F2,
            Key.F3,
            Key.F4,
            Key.F5,
            Key.F6,
            Key.F7,
            Key.F8,
            Key.F9,
            Key.F10,
            Key.F11,
            Key.F12,
    },
        {
            Key.BACKQUOTE,
            Key.KEY_1,
            Key.KEY_2,
            Key.KEY_3,
            Key.KEY_4,
            Key.KEY_5,
            Key.KEY_6,
            Key.KEY_7,
            Key.KEY_8,
            Key.KEY_9,
            Key.KEY_0,
            Key.MINUS,
            Key.EQUALS,
            Key.BACKSPACE
    },
        {
            Key.TAB,
            Key.q,
            Key.w,
            Key.e,
            Key.r,
            Key.t,
            Key.y,
            Key.u,
            Key.i,
            Key.o,
            Key.p,
            Key.LEFTBRACKET,
            Key.RIGHTBRACKET,
            Key.RETURN
    },
        {
            Key.CAPSLOCK,
            Key.a,
            Key.s,
            Key.d,
            Key.f,
            Key.g,
            Key.h,
            Key.j,
            Key.k,
            Key.l,
            Key.SEMICOLON,
            Key.QUOTE,
            Key.HASH
    },
        {
            Key.LSHIFT,
            Key.BACKSLASH,
            Key.z,
            Key.x,
            Key.c,
            Key.v,
            Key.b,
            Key.n,
            Key.m,
            Key.COMMA,
            Key.PERIOD,
            Key.SLASH,
            Key.RSHIFT
    },
        {
            Key.LCTRL,
            Key.LSUPER,
            Key.LALT,
            Key.SPACE,
            Key.RALT,
            Key.RSUPER,
            Key.RCTRL,
    }
    };

    Key[][] middle = new Key[][]
    {
        {
            Key.PRINT,
            Key.SCROLLOCK,
            Key.PAUSE
    },
        {
        },
        {
            Key.INSERT,
            Key.HOME,
            Key.PAGEUP,
        },
        {
            Key.DELETE,
            Key.END,
            Key.PAGEDOWN
        },
        {
        },
        {
            Key.UP,
        },
        {
            Key.LEFT,
            Key.DOWN,
            Key.RIGHT
        }
    };

    Key[][] keypad = new Key[][] {
        {
        },
        {
            Key.NUMLOCK,
            Key.KP_DIVIDE,
            Key.KP_MULTIPLY,
        },
        {
            Key.KP7,
            Key.KP8,
            Key.KP9,
            Key.KP_MINUS
        },
        {
            Key.KP4,
            Key.KP5,
            Key.KP6,
            Key.KP_PLUS
        },
        {
            Key.KP3,
            Key.KP2,
            Key.KP1
        },
        {
            Key.KP0,
            Key.KP_PERIOD,
            Key.KP_ENTER
        }
    };

    Key[][][] keyboard = new Key[][][] {
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

        for (Key[][] s : this.keyboard) {
            PlainContainer section = new PlainContainer();
            section.setLayout(new VerticalLayout());
            keyboardContainer.addChild(section);

            for (Key[] r : s) {
                PlainContainer row = new PlainContainer();
                if (r.length == 0) {
                    row.setMinimumHeight(20); // Arbitrary spacing
                } else {
                    row.addStyle("combo");
                    row.setLayout(new HorizontalLayout());

                    for (Key key : r) {
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

    protected AbstractComponent createKeyButton(ButtonGroup buttonGroup, final Key key)
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
