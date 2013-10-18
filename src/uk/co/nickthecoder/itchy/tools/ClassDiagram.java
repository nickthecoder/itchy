/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.tools;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.nickthecoder.itchy.util.Util;

/**
 * Creates a HTML5 class diagram from a specially crafted html pro-forma. Its designed to create
 * semi-automated class diagrams, which link to the javadocs, and also allow part of the diagram to
 * be fully hand-crafted. This allowed me to combine Java and Javascript classes on a single
 * diagram.
 * <p>
 * Reads a html file and looks for a special section looking like :
 * 
 * <code>
 * <pre>
 * &lt;!--GENERATE
 * CLASS mypackage.MyClass
 *   X 100
 *   Y 200
 * FILEDS
 *   myField
 * METHODS
 *   myMethod1
 *   myMethod2
 * --&gt;
 * </pre>
 * </code>
 * 
 * There can be as many "CLASS" sections as you wish. Note, you can use the "CLASS" tag for
 * interfaces too.
 * <p>
 * Each field and method will be checked using reflection.
 * <p>
 * The generated code is inserted between the sections marked : <code>
 * <pre>
 * &lt;!--BEGIN_CONTENT--&gt;
 * &lt;!--END_CONTENT--&gt;
 * </pre>
 * </code>
 * 
 * The generated code will include links to the API documentation, which is assumed to be at
 * "../api/" relative to the html file.
 * 
 * Assuming no errors are thrown, the original pro-forma document is replaced by the generated code.
 * 
 * <h2>Known Bugs</h2>
 * Methods which take varargs don't link to the API documentation correctly. The API uses "...", but
 * the generated code uses "[]".
 */
public class ClassDiagram
{
    private File file;
    private static final String BEGIN_CONTENT = "<!--BEGIN_CONTENT-->";
    private static final String END_CONTENT = "<!--END_CONTENT-->";

    private PrintWriter writer;

    private ClassRequirements currentKlass;

    private List<ClassRequirements> klasses;

    private Set<String> currentSet;

    public ClassDiagram( File file )
    {
        this.file = file;
    }

    public void generate()
        throws Exception
    {
        File backupFile = new File(this.file.getPath() + "~");
        File tempFile = new File(this.file.getPath() + "#");

        Util.copyFile(this.file, backupFile);

        String html = Util.readFile(this.file);

        int generateStart = html.indexOf("<!--GENERATE");
        int generateEnd = html.indexOf("-->", generateStart);

        String generate = html.substring(generateStart + 12, generateEnd);
        parse(generate.split("\\r?\\n"));

        int contentStart = html.indexOf(BEGIN_CONTENT);
        int contentEnd = html.indexOf(END_CONTENT);

        this.writer = new PrintWriter(tempFile);
        try {

            this.writer.println(html.substring(0, contentStart));
            this.writer.println(BEGIN_CONTENT);

            for (ClassRequirements klass : this.klasses) {
                generate(klass);
            }

            this.writer.print(html.substring(contentEnd));

        } finally {
            this.writer.close();
        }

        tempFile.renameTo(this.file);
    }

    private void parse( String[] lines )
        throws Exception
    {
        this.klasses = new ArrayList<ClassRequirements>();

        for (String line : lines) {
            line = line.trim();

            System.out.println("> " + line);
            String param = "";
            if (line.indexOf(' ') > 0) {
                param = line.substring(line.indexOf(' ')).trim();
            }
            if ("".equals(line)) {
                continue;
            }

            if (line.startsWith("CLASS")) {
                this.currentKlass = new ClassRequirements(param);
                this.currentSet = null;
                this.klasses.add(this.currentKlass);

            } else if (line.startsWith("X")) {
                this.currentKlass.x = Integer.parseInt(param);
            } else if (line.startsWith("Y")) {
                this.currentKlass.y = Integer.parseInt(param);
            } else if (line.startsWith("METHODS")) {
                this.currentSet = this.currentKlass.methodNames;
            } else if (line.startsWith("FIELDS")) {
                this.currentSet = this.currentKlass.fieldNames;
            } else {
                this.currentSet.add(line);
            }

        }
    }

