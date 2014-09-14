' Launches the Itchy java program
' ===============================
'
' Usage : launch.vbs [--verbose] [TARGET] [--editor]
' Where TARGET is either the id of a game, or the full path to a .itchy file.
' Examples :
' launch.vbs rain
' launch.vbs --verbose "C:\Users\T400\Desktop\itchy game\resources\rain.itchy"
'
' I have no clue how to write VBScripts (or use Windows well), so if you can improve this,
' please email nickthecoder at gmail dot com your improvements. Thanks.
'
'
Set objFSO = CreateObject("Scripting.FileSystemObject")

' *** Find the java runtime
' On a 64 bit OS, we need to find the 32 bit version of java, because the JNI code is
' 32 bit, so look in "Program Files (x86)" first, and then try "Program Files"
strJava = "java"

paths= Array( "C:\Program Files (x86)\Java", "C:\Program Files\Java" )
names = Array( "jre8", "jre7", "jdk8", "jdk7" )

For i = 0 to UBound(paths)
    For j = 0 to UBound(names)
        path = paths(i) + "\" + names(j) + "\bin\java.exe"
        If objFSO.FileExists(path) Then
            strJava = paths(i) + "\" + names(j) + "\bin\java"
            i = 999
            Exit For
        End If
    Next
Next

' Get the arguments from the command line
verbose = False
Set objArgs = Wscript.Arguments
strSendArgs = ""
For i = 0 to objArgs.count - 1
    strArg = objArgs.Item(i)
    If strArg = "/v" Or strArg = "-v" Or strArg = "--verbose" Then
        verbose = True
    Else
        strSendArgs = strSendArgs + " """ + strArg + """"
    End If
Next

If Not verbose Then
   ' If we add 'w', then the console will not be shown.
   strJava = strJava & "w"
End IF

' *** Get the directory of this script
strPath = Wscript.ScriptFullName
Set objFile = objFSO.GetFile(strPath)
strFolder = objFSO.GetParentFolderName(objFile) 

strCP = strFolder + "\lib\jame.jar;" + strFolder + "\lib\itchy.jar;" + strFolder + "\lib\jython-2.5.3.jar;" + strFolder + "\lib\groovy-all-2.3.6.jar"

strExplorerCommand = "explorer.exe /e," & strFolder

Set objShell = CreateObject("Wscript.Shell")
' objShell.Run strExplorerCommand

strJavaCommand = """" + strJava + """ -cp """ & strCP & """ uk.co.nickthecoder.itchy.Launcher" + strSendArgs

objShell.Run strJavaCommand
