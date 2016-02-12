/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

public class MessageDialog extends Window
{        
    public MessageDialog( String title, String message )
    {
        super( title );
        
        this.clientArea.setLayout(new VerticalLayout());

        Component messageComponent;
        if ( message.contains("\n")) {
            messageComponent = Label.createMultiline( message );
        } else {
            messageComponent = new Label(message);
        }
        this.clientArea.addChild( Scroll.ifNeeded(messageComponent, 400, 300) );
        //this.clientArea.addChild( messageComponent );

        PlainContainer buttons = new PlainContainer();
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
