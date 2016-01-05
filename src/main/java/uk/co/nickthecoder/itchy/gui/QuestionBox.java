/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

public class QuestionBox extends Window
{
    public Label okLabel;

    public Label cancelLabel;

    public boolean result = false;

    public QuestionBox( String title, String message )
    {
        super(title);

        this.clientArea.addChild(new Label(message));
        this.clientArea.setLayout(new VerticalLayout());
        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");

        this.clientArea.addChild(buttons);

        this.okLabel = new Label("Ok");
        this.cancelLabel = new Label("Cancel");

        GuiButton ok = new GuiButton(this.okLabel);
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                QuestionBox.this.result = true;
                if (QuestionBox.this.onOk()) {
                    hide();
                }
            }
        });
        buttons.addChild(ok);

        GuiButton cancel = new GuiButton(this.cancelLabel);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                QuestionBox.this.result = false;
                if (QuestionBox.this.onCancel()) {
                    hide();
                }
            }
        });
        buttons.addChild(cancel);
    }

    protected boolean onOk()
    {
        return true;
    }

    protected boolean onCancel()
    {
        return true;
    }

}
