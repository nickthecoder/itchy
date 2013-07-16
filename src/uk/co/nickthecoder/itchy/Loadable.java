/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.io.IOException;

public abstract class Loadable
{

    private File directory;

    private File file;

    protected boolean saveFailed = false;

    public void load( String filename ) throws Exception
    {
        this.file = new File(filename);
        this.directory = this.file.getAbsoluteFile().getParentFile();
    }

    public void save() throws Exception
    {
        try {
            File saveAs = new File(this.directory, "#" + this.file.getName() + "#");
            this.actualSave(saveAs.getPath());
            this.checkSave(saveAs.getPath());

            try {
                this.file.delete();
            } catch (Exception e) {
                // Do nothing
            }
            saveAs.renameTo(this.file);

        } catch (Exception e) {
            this.saveFailed = false;
            throw e;
        }
    }

    protected abstract void actualSave( String filename ) throws Exception;

    protected abstract void checkSave( String filename ) throws Exception;

    public File getDirectory()
    {
        return this.directory;
    }

    public File getFile()
    {
        return this.file;
    }

    public String getFilename()
    {
        return this.file.getPath();
    }

    public void setFilename( String filename )
    {
        this.file = new File(filename);
        this.directory = new File(filename).getAbsoluteFile().getParentFile();
    }

    public String nameFromFilename( String filename )
    {
        File file = new File(filename);
        String name = file.getName();
        int firstDot = name.indexOf('.');
        if (firstDot > 1) {
            return name.substring(0, firstDot);
        }
        return name;
    }

    public String resolveFilename( String filename )
    {
        if (this.directory == null) {
            return filename;
        } else {
            File file = new File(filename);
            if (file.isAbsolute()) {
                System.err.println("Warning. Using absolute filenames to load resource : " +
                        filename);
                return filename;
            } else {
                File result = new File(this.directory, filename);
                return result.getPath();
            }
        }
    }

    public String makeRelativeFilename( File file )
    {
        try {
            String filePath = file.getCanonicalPath();
            String dirPath = this.directory.getCanonicalPath();

            if (dirPath == filePath) {
                return "";
            }

            if (filePath.startsWith(dirPath)) {
                return filePath.substring(dirPath.length() + 1);
            } else {
                return file.getPath();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return file.getPath();
        }

    }

    public boolean fileExists( String filename )
    {
        File file = new File(this.resolveFilename(filename));
        return file.exists() && file.isFile();
    }

    public boolean rename( String oldName, String newName )
    {
        File file = new File(this.resolveFilename(oldName));
        File dest = new File(this.resolveFilename(newName));
        return file.renameTo(dest);
    }

}
