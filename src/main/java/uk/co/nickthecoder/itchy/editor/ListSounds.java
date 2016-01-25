package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.Thumbnailed;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.ThumbnailedPickerButton;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ListSounds extends ListSubjects<SoundResource>
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
            CostumeResource cr = this.resources.getCostumeResource(name);
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

                GuiButton button = new GuiButton("Play");
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
    

    public File getSoundsDirectory()
    {
        File dir = this.resources.getDirectory();
        File soundsDir = new File(dir, "sounds");
        if (soundsDir.exists()) {
            return soundsDir;
        } else {
            return dir;
        }
    }
    
    private FileOpenDialog openDialog; 
    
    @Override
    protected void edit(SoundResource subject)
    {
        if (subject == null) {

            openDialog = new FileOpenDialog() {
                @Override
                public void onChosen( File file )
                {
                    try {
                        File relativeFile = resources.makeRelativeFile(file);
                        String name = Util.nameFromFile(relativeFile);

                        openDialog.hide();

                        SoundResource newSubject = new SoundResource();
                        newSubject.setName(name);;
                        newSubject.setFile(relativeFile);
                        EditSound edit = new EditSound( resources, ListSounds.this, newSubject, false );
                        edit.show();         

                    } catch (JameException e) {
                        openDialog.setMessage(e.getMessage());
                        return;
                    }
                }
            };
            openDialog.setDirectory(getSoundsDirectory());
            openDialog.show();
            return;
        }
        
        EditSound edit = new EditSound( this.resources, this, subject, false );
        edit.show();         
    }

    
    @Override
    protected void remove( SoundResource soundResource )
    {
        StringList usedBy = new StringList();

        for (String costumeName : this.resources.costumeNames()) {
            CostumeResource cr = this.resources.getCostumeResource(costumeName);
            Costume costume = cr.getCostume();
            for (String resourceName : costume.getSoundNames()) {
                for (ManagedSound managedSound : costume.getSoundChoices(resourceName)) {
                    if (managedSound.soundResource == soundResource) {
                        usedBy.add(costumeName);
                    }
                }
            }
        }
        if (usedBy.isEmpty()) {
            this.resources.removeSound(soundResource.getName());
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }

    }
    

    interface Filter extends Thumbnailed
    {
        boolean accept( SoundResource pr );
    }
    
    class CostumeFilter implements Filter
    {
        CostumeResource costumeResource;

        CostumeFilter( CostumeResource costumeResource )
        {
            this.costumeResource = costumeResource;
        }

        @Override
        public boolean accept( SoundResource soundResource )
        {
            Costume costume = this.costumeResource.getCostume();
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
            return this.costumeResource.getThumbnail();
        }
    }
}
