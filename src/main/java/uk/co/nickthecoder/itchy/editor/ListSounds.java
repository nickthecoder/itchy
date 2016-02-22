package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.MessageDialog;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.ThumbnailedPickerButton;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ListSounds extends ListFileSubjects<SoundResource>
{

    private PickerButton<Filter> filterPickerButton;

    public ListSounds(Resources resources)
    {
        super(resources);
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

        for (String name : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(name);
            Filter filter = new CostumeFilter(costume);
            filterMap.put(costume.getName(), filter);
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
    protected List<TableModelColumn> createTableColumns()
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

                Button button = new Button("Play");
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
        
        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String soundName : this.resources.soundNames()) {
            SoundResource soundResource = this.resources.getSound(soundName);
            if (this.filterPickerButton.getValue().accept(soundResource)) {
                String[] attributeNames = { "name", "file" };
                TableModelRow row = new ReflectionTableModelRow<SoundResource>(soundResource, attributeNames);
                model.addRow(row);
            }
        }
        return model;
    }
    
    
    protected File getDirectory()
    {
        return resources.getSoundsDirectory();
    }
    
    @Override
    protected void add(String name, File relativeFile)
        throws JameException
    {
        
        SoundResource newSubject = new SoundResource();
        newSubject.setName(name);;
        newSubject.setFile(relativeFile);
        EditSound edit = new EditSound( resources, ListSounds.this, newSubject, true );
        edit.show();
    }


    @Override
    protected void edit(SoundResource subject )
    {
        EditSound edit = new EditSound( this.resources, this, subject, false );
        edit.show();         
    }

    
    @Override
    protected void remove( SoundResource soundResource )
    {
        String usedBy = this.resources.used( soundResource );
        
        if (usedBy != null) {
            MessageDialog message = new MessageDialog("Cannot Remove", "This sprite is being used by : \n\n" + usedBy );
            message.show();
        } else {
            this.resources.removeSound(soundResource.getName());
        }
    }
    

    interface Filter extends Thumbnailed
    {
        boolean accept( SoundResource pr );
    }
    
    class CostumeFilter implements Filter
    {
        Costume costume;

        CostumeFilter( Costume costume)
        {
            this.costume = costume;
        }

        @Override
        public boolean accept( SoundResource soundResource )
        {
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
            return this.costume.getThumbnail();
        }
    }
}
