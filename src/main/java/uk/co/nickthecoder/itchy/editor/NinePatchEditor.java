/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.GraphicsContext;
import uk.co.nickthecoder.itchy.NinePatchResource;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class NinePatchEditor extends SubEditor<NinePatchResource>
{
    private static final HashMap<String, NinePatch.Middle> NINE_PATCH_NAMES_HASH = new HashMap<String, NinePatch.Middle>();

    static {
        NINE_PATCH_NAMES_HASH.put("Fill", NinePatch.Middle.fill);
        NINE_PATCH_NAMES_HASH.put("Empty", NinePatch.Middle.empty);
        NINE_PATCH_NAMES_HASH.put("Tile", NinePatch.Middle.tile);
    }

    private ExplodedImage explodedImage;

    public NinePatchEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn imageColumn = new TableModelColumn("Image", 2, 200) {
            @Override
            public AbstractComponent createCell( TableModelRow row )
            {
                return new ImageComponent((Surface) (row.getData(this.index)));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(imageColumn);

        TableModel model = this.createTableModel();
        Table table = new Table(model, columns);

        return table;
    }

    @Override
    protected TableModel createTableModel()
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

    @Override
    protected void createForm()
    {
        super.createForm();

        ComponentChangeListener changeListener = new ComponentChangeListener() {
            @Override
            public void changed()
            {
                NinePatchEditor.this.explodedImage.invalidate();
            }
        };
        ((IntegerBox) this.form.getComponent("marginTop")).addChangeListener(changeListener);
        ((IntegerBox) this.form.getComponent("marginRight")).addChangeListener(changeListener);
        ((IntegerBox) this.form.getComponent("marginBottom")).addChangeListener(changeListener);
        ((IntegerBox) this.form.getComponent("marginLeft")).addChangeListener(changeListener);

        this.explodedImage = new ExplodedImage();
        this.explodedImage.addStyle("checkered");
        this.form.grid.addRow(new Label("Image"), this.addOptionalScrollbars(this.explodedImage, 500, 130));
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
            if (this.editor.resources.getNinePatchResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addNinePatch(this.currentResource);
        }
    }

    @Override
    protected void remove( NinePatchResource ninePatchResource )
    {
        this.editor.resources.removeNinePatch(ninePatchResource.getName());
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
        this.openDialog.setDirectory(getImageDirectory());
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
                NinePatch ninePatch = new NinePatch(new Surface(this.editor.resources.resolveFilename(filename)), 0, 0, 0, 0);
                this.edit(new NinePatchResource(this.editor.resources, name, filename, ninePatch), true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
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
    
    public class ExplodedImage extends AbstractComponent
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

        @Override
        public void onMouseDown( MouseButtonEvent mbe )
        {
            if (mbe.button == 1) {
                this.backgroundIndex++;
                if (this.backgroundIndex >= this.backgrounds.length) {
                    this.backgroundIndex = 0;
                }
                this.invalidate();
                mbe.stopPropagation();
            }
            super.onMouseDown(mbe);
        }

        private int getMargin( String name )
        {
            try {
                return ((IntegerBox) NinePatchEditor.this.form.getComponent(name)).getValue();
            } catch (Exception e) {
                return 0;
            }
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

            int top = getMargin("marginTop");
            int right = getMargin("marginRight");
            int bottom = getMargin("marginBottom");
            int left = getMargin("marginLeft");

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

    @Override
    protected List<AbstractProperty<NinePatchResource, ?>> getProperties()
    {
        return NinePatchResource.properties;
    }

}
