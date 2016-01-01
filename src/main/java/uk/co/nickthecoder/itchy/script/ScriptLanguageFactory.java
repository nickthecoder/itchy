package uk.co.nickthecoder.itchy.script;

/**
 * If java had class methods (similar to Smalltalk), then there would be no need for this interface, and all of
 * its implementations. We could achieve the same from class methods of ScriptLanguage.
 */
public interface ScriptLanguageFactory
{
	/**
	 * @return The file extension, such as "py" for python and "groovy" for groovy.
	 */
	public String getExtension();
	
	public ScriptLanguage create(ScriptManager scriptManager);
}
