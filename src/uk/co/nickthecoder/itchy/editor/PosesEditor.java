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
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
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
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.itchy.gui.ActionListener;

public class PosesEditor extends SubEditor
{

    private TextBox txtName;

    private FilenameComponent txtFilename;

    private DoubleBox txtDirection;

    private IntegerBox txtOffsetX;

    private IntegerBox txtOffsetY;

    private ImageComponent imgPose;

    private PoseResource currentPoseResource;

    private SimpleTableModel tableModel;

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
    public Container createPage()
    {
        Container form = super.createPage();
        form.setFill(true, true);

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
        filterMap.put(" All ", all);
        filterMap.put(" Shared ", shared);
        for (String name : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(name);
            Filter filter = new CostumeFilter(cr);
            filterMap.put("Costume : " + cr.getName(), filter);
        }

        this.filterPickerButton = new PickerButton<Filter>("Filter", shared, filterMap);
        this.filterPickerButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                rebuildTable();
            }            
        });
        form.addChild(filterPickerButton);
        
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

        this.tableModel = this.createTableModel();
        this.table = new Table(this.tableModel, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                PosesEditor.this.onEdit();
            }
        });

        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        form.addChild(this.table);
        this.table.sort(0);

        form.addChild(this.createListButtons());

        return form;
    }

    private SimpleTableModel createTableModel()
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

    private void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentPoseResource = (PoseResource) resource;
        ImagePose pose = this.currentPoseResource.pose;

        this.txtName = new TextBox(this.currentPoseResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        this.txtFilename = new FilenameComponent(this.editor.resources,
            this.currentPoseResource.filename);
        grid.addRow(new Label("Filename"), this.txtFilename);

        this.txtDirection = new DoubleBox(pose.getDirection());
        grid.addRow(new Label("Direction"), this.txtDirection);

        this.txtOffsetX = new IntegerBox(pose.getOffsetX());
        grid.addRow(new Label("Offset X"), this.txtOffsetX);

        this.txtOffsetY = new IntegerBox(pose.getOffsetY());
        grid.addRow(new Label("Offset Y"), this.txtOffsetY);

        Container imageContainer = new ClickableContainer() {
            @Override
            public void onClick( MouseButtonEvent e )
            {
                Container parent = this.getParent();
                while (parent != null) {
                    parent = parent.getParent();
                }
                PosesEditor.this.txtOffsetX.setValue(e.x);
                PosesEditor.this.txtOffsetY.setValue(e.y);
            }
        };
        this.imgPose = new ImageComponent(pose.getSurface());
        imageContainer.addChild(this.imgPose);
        this.imgPose.addStyle("checkered");

        if (pose.getSurface().getHeight() > 130) {
            VerticalScroll scroll = new VerticalScroll(imageContainer);
            scroll.setNaturalHeight(130);
            grid.addRow(new Label("Image"), scroll);

        } else {
            grid.addRow(new Label("Image"), imageContainer);
        }
        grid.addRow(new Label(""), new Label("(Click the image to set its offsets)"));

        grid.addRow(new Label("Size"), new Label("" + pose.getSurface().getWidth() + "," +
            pose.getSurface().getHeight()));
    }

    @Override
    protected void onOk()
    {
        boolean exists = this.editor.resources.fileExists(this.txtFilename.getText());
        if (!exists) {
            this.setMessage("Filename not found");
            return;
        }

        if (this.adding || (!this.txtName.getText().equals(this.currentPoseResource.getName()))) {
            if (this.editor.resources.getPoseResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }
        this.currentPoseResource.setName(this.txtName.getText());
        this.currentPoseResource.filename = this.txtFilename.getText();

        try {
            this.currentPoseResource.pose.setDirection(this.txtDirection.getValue());
        } catch (Exception e) {
            this.setMessage("Direction must be a number");
            return;
        }

        try {
            this.currentPoseResource.pose.setOffsetX(Integer.parseInt(this.txtOffsetX.getText()));
        } catch (Exception e) {
            this.setMessage("Offset X must be an integer");
            return;
        }
        try {
            this.currentPoseResource.pose.setOffsetY(Integer.parseInt(this.txtOffsetY.getText()));
        } catch (Exception e) {
            this.setMessage("Offset Y must be an integer");
            return;
        }

        if (this.adding) {
            this.editor.resources.addPose(this.currentPoseResource);
            this.rebuildTable();
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        Itchy.getGame().hideWindow(this.editWindow);
    }

    @Override
    protected void remove( Object resource )
    {
        PoseResource poseResource = (PoseResource) resource;

        this.editor.resources.removePose(poseResource.getName());
        this.rebuildTable();

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
        Itchy.getGame().showWindow(this.openDialog);
    }

    public void onAdd( File file )
    {
        if (file == null) {
            Itchy.getGame().hideWindow(this.openDialog);
        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                this.currentPoseResource = new PoseResource(this.editor.resources, name, filename);
                this.adding = true;
                Itchy.getGame().hideWindow(this.openDialog);
                this.showDetails(this.currentPoseResource);
            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

}
