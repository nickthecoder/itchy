/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.ThumbnailedPickerButton;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class SoundsEditor extends SubEditor<SoundResource>
{
    private PickerButton<Filter> filterPickerButton;

    public SoundsEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public void addHeader( Container page )
    {
        HashMap<String, Filter> filterMap = new HashMap<String, Filter>();
        Filter all = new Filter() {
            @Override
            public boolean accept( SoundResource ar )
            {
                return true;
            }

            @Override
            public Surface getThumbnail()
            {
                return null;
            }
        };
        filterMap.put(" * All * ", all);

        for (String name : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(name);
            Filter filter = new CostumeFilter(cr);
            filterMap.put(cr.getName(), filter);
        }

        this.filterPickerButton = new ThumbnailedPickerButton<Filter>("Filter", all, filterMap);
        this.filterPickerButton.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                rebuildTable();
            }
        });

        page.addChild(this.filterPickerButton);

    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn play = new TableModelColumn("Play", 1, 100) {
            @Override
            public AbstractComponent createCell( final TableModelRow row )
            {
                PlainContainer container = new PlainContainer();

                GuiButton button = new GuiButton("Play");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void action()
                    {
                        ReflectionTableModelRow<?> rrow = (ReflectionTableModelRow<?>) row;
                        ((SoundResource) rrow.getData()).getSound().play();
                    };
                });
                container.addChild(button);
                return container;

            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(play);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String soundName : this.editor.resources.soundNames()) {
            SoundResource soundResource = this.editor.resources.getSoundResource(soundName);
            if (this.filterPickerButton.getValue().accept(soundResource)) {
                String[] attributeNames = { "name", "filename" };
                TableModelRow row = new ReflectionTableModelRow<SoundResource>(soundResource, attributeNames);
                model.addRow(row);
            }
        }
        return model;
    }

    @Override
    protected void update() throws MessageException
    {
        FilenameComponent filenameComponent = (FilenameComponent) this.form.getComponent("file");
        File file = filenameComponent.getValue();
        TextBox name = (TextBox) this.form.getComponent("name");

        boolean exists = this.editor.resources.fileExists(file.getPath());
        if (!exists) {
            throw new MessageException("Filename not found");
        }
        

        if (!this.editor.resources.fileIsWithin(file)) {
            File newFile = new File(getSoundsDirectory(), file.getName());
            if (newFile.exists()) {
                throw new MessageException("File is outside of this game's resource directory.");
            }
            try {
                Util.copyFile(this.editor.resources.resolveFile(file), newFile);
                filenameComponent.setText(this.editor.resources.makeRelativeFilename(newFile));
            } catch (Exception e) {
                throw new MessageException("Failed to copy image into the resources directory");
            }
        }
        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getSoundResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addSound(this.currentResource);
        }
    }

    @Override
    protected void remove( SoundResource soundResource )
    {
        StringList usedBy = new StringList();

        for (String costumeName : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(costumeName);
            Costume costume = cr.getCostume();
            for (String resourceName : costume.getSoundNames()) {
                for (ManagedSound managedSound : costume.getSoundChoices(resourceName)) {
                    if (managedSound.soundResource == soundResource) {
                        usedBy.add(costumeName);
                    }
                }
            }
        }
        if (usedBy.isEmpty()) {
            this.editor.resources.removeSound(soundResource.getName());
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }

    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                SoundsEditor.this.onAdd(file);
            }
        };
        this.openDialog.setDirectory(getSoundsDirectory());
        this.openDialog.show();
    }

    public void onAdd( File file )
    {
        if (file == null) {
            this.openDialog.hide();
        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                this.openDialog.hide();
                this.edit(new SoundResource(this.editor.resources, name, filename), true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    public File getSoundsDirectory()
    {
        File dir = this.editor.resources.getDirectory();
        File soundsDir = new File(dir, "sounds");
        if (soundsDir.exists()) {
            return soundsDir;
        } else {
            return dir;
        }
    }

    @Override
    protected List<Property<SoundResource, ?>> getProperties()
    {
        return this.currentResource.getProperties();
    }

    interface Filter extends Thumbnailed
    {
        boolean accept( SoundResource pr );
    }

    class CostumeFilter implements Filter
    {
        CostumeResource costumeResource;

        CostumeFilter( CostumeResource costumeResource )
        {
            this.costumeResource = costumeResource;
        }

        @Override
        public boolean accept( SoundResource soundResource )
        {
            Costume costume = this.costumeResource.getCostume();
            while (costume != null) {
                for (String eventName : costume.getSoundNames()) {
                    for (ManagedSound ms : costume.getSoundChoices(eventName)) {
                        SoundResource other = ms.soundResource;
                        if (soundResource == other) {
                            return true;
                        }
                    }
                }
                costume = costume.getExtendedFrom();
            }
            return false;
        }

        @Override
        public Surface getThumbnail()
        {
            return this.costumeResource.getThumbnail();
        }
    }

}
