package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.FilePoseResource;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.MessageDialog;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.ThumbnailedPickerButton;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ListPoses extends ListFileSubjects<FilePoseResource>
{
    private PickerButton<Filter> filterPickerButton;

    public ListPoses(Resources resources)
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
            public boolean accept(PoseResource pr)
            {
                return pr instanceof FilePoseResource;
            }

            @Override
            public Surface getThumbnail()
            {
                return null;
            }
        };
        Filter shared = new Filter()
        {
            @Override
            public boolean accept(PoseResource pr)
            {
                if (pr instanceof FilePoseResource) {
                    return ((FilePoseResource) pr).shared;
                }
                return false;
            }

            @Override
            public Surface getThumbnail()
            {
                return null;
            }
        };
        filterMap.put(" * All * ", all);
        filterMap.put(" * Shared * ", shared);
        for (String name : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(name);
            Filter filter = new CostumeFilter(costume);
            filterMap.put(costume.getName(), filter);
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

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String poseName : this.resources.poseNames()) {
            PoseResource poseResource = this.resources.getPoseResource(poseName);
            if (poseResource instanceof FilePoseResource) {
                if (this.filterPickerButton.getValue().accept(poseResource)) {
                    String[] attributeNames = { "name", "filename", "thumbnail" };
                    TableModelRow row = new ReflectionTableModelRow<PoseResource>(poseResource, attributeNames);
                    model.addRow(row);
                }
            }
        }
        return model;
    }


    protected File getDirectory()
    {
        return resources.getImagesDirectory();
    }
    
    @Override
    protected void add(String name, File relativeFile)
        throws JameException
    {
        FilePoseResource fpr = new FilePoseResource(name, relativeFile);
        EditPose edit = new EditPose(resources, ListPoses.this, fpr, true);
        edit.show();
    }
    
    protected void edit(FilePoseResource subject)
    {
        EditPose edit = new EditPose(this.resources, this, subject, false);
        edit.show();
    }

    @Override
    protected void remove(FilePoseResource subject)
    {
        String usedBy = this.resources.used( subject );
        if (usedBy != null) {
            MessageDialog message = new MessageDialog("Cannot Remove", "This pose is being used by : \n\n" + usedBy );
            message.show();
        } else {
            this.resources.removePose(subject.name);
        }
    }

    interface Filter extends Thumbnailed
    {
        boolean accept(PoseResource pr);

    }

    class CostumeFilter implements Filter
    {
        private Costume costume;

        CostumeFilter(Costume costume)
        {
            this.costume = costume;
        }

        @Override
        public boolean accept(PoseResource poseResource)
        {
            Costume cost = costume;
            while (cost != null) {
                for (String eventName : cost.getPoseNames()) {
                    for (PoseResource other : cost.getPoseChoices(eventName)) {
                        if (poseResource == other) {
                            return true;
                        }
                    }
                }
                cost = cost.getExtendedFrom();
            }
            return false;
        }

        @Override
        public Surface getThumbnail()
        {
            return this.costume.getThumbnail();
        }
    }

}
