/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Sprite;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.Scroll;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SimpleTableModelRow;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class SpriteSheetsEditor extends SubEditor<SpriteSheet>
{
    public SpriteSheetsEditor(Editor editor)
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        TableModelColumn image = new TableModelColumn("Image", 2, 100)
        {
            @Override
            public AbstractComponent createCell(TableModelRow row)
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
        for (String spriteSheetName : this.editor.resources.spriteSheetNames()) {
            SpriteSheet spriteSheet = this.editor.resources.getSpriteSheet(spriteSheetName);
            String[] attributeNames = { "name", "filename", "thumbnail" };
            TableModelRow row = new ReflectionTableModelRow<SpriteSheet>(spriteSheet, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    Table spritesTable;
    TableModel spritesTableModel;

    @Override
    protected Component createForm()
    {
        super.createForm();

        Notebook notebook = new Notebook();
        Container spritesPage = new PlainContainer();

        notebook.addPage("Details", this.form.container);
        notebook.addPage("Sprites", spritesPage);

        spritesPage.setLayout(new VerticalLayout());
        PlainContainer spritesTableSection = new PlainContainer();

        this.spritesTable = this.createSpritesTable();
        this.spritesTable.addTableListener(new AbstractTableListener()
        {
            @Override
            public void onRowPicked(TableRow tableRow)
            {
                SpriteSheetsEditor.this.onEditSprite();
            }
        });

        this.spritesTable.sort(0);
        spritesTableSection.addChild(this.spritesTable);
        spritesPage.addChild(spritesTableSection);
        PlainContainer eventsTableButtons = new PlainContainer();
        eventsTableButtons.setLayout(new VerticalLayout());
        eventsTableButtons.setYAlignment(0.5f);
        spritesTableSection.setFill(true, true);
        eventsTableButtons.addStyle("buttonColumn");

        spritesTableSection.addChild(eventsTableButtons);

        GuiButton edit = new GuiButton("Edit");
        edit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                SpriteSheetsEditor.this.onEditSprite();
            }
        });
        eventsTableButtons.addChild(edit);

        GuiButton add = new GuiButton("Add");
        add.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                SpriteSheetsEditor.this.onAddSprite();
            }
        });
        eventsTableButtons.addChild(add);

        GuiButton remove = new GuiButton("Remove");
        remove.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                SpriteSheetsEditor.this.onRemoveSprite();
            }
        });
        eventsTableButtons.addChild(remove);

        return notebook;
    }

    private Table createSpritesTable()
    {
        TableModelColumn nameColumn = new TableModelColumn("Name", 0, 200);
        nameColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn xColumn = new TableModelColumn("X", 1, 50);
        xColumn.rowComparator = new SingleColumnRowComparator<Integer>(0);

        TableModelColumn yColumn = new TableModelColumn("Y", 2, 50);
        yColumn.rowComparator = new SingleColumnRowComparator<Integer>(0);

        TableModelColumn previewColumn = new TableModelColumn("Sprite", 3, 140)
        {
            public void addPlainCell(Container container, final TableModelRow row)
            {
                final Sprite sprite = (Sprite) row.getData(3);
                container.addChild(new ImageComponent(sprite.getThumbnail()));
            }

            @Override
            public AbstractComponent createCell(TableModelRow row)
            {
                PlainContainer container = new PlainContainer();
                this.addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent(Component component, TableModelRow row)
            {
                Container container = (Container) component;
                container.clear();
                this.addPlainCell(container, row);
            };
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(nameColumn);
        columns.add(xColumn);
        columns.add(yColumn);
        columns.add(previewColumn);

        this.spritesTableModel = this.createSpritesTableModel();
        return new Table(this.spritesTableModel, columns);
    }

    private SimpleTableModel createSpritesTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (Sprite sprite : this.currentResource.getSprites()) {
            SimpleTableModelRow row = new SimpleTableModelRow();
            row.add(sprite.getName());
            row.add(sprite.getX());
            row.add(sprite.getY());
            row.add(sprite);

            model.addRow(row);
        }

        return model;
    }

    private void rebuildSpritesTable()
    {
        this.spritesTableModel = this.createSpritesTableModel();
        this.spritesTable.setTableModel(this.spritesTableModel);
    }

    private void selectSpritesTableRow(Sprite sprite)
    {
        for (int i = 0; i < this.spritesTableModel.getRowCount(); i++) {
            TableModelRow row = this.spritesTableModel.getRow(i);
            if (row.getData(3) == sprite) {
                this.spritesTable.selectRow(row);
                return;
            }
        }
    }

    private void onAddSprite()
    {
        Sprite sprite = new Sprite(this.currentResource, "new");

        editSprite(sprite);
    }

    private void onRemoveSprite()
    {
        // TODO onRemoveSprite
        System.out.println("Remove Sprite");
    }

    private void onEditSprite()
    {
        TableModelRow row = this.spritesTable.getCurrentTableModelRow();
        if (row == null) {
            return;
        }

        Sprite sprite = (Sprite) row.getData(3);

        if (row != null) {
            editSprite(sprite);
        }
    }

    private Sprite edittingSprite;
    private PropertiesForm<Sprite> spriteForm;

    private Component createScrolledImage(ClickableContainer imageContainer)
    {
        imageContainer.setMinimumWidth(300);
        imageContainer.setMinimumHeight(300);
        
        Scroll scroll = new Scroll(imageContainer);
        scroll.setNaturalHeight(301);
        scroll.setNaturalWidth(301);
        return scroll;
    }

    private void editSprite(final Sprite sprite)
    {
        this.edittingSprite = sprite;

        final Window window = new Window("Edit Event");
        window.clientArea.setFill(true, true);
        window.clientArea.setLayout(new VerticalLayout());

        Container across = new PlainContainer();
        across.setLayout(new HorizontalLayout());
        window.clientArea.addChild(across);

        Notebook notebook = new Notebook();
        this.spriteForm = new PropertiesForm<Sprite>(sprite, sprite.getProperties());
        this.spriteForm.autoUpdate = true;
        across.addChild(this.spriteForm.createForm());
        across.addChild(notebook);

        final PlainContainer previewPage = new PlainContainer();
        final ClickableContainer previewClick = new ClickableContainer()
        {
            @Override
            public void onClick(MouseButtonEvent e)
            {
                ((IntegerBox) spriteForm.getComponent("offsetX")).setValue(e.x);
                ((IntegerBox) spriteForm.getComponent("offsetY")).setValue(e.y);
            }
        };
        final ImageComponent previewImg = new ImageComponent(sprite.pose.getSurface());
        previewImg.addStyle("checkered");
        previewClick.addChild(previewImg);

        previewPage.addChild(createScrolledImage(previewClick));
        notebook.addPage("Preview", previewPage);

        PlainContainer spriteSheetPage = new PlainContainer();
        ClickableContainer spriteSheetClick = new ClickableContainer()
        {
            @Override
            public void onClick(MouseButtonEvent e)
            {
                ((IntegerBox) spriteForm.getComponent("x")).setValue(e.x);
                ((IntegerBox) spriteForm.getComponent("y")).setValue(e.y);
            }
        };
        ImageComponent img = new ImageComponent(this.currentResource.getSurface());
        img.addStyle("checkered");
        spriteSheetClick.addChild(img);

        spriteSheetPage.addChild(createScrolledImage(spriteSheetClick));
        notebook.addPage("Sprite Sheet", spriteSheetPage);

        // Whenever the x,y,width or height change, update the preview image.
        for (String key : new String[] { "x", "y", "width", "height" }) {
            ((IntegerBox) this.spriteForm.getComponent(key)).addChangeListener(new ComponentChangeListener()
            {
                @Override
                public void changed()
                {
                    previewImg.setImage(sprite.pose.getSurface());
                    previewClick.getParent().getParent().invalidate();
                    previewClick.getParent().getParent().forceLayout();
                }
            });
        }

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        GuiButton ok = new GuiButton(new Label("Ok"));
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                SpriteSheetsEditor.this.onEditSpritesOk();
                window.hide();
            }
        });
        GuiButton cancel = new GuiButton(new Label("Cancel"));
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                spriteForm.revert();
                window.hide();
            }
        });
        buttons.addChild(ok);
        buttons.addChild(cancel);

        window.clientArea.addChild(buttons);

        window.show();
    }

    private void onEditSpritesOk()
    {
        this.spriteForm.update();

        if (this.currentResource.getSprites().contains(this.edittingSprite)) {
        } else {
            this.currentResource.addSprite(this.edittingSprite);
        }
        this.rebuildSpritesTable();
        this.selectSpritesTableRow(this.edittingSprite);
        this.edittingSprite = null;
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
            if (this.editor.resources.getSpriteSheet(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addSpriteSheet(this.currentResource);
        }

    }

    @Override
    protected void remove(SpriteSheet spriteSheet)
    {
        StringList usedBy = new StringList();

        for (PoseResource poseResource : spriteSheet.getSprites()) {

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

        }

        if (usedBy.isEmpty()) {
            this.editor.resources.removeSpriteSheet(spriteSheet.getName());
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }
    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog()
        {
            @Override
            public void onChosen(File file)
            {
                SpriteSheetsEditor.this.onAdd(file);
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

    public void onAdd(File file)
    {
        if (file == null) {
            this.openDialog.hide();

        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                SpriteSheet spriteSheet = new SpriteSheet(this.getResources(), name);
                spriteSheet.setFilename(filename);
                this.openDialog.hide();
                this.edit(spriteSheet, true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    @Override
    protected List<Property<SpriteSheet, ?>> getProperties()
    {
        return this.currentResource.getProperties();
    }

}
