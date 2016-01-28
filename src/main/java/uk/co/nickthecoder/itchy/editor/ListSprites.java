package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Sprite;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListSprites extends ListSubjects<Sprite>
{
    private SpriteSheet spriteSheet;
    
    public ListSprites(Resources resources, SpriteSheet spriteSheet)
    {
        super(resources);
        this.spriteSheet = spriteSheet;
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn nameColumn = new TableModelColumn("Name", 0, 200);
        nameColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn xColumn = new TableModelColumn("X", 1, 50);
        xColumn.rowComparator = new SingleColumnRowComparator<Integer>(1);

        TableModelColumn yColumn = new TableModelColumn("Y", 2, 70);
        yColumn.rowComparator = new SingleColumnRowComparator<Integer>(2);

        TableModelColumn widthColumn = new TableModelColumn("Width", 3, 70);
        widthColumn.rowComparator = new SingleColumnRowComparator<Integer>(3);

        TableModelColumn heightColumn = new TableModelColumn("Height", 4, 70);
        heightColumn.rowComparator = new SingleColumnRowComparator<Integer>(4);

        TableModelColumn previewColumn = new TableModelColumn("Sprite", 0, 140)
        {
            public void addPlainCell(Container container, final TableModelRow r)
            {
                @SuppressWarnings("unchecked")
                ReflectionTableModelRow<Sprite> row = (ReflectionTableModelRow<Sprite>) r;
                Sprite sprite = row.getData();
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
        columns.add(widthColumn);
        columns.add(heightColumn);
        columns.add(previewColumn);

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();
        for (Sprite sprite : this.spriteSheet.getSprites()) {
            String[] attributeNames = { "name", "x", "y", "width", "height" };
            TableModelRow row = new ReflectionTableModelRow<Sprite>(sprite, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void addOrEdit(Sprite subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new Sprite(this.spriteSheet, "new");
            isNew = true;
        }
        
        EditSprite edit = new EditSprite( this.resources, this, this.spriteSheet, subject, isNew );
        edit.show();             
    }

    @Override
    protected void remove(Sprite subject)
    {
        this.resources.removePose(subject.name);
        this.spriteSheet.removeSprite(subject);
    }

}
