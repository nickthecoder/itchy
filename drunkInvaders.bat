SET CLASSPATH=itchy.jar;lib\jame.jar
SET MAIN=uk.co.nickthecoder.drunkinvaders.DrunkInvaders

move native\win32\*.dll .

java -classpath "%CLASSPATH%" "%MAIN%"
