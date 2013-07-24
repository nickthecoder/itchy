/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.NinePatchResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
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
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class NinePatchEditor extends SubEditor
{
    private static final HashMap<String, NinePatch.Middle> NINE_PATCH_NAMES_HASH = new HashMap<String, NinePatch.Middle>();

    static {
        NINE_PATCH_NAMES_HASH.put("Fill", NinePatch.Middle.fill);
        NINE_PATCH_NAMES_HASH.put("Empty", NinePatch.Middle.empty);
        NINE_PATCH_NAMES_HASH.put("Tile", NinePatch.Middle.tile);
    }

    private TextBox txtName;

    private FilenameComponent txtFilename;

    private IntegerBox txtTop;
    private IntegerBox txtRight;
    private IntegerBox txtBottom;
    private IntegerBox txtLeft;

    private ExplodedImage explodedImage;

    private NinePatchResource currentResource;

    private PickerButton<NinePatch.Middle> pickMiddle;

    public NinePatchEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Container createPage()
    {
        Container form = super.createPage();
        form.setFill(true, true);

        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn imageColumn = new TableModelColumn("Image", 2, 200) {
            @Override
            public Component createCell( TableModelRow row )
            {
                return new ImageComponent((Surface) (row.getData(this.index)));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(imageColumn);

        TableModel model = this.createTableModel();
        this.table = new Table(model, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                NinePatchEditor.this.onEdit();
            }
        });

        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        form.addChild(this.table);
        this.table.sort(0);

        form.addChild(this.createListButtons());

        return form;
    }

    private TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String soundName : this.editor.resources.ninePatchNames()) {
            NinePatchResource resource = this.editor.resources.getNinePatchResource(soundName);
            String[] attributeNames = { "name", "filename", "thumbnail" };
            TableModelRow row = new ReflectionTableModelRow<NinePatchResource>(resource,
                    attributeNames);
            model.addRow(row);
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
        this.currentResource = (NinePatchResource) resource;
        NinePatch ninePatch = this.currentResource.ninePatch;

        this.txtName = new TextBox(this.currentResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        this.txtFilename = new FilenameComponent(this.editor.resources,
                this.currentResource.filename);
        grid.addRow(new Label("Filename"), this.txtFilename);

        ComponentChangeListener changeListener = new ComponentChangeListener() {
            @Override
            public void changed()
            {
                NinePatchEditor.this.explodedImage.invalidate();
            }
        };

        this.txtTop = new IntegerBox(ninePatch.getMarginTop());
        this.txtTop.addChangeListener(changeListener);
        grid.addRow(new Label("Top"), this.txtTop);

        this.txtRight = new IntegerBox(ninePatch.getMarginRight());
        this.txtRight.addChangeListener(changeListener);
        grid.addRow(new Label("Right"), this.txtRight);

        this.txtBottom = new IntegerBox(ninePatch.getMarginBottom());
        this.txtBottom.addChangeListener(changeListener);
        grid.addRow(new Label("Bottom"), this.txtBottom);

        this.txtLeft = new IntegerBox(ninePatch.getMarginLeft());
        this.txtLeft.addChangeListener(changeListener);
        grid.addRow(new Label("Left"), this.txtLeft);

        this.pickMiddle = new PickerButton<NinePatch.Middle>("Middle", ninePatch.middle,
                NINE_PATCH_NAMES_HASH);
        grid.addRow(new Label("Middle"), this.pickMiddle);

        this.explodedImage = new ExplodedImage();
        this.explodedImage.addStyle("checkered");
        grid.addRow(new Label("Image"), this.addOptionalScrollbars(this.explodedImage, 500, 130));
    }

    @Override
    protected void onOk()
    {
        boolean exists = this.editor.resources.fileExists(this.txtFilename.getText());
        if (!exists) {
            this.setMessage("Filename not found");
            return;
        }
        if (this.adding || (!this.txtName.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getNinePatchResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        this.currentResource.rename(this.txtName.getText());
        this.currentResource.filename = this.txtFilename.getText();

        try {
            this.currentResource.ninePatch.marginTop = Integer.parseInt(this.txtTop.getText());
        } catch (NumberFormatException e) {
            this.setMessage("Top must be an integer");
            return;
        }

        try {
            this.currentResource.ninePatch.marginRight = Integer.parseInt(this.txtRight.getText());
        } catch (NumberFormatException e) {
            this.setMessage("Right must be an integer");
            return;
        }

        try {
            this.currentResource.ninePatch.marginBottom = Integer
                    .parseInt(this.txtBottom.getText());
        } catch (NumberFormatException e) {
            this.setMessage("Bottom must be an integer");
            return;
        }

        try {
            this.currentResource.ninePatch.marginLeft = Integer.parseInt(this.txtLeft.getText());
        } catch (NumberFormatException e) {
            this.setMessage("Left must be an integer");
            return;
        }

        this.currentResource.ninePatch.middle = this.pickMiddle.getValue();

        if (this.adding) {
            this.editor.resources.addNinePatch(this.currentResource);
            this.rebuildTable();
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        Itchy.singleton.getGame().hideWindow(this.editWindow);
    }

    @Override
    protected void remove( Object resource )
    {
        NinePatchResource ninePatchResource = (NinePatchResource) resource;

        this.editor.resources.removeNinePatch(ninePatchResource.getName());
        this.rebuildTable();

    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                NinePatchEditor.this.onAdd(file);
            }
        };
        this.openDialog.setDirectory(this.editor.resources.getDirectory());
        Itchy.singleton.getGame().showWindow(this.openDialog);
    }

    public void onAdd( File file )
    {
        if (file == null) {
            Itchy.singleton.getGame().hideWindow(this.openDialog);
        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                NinePatch ninePatch = new NinePatch(new Surface(
                        this.editor.resources.resolveFilename(filename)), 0, 0, 0, 0);
                this.currentResource = new NinePatchResource(this.editor.resources, name, filename,
                        ninePatch);
                this.adding = true;
                Itchy.singleton.getGame().hideWindow(this.openDialog);
                this.showDetails(this.currentResource);
            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    public class ExplodedImage extends Component
    {
        private final Surface surface;

        private final int spacing = 10;

        private int backgroundIndex = 0;

        private final RGBA[] backgrounds = new RGBA[] { null, new RGBA(0, 0, 0),
            new RGBA(255, 255, 255), new RGBA(128, 128, 128) };

        public ExplodedImage()
        {
            this.surface = NinePatchEditor.this.currentResource.ninePatch.getSurface();
        }

        @Override
        public int getNaturalWidth()
        {
            return this.surface.getWidth() + this.spacing * 2;
        }

        @Override
        public int getNaturalHeight()
        {
            return this.surface.getHeight() + this.spacing * 2;
        }

        private int getInteger( IntegerBox box )
        {
            try {
                return box.getValue();
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public boolean mouseDown( MouseButtonEvent mbe )
        {
            if (mbe.button == 1) {
                this.backgroundIndex++;
                if (this.backgroundIndex >= this.backgrounds.length) {
                    this.backgroundIndex = 0;
                }
                this.invalidate();
                return true;
            }
            return super.mouseDown(mbe);
        }

        @Override
        public void render( GraphicsContext gc )
        {
            RGBA background = this.backgrounds[this.backgroundIndex];
            if (background == null) {
                this.renderBackground(gc);
            } else {
                Rect whole = new Rect(0, 0, this.surface.getWidth() + this.spacing * 2,
                        this.surface.getHeight() + this.spacing * 2);
                gc.fill(whole, background);
            }

            int top = this.getInteger(NinePatchEditor.this.txtTop);
            int right = this.getInteger(NinePatchEditor.this.txtRight);
            int bottom = this.getInteger(NinePatchEditor.this.txtBottom);
            int left = this.getInteger(NinePatchEditor.this.txtLeft);

            int width = this.surface.getWidth();
            int height = this.surface.getHeight();

            // Top left
            Rect srcRect = new Rect(0, 0, left, top);
            gc.blit(this.surface, srcRect, 0, 0, Surface.BlendMode.NONE);

            // Top edge
            srcRect = new Rect(left, 0, width - left - right, top);
            gc.blit(this.surface, srcRect, left + this.spacing, 0, Surface.BlendMode.NONE);

            // Top right
            srcRect = new Rect(width - right, 0, right, top);
            gc.blit(this.surface, srcRect, width - right + this.spacing * 2, 0,
                    Surface.BlendMode.NONE);

            // Left Edge
            srcRect = new Rect(0, top, left, height - top - bottom);
            gc.blit(this.surface, srcRect, 0, top + this.spacing, Surface.BlendMode.NONE);

            // Center
            srcRect = new Rect(left, top, width - left - right, height - top - bottom);
            gc.blit(this.surface, srcRect, left + this.spacing, top + this.spacing,
                    Surface.BlendMode.NONE);

            // Right Edge
            srcRect = new Rect(width - right, top, right, height - top - bottom);
            gc.blit(this.surface, srcRect, width - right + this.spacing * 2, top + this.spacing,
                    Surface.BlendMode.NONE);

            // Bottom Left
            srcRect = new Rect(0, height - bottom, left, bottom);
            gc.blit(this.surface, srcRect, 0, height - bottom + this.spacing * 2,
                    Surface.BlendMode.NONE);

            // Bottom edge
            srcRect = new Rect(left, height - bottom, width - left - right, bottom);
            gc.blit(this.surface, srcRect, left + this.spacing, height - bottom + this.spacing * 2,
                    Surface.BlendMode.NONE);

            // Bottom right
            srcRect = new Rect(width - right, height - bottom, right, bottom);
            gc.blit(this.surface, srcRect, width - right + this.spacing * 2, height - bottom +
                    this.spacing * 2, Surface.BlendMode.NONE);

        }
    }

}
