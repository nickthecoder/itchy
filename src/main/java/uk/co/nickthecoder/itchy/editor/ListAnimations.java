package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.ThumbnailedPickerButton;
import uk.co.nickthecoder.jame.Surface;

public class ListAnimations extends ListSubjects<AnimationResource>
{
    private PickerButton<Filter> filterPickerButton;

    
    public ListAnimations(Resources resources)
    {
        super(resources);
    }


    @Override
    public void addHeader(Container page)
    {
        HashMap<String, Filter> filterMap = new HashMap<String, Filter>();
        Filter all = new Filter()
        {
            @Override
            public boolean accept(AnimationResource ar)
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
        
        for (String name : this.resources.costumeNames()) {
            CostumeResource cr = this.resources.getCostumeResource(name);
            Filter filter = new CostumeFilter(cr);
            filterMap.put(cr.getName(), filter);
        }

        this.filterPickerButton = new ThumbnailedPickerButton<Filter>("Filter", all, filterMap);
        this.filterPickerButton.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                rebuildTable();
            }
        });

        page.addChild(this.filterPickerButton);

    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        
        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String animationName : this.resources.animationNames()) {
            AnimationResource animationResource = this.resources.getAnimationResource(animationName);
            if (this.filterPickerButton.getValue().accept(animationResource)) {
                String[] attributeNames = { "name" };
                TableModelRow row = new ReflectionTableModelRow<AnimationResource>(animationResource, attributeNames);
                model.addRow(row);
            }
        }
        return model;
    }

    @Override
    protected void edit(AnimationResource subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new AnimationResource();
            isNew = true;
        }
        
        EditAnimation edit = new EditAnimation( this.resources, this, subject, isNew );
        edit.show();        
    }

    @Override
    protected void remove(AnimationResource subject)
    {
        this.resources.removeAnimation(subject.getName());
    }


    interface Filter extends Thumbnailed
    {
        boolean accept(AnimationResource pr);
    }

    class CostumeFilter implements Filter
    {
        CostumeResource costumeResource;

        CostumeFilter(CostumeResource costumeResource)
        {
            this.costumeResource = costumeResource;
        }

        @Override
        public boolean accept(AnimationResource animationResource)
        {
            Costume costume = this.costumeResource.getCostume();
            while (costume != null) {
                for (String eventName : costume.getAnimationNames()) {
                    for (AnimationResource other : costume.getAnimationChoices(eventName)) {
                        if (animationResource == other) {
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
