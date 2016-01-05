/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.ButtonGroup;
import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ToggleButton;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.jame.event.KeysEnum;

public abstract class KeyInputPicker extends Window
{
    public KeysEnum key;

    public KeyInputPicker()
    {
        super("Pick a Key");
        
        this.clientArea.setLayout(new VerticalLayout());
        
        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);
        this.clientArea.setFill(true, false);


        GuiButton ok = new GuiButton("Ok");
        ok.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                if (KeyInputPicker.this.key == null) {
                    return;
                }
                KeyInputPicker.this.hide();
                
                KeyInput keyInput = new KeyInput( KeyInputPicker.this.key );
                keyInput.ctrlModifier = KeyInputPicker.this.ctrl.getValue();
                keyInput.shiftModifier = KeyInputPicker.this.shift.getValue();
                keyInput.altModifier = KeyInputPicker.this.alt.getValue();
                keyInput.metaModifier = KeyInputPicker.this.meta.getValue();
                
                KeyInputPicker.this.pick(keyInput);
            }

        });
        buttons.addChild(ok);
        
        GuiButton cancel = new GuiButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                KeyInputPicker.this.hide();
            }

        });
        buttons.addChild(cancel);

        this.clientArea.addChild(createForm());
        this.clientArea.addChild(buttons);

    }

    KeysEnum[][] main = new KeysEnum[][]
    {
        {
            KeysEnum.ESCAPE,
            KeysEnum.F1,
            KeysEnum.F2,
            KeysEnum.F3,
            KeysEnum.F4,
            KeysEnum.F5,
            KeysEnum.F6,
            KeysEnum.F7,
            KeysEnum.F8,
            KeysEnum.F9,
            KeysEnum.F10,
            KeysEnum.F11,
            KeysEnum.F12,
        },
        {
            KeysEnum.BACKQUOTE,
            KeysEnum.KEY_1,
            KeysEnum.KEY_2,
            KeysEnum.KEY_3,
            KeysEnum.KEY_4,
            KeysEnum.KEY_5,
            KeysEnum.KEY_6,
            KeysEnum.KEY_7,
            KeysEnum.KEY_8,
            KeysEnum.KEY_9,
            KeysEnum.KEY_0,
            KeysEnum.MINUS,
            KeysEnum.EQUALS,
            KeysEnum.BACKSPACE
        },
        {
            KeysEnum.TAB,
            KeysEnum.q,
            KeysEnum.w,
            KeysEnum.e,
            KeysEnum.r,
            KeysEnum.t,
            KeysEnum.y,
            KeysEnum.u,
            KeysEnum.i,
            KeysEnum.o,
            KeysEnum.p,
            KeysEnum.LEFTBRACKET,
            KeysEnum.RIGHTBRACKET,
            KeysEnum.RETURN
        },
        {
            KeysEnum.CAPSLOCK,
            KeysEnum.a,
            KeysEnum.s,
            KeysEnum.d,
            KeysEnum.f,
            KeysEnum.g,
            KeysEnum.h,
            KeysEnum.j,
            KeysEnum.k,
            KeysEnum.l,
            KeysEnum.SEMICOLON,
            KeysEnum.QUOTE,
            KeysEnum.HASH
        },
        {
            KeysEnum.LSHIFT,
            KeysEnum.BACKSLASH,
            KeysEnum.z,
            KeysEnum.x,
            KeysEnum.c,
            KeysEnum.v,
            KeysEnum.b,
            KeysEnum.n,
            KeysEnum.m,
            KeysEnum.COMMA,
            KeysEnum.PERIOD,
            KeysEnum.SLASH,
            KeysEnum.RSHIFT
        },
        {
            KeysEnum.LCTRL,
            KeysEnum.LSUPER,
            KeysEnum.LALT,
            KeysEnum.SPACE,
            KeysEnum.RALT,
            KeysEnum.RSUPER,
            KeysEnum.RCTRL,
        }
    };

    KeysEnum[][] middle = new KeysEnum[][]
    {
        {
            KeysEnum.PRINT,
            KeysEnum.SCROLLOCK,
            KeysEnum.PAUSE
        },
        {
        },
        {
            KeysEnum.INSERT,
            KeysEnum.HOME,
            KeysEnum.PAGEUP,
        },
        {
            KeysEnum.DELETE,
            KeysEnum.END,
            KeysEnum.PAGEDOWN
        },
        {
        },
        {
            KeysEnum.UP,
        },
        {
            KeysEnum.LEFT,
            KeysEnum.DOWN,
            KeysEnum.RIGHT
        }
    };

    KeysEnum[][] keypad = new KeysEnum[][] {
        {
        },
        {
            KeysEnum.NUMLOCK,
            KeysEnum.KP_DIVIDE,
            KeysEnum.KP_MULTIPLY,
        },
        {
            KeysEnum.KP7,
            KeysEnum.KP8,
            KeysEnum.KP9,
            KeysEnum.KP_MINUS
        },
        {
            KeysEnum.KP4,
            KeysEnum.KP5,
            KeysEnum.KP6,
            KeysEnum.KP_PLUS
        },
        {
            KeysEnum.KP3,
            KeysEnum.KP2,
            KeysEnum.KP1
        },
        {
            KeysEnum.KP0,
            KeysEnum.KP_PERIOD,
            KeysEnum.KP_ENTER
        }
    };

    KeysEnum[][][] keyboard = new KeysEnum[][][] {
        this.main, this.middle, this.keypad
    };

    protected PlainContainer createForm()
    {        
        PlainContainer result = new PlainContainer();
        result.setLayout( new VerticalLayout() );
        result.setYSpacing(20);
        
        PlainContainer keyboardContainer = new PlainContainer();
        result.addChild(keyboardContainer);
        keyboardContainer.setLayout(new HorizontalLayout());
        keyboardContainer.setXSpacing(20);
        
        ButtonGroup buttonGroup = new ButtonGroup();

        for (KeysEnum[][] s : this.keyboard) {
            PlainContainer section = new PlainContainer();
            section.setLayout(new VerticalLayout());
            keyboardContainer.addChild(section);

            for (KeysEnum[] r : s) {
                PlainContainer row = new PlainContainer();
                if (r.length == 0) {
                    row.setMinimumHeight(20); // Arbitrary spacing
                } else {
                    row.addStyle("combo");
                    row.setLayout(new HorizontalLayout());
    
                    for (KeysEnum key : r) {
                        AbstractComponent button = createKeyButton(buttonGroup, key);
                        row.addChild(button);
                    }
                }
                section.addChild(row);
            }
        }
        
        PlainContainer modifiers = new PlainContainer();
        modifiers.setXSpacing( 40 );
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

        return result;
    }

    protected AbstractComponent makeModifier( AbstractComponent component, String label )
    {
        PlainContainer container = new PlainContainer();
        container.setLayout(new HorizontalLayout());
        container.setXSpacing(10);
        container.setYAlignment(0.5);
        container.addChild( component );
        container.addChild( new Label(label) );
        return container;
    }
    
    public CheckBox ctrl;
    public CheckBox shift;
    public CheckBox alt;
    public CheckBox meta;
    
    protected AbstractComponent createKeyButton( ButtonGroup buttonGroup, final KeysEnum key )
    {
        ToggleButton button = new ToggleButton(key.label);
        buttonGroup.add(button);
        button.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                KeyInputPicker.this.key = key;
            }

        });

        return button;
    }

    public abstract void pick( KeyInput keyInput );

}
