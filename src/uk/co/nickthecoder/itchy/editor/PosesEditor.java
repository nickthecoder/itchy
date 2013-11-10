/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
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

    interface Filter
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
        public boolean accept( PoseResource pr )
        {
            for (String eventName : this.costumeResource.getCostume().getPoseNames()) {
                for (PoseResource other : this.costumeResource.getCostume().getPoseChoices(eventName)) {
                    if (pr == other) {
                        return true;
                    }
                }
            }
            return false;
        }
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
        };
        Filter shared = new Filter() {
            @Override
            public boolean accept( PoseResource pr )
            {
                return pr.shared;
            }
        };
        filterMap.put(" * All * ", all);
        filterMap.put(" * Shared * ", shared);
        for (String name : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(name);
            Filter filter = new CostumeFilter(cr);
            filterMap.put(cr.getName(), filter);
        }

        this.filterPickerButton = new PickerButton<Filter>("Filter", all, filterMap);
        this.filterPickerButton.addActionListener(new ActionListener() {
            @Override
            public void action()
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
        FilenameComponent filename = (FilenameComponent) this.form.getComponent("file");
        TextBox name = (TextBox) this.form.getComponent("name");

        boolean exists = this.editor.resources.fileExists(filename.getText());
        if (!exists) {
            throw new MessageException("Filename not found");
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
        this.editor.resources.removePose(poseResource.getName());
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
        this.openDialog.setDirectory(this.editor.resources.getDirectory());
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

}
