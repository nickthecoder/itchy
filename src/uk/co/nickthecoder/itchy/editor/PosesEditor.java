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
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
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
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class PosesEditor extends SubEditor<PoseResource>
{
    private PickerButton<Filter> filterPickerButton;

    public PosesEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public void addHeader( Container page )
    {
        HashMap<String, Filter> filterMap = new HashMap<String, Filter>();
        Filter all = new Filter() {
            @Override
            public boolean accept( PoseResource pr )
            {
                return true;
            }

            @Override
            public Surface getThumbnail()
            {
                return null;
            }
        };
        Filter shared = new Filter() {
            @Override
            public boolean accept( PoseResource pr )
            {
                return pr.shared;
            }

            @Override
            public Surface getThumbnail()
            {
                return null;
            }
        };
        filterMap.put(" * All * ", all);
        filterMap.put(" * Shared * ", shared);
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

        TableModelColumn image = new TableModelColumn("Image", 2, 100) {
            @Override
            public Component createCell( TableModelRow row )
            {
                return new ImageComponent((Surface) (row.getData(this.index)));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(image);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected SimpleTableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String poseName : this.editor.resources.poseNames()) {
            PoseResource poseResource = this.editor.resources.getPoseResource(poseName);
            if (this.filterPickerButton.getValue().accept(poseResource)) {
                String[] attributeNames = { "name", "filename", "thumbnail" };
                TableModelRow row = new ReflectionTableModelRow<PoseResource>(poseResource,
                    attributeNames);
                model.addRow(row);
            }
        }
        return model;
    }

    @Override
    protected void createForm()
    {
        super.createForm();

        GridLayout grid = this.form.grid;

        Container imageContainer = new ClickableContainer() {
            @Override
            public void onClick( MouseButtonEvent e )
            {
                Container parent = this.getParent();
                while (parent != null) {
                    parent = parent.getParent();
                }
                ((IntegerBox) PosesEditor.this.form.getComponent("offsetX")).setValue(e.x);
                ((IntegerBox) PosesEditor.this.form.getComponent("offsetY")).setValue(e.y);
            }
        };
        ImagePose pose = this.currentResource.pose;

        ImageComponent imgPose = new ImageComponent(pose.getSurface());
        imageContainer.addChild(imgPose);
        imgPose.addStyle("checkered");

        if (pose.getSurface().getHeight() > 130) {
            VerticalScroll scroll = new VerticalScroll(imageContainer);
            scroll.setNaturalHeight(130);
            grid.addRow("Image", scroll);

        } else {
            grid.addRow("Image", imageContainer);
        }
        grid.addRow("", new Label("(Click the image to set its offsets)"));

        grid.addRow("Size", new Label("" + pose.getSurface().getWidth() + "," + pose.getSurface().getHeight()));

    }

    @Override
    protected void update() throws MessageException
    {
        FilenameComponent filenameComponent = (FilenameComponent) this.form.getComponent("file");
        File file = filenameComponent.getValue();
        TextBox name = (TextBox) this.form.getComponent("name");

        boolean exists = this.editor.resources.fileExists(file.getPath());
        if (!exists) {
            throw new MessageException("File not found");
        }

        if (!this.editor.resources.fileIsWithin(file)) {
            File newFile = new File(getImageDirectory(), file.getName());
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
            if (this.editor.resources.getPoseResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addPose(this.currentResource);
        }
    }

    @Override
    protected void remove( PoseResource poseResource )
    {
        StringList usedBy = new StringList();

        for (String costumeName : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(costumeName);
            Costume costume = cr.getCostume();
            for (String resourceName : costume.getPoseNames()) {
                for (PoseResource resource : costume.getPoseChoices(resourceName)) {
                    if (resource == poseResource) {
                        usedBy.add(costumeName);
                    }
                }
            }
        }
        if (usedBy.isEmpty()) {
            this.editor.resources.removePose(poseResource.getName());
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
                PosesEditor.this.onAdd(file);
            }
        };
        this.openDialog.setDirectory(getImageDirectory());
        this.openDialog.show();
    }

    public File getImageDirectory()
    {
        File dir = this.editor.resources.getDirectory();
        File imageDir = new File(dir, "images");
        if (imageDir.exists()) {
            return imageDir;
        } else {
            return dir;
        }
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
                this.edit(new PoseResource(this.editor.resources, name, filename), true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    @Override
    protected List<AbstractProperty<PoseResource, ?>> getProperties()
    {
        return PoseResource.properties;
    }

    interface Filter extends Thumbnailed
    {
        boolean accept( PoseResource pr );

    }

    class CostumeFilter implements Filter
    {
        CostumeResource costumeResource;

        CostumeFilter( CostumeResource costumeResource )
        {
            this.costumeResource = costumeResource;
        }

        @Override
        public boolean accept( PoseResource poseResource )
        {
            Costume costume = this.costumeResource.getCostume();
            while (costume != null) {
                for (String eventName : costume.getPoseNames()) {
                    for (PoseResource other : costume.getPoseChoices(eventName)) {
                        if (poseResource == other) {
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
