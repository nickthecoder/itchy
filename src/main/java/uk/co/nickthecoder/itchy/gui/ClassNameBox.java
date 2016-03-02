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
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.script.ScriptManager;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ClassNameBox extends PlainContainer
{
    private ScriptManager scriptManager;

    private TextBox textBox;

    private Button editButton;

    private Button reloadButton;

    private Label editButtonLabel;

    private ClassName value;

    private Class<?> baseClass;

    public ClassNameBox( final ScriptManager scriptManager, final ClassName className, final Class<?> baseClass )
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

        Button pick = new Button("...");
        main.addChild(pick);

        this.editButtonLabel = new Label("Edit");
        this.editButton = new Button(this.editButtonLabel);
        main.addChild(this.editButton);

        this.reloadButton = new Button("Reload");
        main.addChild(this.reloadButton);

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

        this.addValidator(new ComponentValidator() {

            @Override
            public boolean isValid()
            {
                ClassName temp = new ClassName(className.baseClass, textBox.getText());
                return temp.isValid(Itchy.getGame().scriptManager);
            }
            
        });
        update();
    }

    public boolean isValid()
    {
        return !this.textBox.hasStyle("error");
    }
    
    private void update()
    {
        this.addStyle("error", this.textBox.hasStyle("error"));
        
        this.value.name = this.textBox.getText();

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
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }
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
                fireChangeEvent();
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
    
    public void addValidator( ComponentValidator validator )
    {
        this.textBox.addValidator(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.textBox.removeValidator(validator);
    }

    public void fireChangeEvent()
    {
        this.textBox.fireChangeEvent();
    }

}
