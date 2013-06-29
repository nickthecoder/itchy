SET CLASSPATH=itchy.jar;lib\jame.jar;lib\bsh.jar;lib\jdom.jar
SET MAIN=uk.co.nickthecoder.drunkinvaders.DrunkInvaders

move native\win32\*.dll .

java -classpath "%CLASSPATH%" "%MAIN%"
