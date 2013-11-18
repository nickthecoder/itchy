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

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.editor.ComboBox;
import uk.co.nickthecoder.itchy.script.ScriptLanguage;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ClassNameBox extends Container
{

    private ScriptManager scriptManager;

    private ComboBox comboBox;

    private Button editButton;

    private Button reloadButton;

    private Label editButtonLabel;

    private Label errorText;

    private ClassName value;

    private Class<?> baseClass;

    public ClassNameBox( ScriptManager scriptManager, ClassName className, Class<?> baseClass )
    {
        super();

        this.type = "className";
        this.setLayout(new VerticalLayout());

        this.scriptManager = scriptManager;
        this.value = new ClassName(className.name);
        this.baseClass = baseClass;

        this.comboBox = new ComboBox(
            className.name,
            getKnownNames(Itchy.getGame().resources));

        this.editButtonLabel = new Label("Edit");
        this.editButton = new Button(this.editButtonLabel);

        this.reloadButton = new Button("Reload");

        this.addChild(this.comboBox);
        // this.addChild(this.editButton);
        this.comboBox.addChild(this.editButton);
        this.comboBox.addChild(this.reloadButton);

        this.errorText = new Label("");
        this.addChild(this.errorText);
        this.errorText.addStyle("error");
        this.errorText.setVisible(false);

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

        this.reloadButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                reload();
            }
        });

        update();
    }

    private void update()
    {
        this.errorText.setVisible(false);

        this.value.name = this.comboBox.getText();
        this.comboBox.addStyle("error", !isValid());

        boolean isValidScript = this.scriptManager.isValidScript(this.value);
        this.editButtonLabel.setText(isValidScript ? "Edit" : "Create");

        this.editButton.setVisible(this.value.isScript());
        this.reloadButton.setVisible(isValidScript);

        if (isValidScript) {

            try {
                this.scriptManager.loadScript(getClassName().name);
            } catch (ScriptException e) {
                this.scriptManager.resources.errorLog.log(e.getMessage());
                this.comboBox.addStyle("error");
                ScriptLanguage language = this.scriptManager.getLanguage(getClassName());
                this.errorText.setText(language.simpleMessage(e, false));
                this.errorText.setVisible(true);
            }

        }
    }
    
    private boolean isValid()
    {
        if ( this.value.isScript()) {
            return  this.scriptManager.isValidScript(this.value);
        } else {
            try {
                Class<?> klass = Class.forName(this.value.name);
                if (klass == null) {
                    return false;
                }
                klass.asSubclass(this.baseClass);
                
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private void reload()
    {
        fireChangeEvent();
    }

    public Set<String> getKnownNames( Resources resources )
    {
        if (this.baseClass == Role.class) {
            return resources.getRoleClassNames();
        } else if (this.baseClass == SceneDirector.class) {
            return resources.getSceneDirectorClassNames();
        } else if (this.baseClass == Director.class) {
            return resources.getDirectorClassNames();
        } else if (this.baseClass == CostumeProperties.class) {
            return resources.getCostumePropertiesClassNames();
        } else {
            return new HashSet<String>();
        }
    }

    public String getBaseName()
    {
        if (this.baseClass == Role.class) {
            return "Role";
        } else if (this.baseClass == SceneDirector.class) {
            return "SceneDirector";
        } else if (this.baseClass == Director.class) {
            return "Director";
        } else if (this.baseClass == CostumeProperties.class) {
            return "CostumeProperties";
        } else {
            return null;
        }
    }

    protected void edit()
    {

        if (!this.scriptManager.isValidScript(this.value)) {
            String baseName = getBaseName();
            if (baseName != null) {
                this.scriptManager.createScript(baseName, this.value);
                update();
            }
        }

        File file = this.scriptManager.getScript(this.value.name);

        try {
            Desktop.getDesktop().open(file);
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

    public void fireChangeEvent()
    {
        this.comboBox.fireChangeEvent();
    }

}
