/* 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.nickthecoder.itchy.tools;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.jame.Surface;

public class GameMenu implements Page
{

    @Override
    public String getName()
    {
        return "Game Menu";
    }

    private File findResourceFile( File directory )
    {
        for (String name: directory.list()) {
            if (name.endsWith(".itchy")) {
                return new File( directory, name );
            }
        }
        return null;
    }
    
    @Override
    public Component createPage()
    {
        Container result = new Container();
        result.setLayout(new VerticalLayout());
        result.setFill(false, true);
        result.setXAlignment(0.5);

        Container main = new Container();
        main.setFill(false, true);
        result.addChild(main);
        main.setExpansion(1);
        main.setYAlignment(0.25);

        Container menu = new Container();
        menu.setFill(true, true);
        menu.setYAlignment(0.25);
        GridLayout grid = new GridLayout(menu, 2);
        menu.setLayout(grid);
        menu.setYSpacing(10);
        menu.setXSpacing(30);

        Container menuScroll = new VerticalScroll(menu);
        menuScroll.setYAlignment(0.25);
        menuScroll.setFill(true, true);
        main.addChild(menuScroll);

        File directory = new File(Itchy.getBaseDirectory(), "resources");
        File defaultImageFile = new File(directory, "icon32.png");

        for (File dir : directory.listFiles()) {
            // Ignore Luancher's own directory.
            if (dir.getName().equals("Launcher")) {
                continue;
            }

            if (dir.isDirectory()) {
                final File resourceFile = findResourceFile( dir );
                if (resourceFile != null) {

                    Container combo = new Container();
                    combo.setType("comboBox");
                    combo.addStyle("combo");
                    combo.setFill(true, true);

                    Button playButton = new Button();

                    File imageFile = new File(dir, "icon32.png");
                    try {
                        Surface image = new Surface((imageFile.exists() ? imageFile : defaultImageFile).getPath());
                        ImageComponent icon = new ImageComponent(image);
                        playButton.addChild(icon);
                    } catch (Exception e) {
                        // Do nothing
                        e.printStackTrace();
                    }
                    playButton.addChild(new Label(dir.getName()));
                    playButton.setFill(true, true);
                    playButton.setXAlignment(0.5);
                    playButton.setYAlignment(0.5);
                    playButton.setXSpacing(5);
                    playButton.setExpansion(1.0);
                    playButton.addActionListener(new ActionListener()
                    {
                        @Override
                        public void action()
                        {
                            launchGame(resourceFile);
                        }
                    });

                    Button editButton = new Button("Edit");
                    editButton.addActionListener(new ActionListener()
                    {
                        @Override
                        public void action()
                        {
                            editGame(resourceFile);
                        }
                    });
                    combo.addChild(playButton);
                    combo.addChild(editButton);

                    grid.addChild(combo);
                }
            }
        }
        grid.endRow();

        return result;
    }

    private void launchGame(File resourceFile)
    {
        Resources resources = new Resources();
        try {
            resources.load(resourceFile);

            resources.getGame().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editGame(File resourceFile)
    {
        Resources resources = new Resources();
        try {
            resources.load(resourceFile);
            Game game = resources.getGame();

            game.startEditor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
