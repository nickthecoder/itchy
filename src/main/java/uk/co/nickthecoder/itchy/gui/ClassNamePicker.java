/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.HashMap;
import java.util.Set;

import uk.co.nickthecoder.itchy.script.ScriptManager;

public abstract class ClassNamePicker extends Window
{
    private HashMap<String, GridLayout> sections = new HashMap<String, GridLayout>();

    private PlainContainer main;

    private GridLayout previousGrid;

    public ClassNamePicker( String title, Class<?> baseClass, Set<String> names, String selected )
    {
        super(title);

        this.clientArea.setFill(true, false);
        this.clientArea.setLayout(new VerticalLayout());

        this.main = new PlainContainer();
        this.main.setLayout(new VerticalLayout());
        this.main.setFill(true, false);
        this.main.setYSpacing(10);

        VerticalScroll vs = new VerticalScroll(this.main);
        this.clientArea.addChild(vs);

        for (String name : names) {

            Component component = this.createButton(name);

            if (((name == null) && (selected == null)) || ((name != null) && name.equals(selected))) {
                component.focus();
            }

        }

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setLayout(new HorizontalLayout());
        buttons.setXAlignment(0.5f);

        Button cancelButton = new Button("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                hide();
            }

        });
        buttons.addChild(cancelButton);

        this.clientArea.addChild(buttons);

        for (GridLayout grid : this.sections.values()) {
            grid.endRow();
        }
    }

    private Component createButton( final String wholeName )
    {
        String packageName;
        String labelText = wholeName;

        if (ScriptManager.isScript(wholeName)) {
            packageName = "Scripts";
        } else {
            int dot = wholeName.lastIndexOf(".");
            if (dot <= 0) {
                packageName = "No Package";
            } else {
                packageName = "Package : " + wholeName.substring(0, dot);
                labelText = wholeName.substring(dot + 1);
            }
        }

        GridLayout grid = this.sections.get(packageName);
        if (grid == null) {
            PlainContainer headingAndBody = new PlainContainer();
            headingAndBody.addStyle("panel");

            headingAndBody.setLayout(new VerticalLayout());
            Label packageLabel = new Label(packageName);
            packageLabel.addStyle("bold");
            headingAndBody.addChild(packageLabel);
            headingAndBody.setYSpacing(5);

            PlainContainer body = new PlainContainer();
            body.setFill(true, false);
            body.setXSpacing(5);
            body.setYSpacing(5);
            grid = new GridLayout(body, 4);
            headingAndBody.addChild(body);
            this.main.addChild(headingAndBody);
            this.sections.put(packageName, grid);

            if (this.previousGrid != null) {
                grid.groupWith(this.previousGrid);
            }
            this.previousGrid = grid;
        }

        final Button button = new Button(new Label(labelText));

        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                hide();
                pick(wholeName);
            }
        });
        grid.addChild(button);

        return button;
    }

    public abstract void pick( String name );
}
