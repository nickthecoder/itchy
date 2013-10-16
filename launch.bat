SET BASE="%~dp0"
SET CLASSPATH=%BASE%\lib\itchy.jar;%BASE%\lib\jame.jar
SET MAIN=uk.co.nickthecoder.itchy.Launcher

Pushd "%~dp0"
move lib\native\win32\*.dll .

java "-Ditchy.base=%BASE%" -classpath "%CLASSPATH%" "%MAIN%" %*

ASSOCIATE .itchy "%~dp0"\launch.bat /q
