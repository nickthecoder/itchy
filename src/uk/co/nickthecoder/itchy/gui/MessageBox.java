/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

public class MessageBox extends Window
{

    public MessageBox( String title, Exception exception )
    {
        this(title, exception.getMessage() == null ? "" : exception.getMessage());
    }

    public MessageBox( String title, String message )
    {
        super(title);

        this.clientArea.addChild(new Label(message));
        this.clientArea.setLayout(new VerticalLayout());
        Container buttons = new Container();
        buttons.addStyle("buttonBar");

        this.clientArea.addChild(buttons);

        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                if (MessageBox.this.onOk()) {
                    hide();
                }
            }
        });
        buttons.addChild(ok);
    }

    protected boolean onOk()
    {
        return true;
    }

}
