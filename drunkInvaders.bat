SET CLASSPATH=lib\itchy.jar;lib\jame.jar
SET MAIN=uk.co.nickthecoder.drunkinvaders.DrunkInvaders

move lib\native\win32\*.dll .

java -classpath "%CLASSPATH%" "%MAIN%"
