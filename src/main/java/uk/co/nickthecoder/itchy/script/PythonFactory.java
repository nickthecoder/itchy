package uk.co.nickthecoder.itchy.script;

public class PythonFactory implements ScriptLanguageFactory
{

	@Override
	public String getExtension()
	{
		return "py";
	}

	@Override
	public ScriptLanguage create( ScriptManager scriptManager )
	{
		return new PythonLanguage(scriptManager);
	}

}
