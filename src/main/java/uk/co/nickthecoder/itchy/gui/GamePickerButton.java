package uk.co.nickthecoder.itchy.gui;

import java.io.File;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.Itchy;

public class GamePickerButton extends PickerButton<File>
{

    public GamePickerButton( File resourceFile )
    {
        super( "Choose a Game", resourceFile, getGames());
    }

    private static HashMap<String, File> getGames()
    {
        HashMap<String, File> result = new HashMap<String, File>();

        File directory = Itchy.getResourcesDirectory();

        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                final File resourceFile = new File(dir, dir.getName() + ".itchy");
                if (resourceFile.exists()) {

                    result.put(dir.getName(), resourceFile);
                }
            }
        }
        return result;
    }

}
