package uk.co.nickthecoder.itchy.script;

public class GroovyFactory implements ScriptLanguageFactory
{

	@Override
	public String getExtension()
	{
		return "groovy";
	}

	@Override
	public ScriptLanguage create( ScriptManager scriptManager )
	{
		return new GroovyLanguage(scriptManager);
	}
}