    private static String getSlashName( Class<?> klass )
    {
        return klass.getName().replace('.', '/');
    }

    private String getMethodSig( Method method )
    {
        String result = "";

        boolean first = true;
        for (Class<?> arg : method.getParameterTypes()) {

            String name = arg.getName();
            if (name.indexOf("[L") == 0) {
                System.out.println("Arg : " + arg + " name " + name);
                name = name.substring(2, name.length() - 1) + "[]";
            }
            if (!first) {
                result += ", ";
            }
            result += name;
            first = false;
        }

        return result;
    }

    private void generate( ClassRequirements classRequirements )
    {
        System.out.println("Generating : " + classRequirements.klass.getName());
        String name = classRequirements.klass.getSimpleName();
        String slashName = getSlashName(classRequirements.klass);

        this.writer.println("<div id=\"" + name + "\" class=\"box\" style=\"left:" +
            classRequirements.x +
            "px;top:" + classRequirements.y + "px;\">");
        this.writer.println("  <div class=\"title\"><a href=\"../api/" + slashName + ".html\">" +
            name + "</a></div>");
        this.writer.println("  <div class=\"content\">");

        if (!classRequirements.fieldNames.isEmpty()) {
            List<String> sortedFieldNames = new ArrayList<String>(classRequirements.fieldNames);
            Collections.sort(sortedFieldNames);

            this.writer.println("    <ul class=\"fields\">");
            for (String fieldName : sortedFieldNames) {
                try {
                    Field field = classRequirements.klass.getDeclaredField(fieldName);
                    String statik = Modifier.isStatic(field.getModifiers()) ? " class=\"static\""
                        : "";
                    this.writer.println("      <li" + statik + "><a href=\"../api/" + slashName +
                        ".html#" + fieldName +
                        "\">" + fieldName + "</a></li>");
                } catch (Exception e) {
                    System.out.println("Field not found : " + fieldName);
                }
            }
            this.writer.println("    </ul>");

        }

        HashMap<String, HashMap<String, Method>> knownMethods = new HashMap<String, HashMap<String, Method>>();
        for (Class<?> klass = classRequirements.klass; klass != null; klass = klass.getSuperclass()) {
            Method[] declaredMethods = classRequirements.klass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                String methodName = method.getName();
                if (!knownMethods.containsKey(methodName)) {
                    knownMethods.put(methodName, new HashMap<String, Method>());
                }
                knownMethods.get(methodName).put(getMethodSig(method), method);
            }
        }

        if (!classRequirements.methodNames.isEmpty()) {
            List<String> sortedMethodNames = new ArrayList<String>(classRequirements.methodNames);
            Collections.sort(sortedMethodNames);

            this.writer.println("    <ul class=\"methods\">");

            for (String methodName : sortedMethodNames) {

                HashMap<String, Method> overloaded = knownMethods.get(methodName);
                if (overloaded == null) {
                    System.out.println("Method not found : " + methodName);
                } else {
                    for (Method method : overloaded.values()) {
                        String statik = Modifier.isStatic(method.getModifiers())
                            ? " class=\"static\"" : "";
                        String methodSig = getMethodSig(method);
                        this.writer.println("      <li" + statik + "><a href=\"../api/" +
                            slashName + ".html#" +
                            methodName + "(" + methodSig + ")\">" + methodName + "</a></li>");
                    }
                }
            }
            this.writer.println("    </ul>");
        }
        this.writer.println("  </div>");
        this.writer.println("</div>");

    }

    /**
     * Usage : ClassDiagram HTML_TEMPLATE
     */
    public static void main( String[] argv )
        throws Exception
    {
        String path = argv[0];

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found: " + file);
        } else {

            new ClassDiagram(file).generate();
        }

    }

    public class ClassRequirements
    {
        Class<?> klass;
        int x;
        int y;
        Set<String> methodNames;
        Set<String> fieldNames;

        ClassRequirements( String className )
            throws Exception
        {
            this.klass = Class.forName(className);
            this.methodNames = new HashSet<String>();
            this.fieldNames = new HashSet<String>();
        }
    }
}
