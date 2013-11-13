/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;


public class MessageDialog extends Window
{
    public MessageDialog( String title, String message )
    {
        super(title);
        this.clientArea.setLayout(new VerticalLayout());

        this.clientArea.addChild(new Label(message));

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        this.clientArea.addChild(buttons);

        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                MessageDialog.this.hide();
            }
        });
        buttons.addChild(ok);

    }

}
