/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.io.IOException;

public abstract class Loadable
{

    /**
     * The base directory. The resource will live in this directory, or one of its sub-directories.
     */
    private File directory;

    /**
     * The name of the resource. This is usually RELATIVE to this.directory, but can be absolute, but will issue warnings that the resource
     * is being loaded from an absolute path.
     */
    private File file;

    public Loadable()
    {
        this.directory = null;
        this.file = null;
    }

    public Loadable( File file )
    {
        this.directory = file.getAbsoluteFile().getParentFile();
        this.file = new File(file.getName());
    }

    public Loadable( File directory, File file )
    {
        this.directory = directory;
        this.file = file;
    }

    public void load( File file ) throws Exception
    {
        this.directory = file.getAbsoluteFile().getParentFile();
        this.file = new File(file.getName());
        this.load();
    }

    public abstract void load() throws Exception;

    public void save() throws Exception
    {
        File saveAs = new File(this.directory, "#" + this.file.getName() + "#");
        this.actualSave(saveAs);
        this.checkSave(saveAs);

        File file = this.getFile();
        file.delete();
        saveAs.renameTo(file);
    }

    protected abstract void actualSave( File file ) throws Exception;

    protected abstract void checkSave( File file ) throws Exception;

    public File getDirectory()
    {
        return this.directory;
    }

    /**
     * @return true iff file is within the directory.
     */
    public boolean fileIsWithin( File file )
    {
        try {
            String dirStr = getDirectory().getCanonicalPath();
            String fileStr = resolveFile(file).getCanonicalPath();

            return fileStr.startsWith(dirStr);
        } catch( Exception e ) {
            return false;
        }
    }
    
    public void setFile( File file )
    {
        this.directory = file.getParentFile();
        this.file = new File(file.getName());
    }

    public File getFile()
    {
        return resolveFile(this.file);
    }

    /**
     * Renames the current object on disk.
     * 
     * @param file
     *        The new name for the file, which can be (and usually is) relative to this.directory.
     */
    public void renameFile( File file )
    {
        File oldFile = getFile();
        this.file = file;
        File newFile = getFile();

        oldFile.renameTo(newFile);
    }

    /**
     * Renames a file using names relative to this directory.
     * 
     * @return true if the rename succeeded.
     */
    public boolean renameFile( String oldName, String newName )
    {
        File file = new File(this.resolveFilename(oldName));
        File dest = new File(this.resolveFilename(newName));
        return file.renameTo(dest);
    }

    
    
    public String getFilename()
    {
        return resolveFile(this.file).getPath();
    }

    public File resolveFile( File file )
    {
        if (this.directory == null) {
            return file;
        } else {
            if (file.isAbsolute()) {
                return file;
            } else {
                File result = new File(this.directory, file.getPath());
                return result;
            }
        }
    }

    public String resolveFilename( String filename )
    {
        return resolveFile(new File(filename)).getPath();
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

    public File makeRelativeFile( File file )
    {
        return new File( makeRelativeFilename(file));
    }

    public boolean fileExists( String filename )
    {
        File file = new File(this.resolveFilename(filename));
        return file.exists() && file.isFile();
    }

    public void ensure( Object a, Object b, String reason ) throws Exception
    {
        if (!a.equals(b)) {
            throw new Exception(reason + " " + a + " ---vs--- " + b);
        }
    }

    public void ensure( boolean test, String reason ) throws Exception
    {
        if (!test) {
            throw new Exception(reason);
        }
    }
}
