SET CLASSPATH=lib\itchy.jar;lib\jame.jar
SET MAIN=uk.co.nickthecoder.itchy.Launcher

move lib\native\win32\*.dll .

java -classpath "%CLASSPATH%" "%MAIN%" %*

