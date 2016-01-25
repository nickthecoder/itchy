package uk.co.nickthecoder.itchy.util;

import java.io.File;

public class SaveBackup
{
    private File finalDestination;
    

    /**
     * Throws an exception if the two objects aren't equal. This is used when testing that a file was saved correctly.
     * @param reason A human readable string explaining what went wrong.
     * @throws Exception
     */
    public static void ensure( Object a, Object b, String reason ) throws Exception
    {
        if (!a.equals(b)) {
            throw new Exception(reason);
        }
    }
    
    public SaveBackup( File finalDestination )
    {
        this.finalDestination = finalDestination;
    }
    
    public File getTemporyFile()
    {
        return new File(this.finalDestination.getParent(), "#" + this.finalDestination.getName() + "#");
    }
    
    public void complete()
    {
        if (finalDestination.exists()) {
            File backupFile = new File(this.finalDestination.getParent(), this.finalDestination.getName() + "~");
            if (backupFile.exists()) {
                backupFile.delete();
            }
            finalDestination.renameTo( backupFile );
        }
        getTemporyFile().renameTo(finalDestination);
    }
    
    /**
     * Renames the file given in the constructor, to one with a tilda at the end.
     * Used when deleting a file, that you want to keep a backup of.
     * Not used in conjunction with "complete".
     */
    public void renameToBackup()
    {
        File backupFile = new File(this.finalDestination.getParent(), this.finalDestination.getName() + "~");
        if (backupFile.exists()) {
            backupFile.delete();
        }
        this.finalDestination.renameTo(backupFile);
    }
}
