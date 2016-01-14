/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.awt.Desktop;
import java.io.File;
import java.util.Set;

import javax.script.ScriptException;

import uk.co.nickthecoder.itchy.Director;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.script.ScriptLanguage;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ClassNameBox extends PlainContainer
{
    private ScriptManager scriptManager;

    private TextBox textBox;

    private GuiButton editButton;

    private GuiButton reloadButton;

    private Label editButtonLabel;

    private Label errorText;

    private ClassName value;

    private Class<?> baseClass;

    public ClassNameBox( final ScriptManager scriptManager, ClassName className, final Class<?> baseClass )
    {
        super();

        this.type = "className";
        this.setLayout(new VerticalLayout());

        this.scriptManager = scriptManager;
        this.value = new ClassName(baseClass, className.name);
        this.baseClass = baseClass;

        PlainContainer main = new PlainContainer();
        this.addChild(main);
        main.type = "comboBox";
        main.addStyle("combo");

        this.textBox = new TextBox(className.name);
        main.addChild(this.textBox);

        GuiButton pick = new GuiButton("...");
        main.addChild(pick);

        this.editButtonLabel = new Label("Edit");
        this.editButton = new GuiButton(this.editButtonLabel);
        main.addChild(this.editButton);

        this.reloadButton = new GuiButton("Reload");
        main.addChild(this.reloadButton);

        this.errorText = new Label("");
        this.addChild(this.errorText);

        this.errorText.addStyle("error");
        this.errorText.setVisible(false);

        this.textBox.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                update();
            }

        });

        pick.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                final ClassNamePicker picker = new ClassNamePicker(
                    "Choose a class",
                    baseClass,
                    scriptManager.resources.registry.getClassNames(baseClass),
                    ClassNameBox.this.textBox.getText()) {

                    @Override
                    public void pick( String value )
                    {
                        ClassNameBox.this.textBox.setText(value);
                    }

                };
                picker.show();
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

        this.value.name = this.textBox.getText();
        this.textBox.addStyle("error", !isValid());

        boolean isValidScript = this.scriptManager.isValidScript(this.value);
        this.editButtonLabel.setText(isValidScript ? "Edit" : "Create");

        this.editButton.setVisible(this.value.isScript());
        this.reloadButton.setVisible(isValidScript);

        if (isValidScript) {

            try {
                this.scriptManager.loadScript(getClassName());
            } catch (ScriptException e) {
                this.scriptManager.resources.errorLog.log(e.getMessage());
                this.textBox.addStyle("error");
                ScriptLanguage language = this.scriptManager.findLanguage(getClassName());
                this.errorText.setText(language.simpleMessage(e, false));
                this.errorText.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }
    }

    public boolean isValid()
    {
        return this.value.isValid(this.scriptManager);
    }

    private void reload()
    {
        fireChangeEvent();
    }

    public Set<String> getKnownNames( Resources resources )
    {
        return resources.registry.getClassNames(this.baseClass);
    }

    public String getBaseName()
    {
        if (this.baseClass == Role.class) {
            return "Role";
        } else if (this.baseClass == SceneDirector.class) {
            return "SceneDirector";
        } else if (this.baseClass == Director.class) {
            return "Director";
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

    public void setClassName( ClassName value )
    {
        this.value = value;
        this.textBox.setText(value.name);
    }

    public void addChangeListener( ComponentChangeListener ccl )
    {
        this.textBox.addChangeListener(ccl);
    }

    public void removeChangeListener( ComponentChangeListener ccl )
    {
        this.textBox.removeChangeListener(ccl);
    }

    public void fireChangeEvent()
    {
        this.textBox.fireChangeEvent();
    }

}
