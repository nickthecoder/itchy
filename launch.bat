SET CLASSPATH=lib\itchy.jar;lib\jame.jar
SET MAIN=uk.co.nickthecoder.itchy.Launcher

move lib\native\win32\*.dll .

# cd to the parent directory of the "resources" folder. Itchy uses relative paths to get to its resources,
# such as scripts, templates, the editor's resources etc.
#
Pushd "%~dp0"

java -classpath "%CLASSPATH%" "%MAIN%" %*

