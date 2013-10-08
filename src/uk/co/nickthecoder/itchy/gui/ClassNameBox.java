/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.awt.Desktop;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.editor.ComboBox;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ClassNameBox extends Container
{

    private ScriptManager scriptManager;

    private ComboBox comboBox;

    private Button editButton;

    private Label editButtonLabel;

    private ClassName value;

    private Class<?> baseClass;

    public ClassNameBox( ScriptManager scriptManager, ClassName className, Class<?> baseClass )
    {
        super();

        this.type = "className";

        this.scriptManager = scriptManager;
        this.value = new ClassName(className.name);
        this.baseClass = baseClass;

        this.comboBox = new ComboBox(
            className.name,
            getKnownNames(Itchy.getGame().resources));

        this.editButtonLabel = new Label("Edit");
        this.editButton = new Button(this.editButtonLabel);

        this.addChild(this.comboBox);
        //this.addChild(this.editButton);
        this.comboBox.addChild(this.editButton);

        this.comboBox.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                update();
            }

        });

        this.editButton.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                edit();
            }

        });

        update();
    }

    private void update()
    {
        this.value.name = this.comboBox.getText();
        this.editButtonLabel.setText(
            this.scriptManager.isValidScript(this.value) ? "Edit" : "Create");

        this.editButton.setVisible(this.value.isScript());
    }

    public Set<String> getKnownNames( Resources resources )
    {
        if (this.baseClass == Behaviour.class) {
            return resources.getBehaviourClassNames();
        } else if (this.baseClass == SceneBehaviour.class) {
            return resources.getSceneBehaviourClassNames();
        } else if (this.baseClass == Game.class) {
            return resources.getGameClassNames();
        } else if (this.baseClass == CostumeProperties.class) {
            return resources.getCostumePropertiesClassNames();
        } else {
            return new HashSet<String>();
        }
    }

    public String getBaseName()
    {
        if (this.baseClass == Behaviour.class) {
            return "Behaviour";
        } else if (this.baseClass == SceneBehaviour.class) {
            return "SceneBehaviour";
        } else if (this.baseClass == Game.class) {
            return "Game";
        } else if (this.baseClass == CostumeProperties.class) {
            return "CostumeProperties";
        } else {
            return null;
        }
    }

    public void edit()
    {

        if (!this.scriptManager.isValidScript(this.value)) {
            String baseName = getBaseName();
            if (baseName != null) {
                this.scriptManager.createScript(baseName, this.value);
            }
        }

        File file = this.scriptManager.getScript(this.value.name);
        // String textEditor = Editor.singleton.preferences.textEditor;

        try {
            Desktop.getDesktop().open(file);
            // Runtime.getRuntime().exec(new String[] { textEditor, file.getPath() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassName getClassName()
    {
        return this.value;
    }

    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.comboBox.addChangeListener(ccl);
    }

    public void removeChangeListener( ComponentChangeListener ccl )
    {
        this.comboBox.removeChangeListener(ccl);
    }

}
