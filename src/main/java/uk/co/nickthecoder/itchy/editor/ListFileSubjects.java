package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;

public abstract class ListFileSubjects<S> extends ListSubjects<S>
{
    private FileOpenDialog openDialog; 

    public ListFileSubjects(Resources resources)
    {
        super(resources);
    }

    protected void addOrEdit(S subject)
    {
        if (subject == null) {
            if (subject == null) {

                openDialog = new FileOpenDialog() {
                    @Override
                    public void onChosen( File file )
                    {
                        if (file == null) {
                            openDialog.hide();
                            return;
                        }
                        
                        try {
                            File relativeFile = resources.makeRelativeFile(file);
                            String name = Util.nameFromFile(relativeFile);

                            openDialog.hide();

                            add( name, relativeFile );

                        } catch (JameException e) {
                            openDialog.setMessage(e.getMessage());
                            return;
                        }
                    }
                };
                openDialog.setDirectory(getDirectory());
                openDialog.show();
                return;
            }
        } else {
            edit( subject );
        }
    }
    
    protected abstract File getDirectory();
    
    protected abstract void add(String name, File relativeFile) throws JameException;

    protected abstract void edit(S subject);
    
}
