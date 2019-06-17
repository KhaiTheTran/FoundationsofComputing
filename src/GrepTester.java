import java.io.File;
import java.nio.file.Files; 
import java.nio.file.FileSystems; 
import java.nio.file.Paths; 
import java.io.*;

import java.util.Scanner;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaCompiler.CompilationTask;

import java.lang.reflect.InvocationTargetException;
import java.security.Permission;

public class GrepTester {
    public static int NUM_TESTS = 14;
    public static ByteArrayOutputStream baos;    
    public static Iterable<? extends JavaFileObject> compilationUnits;
    public static CompilationTask task; 
    public static boolean compilationSuccess;
    public static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    public static DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    public static StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    public static PrintStream SYS_OUT = System.out;
    public static String prefix = ".";


    public static void main(String args[]) throws IOException {
        /* Make sure regex-grammar.cfg is in the current folder */
        if (Files.exists(FileSystems.getDefault().getPath("./src/regex-grammar.cfg"))) {
            prefix = "src";
        }

        if (!prefix.equals(".")) {
            Files.copy(FileSystems.getDefault().getPath("./src/regex-grammar.cfg"), 
                       FileSystems.getDefault().getPath("./regex-grammar.cfg"));
        }

        /* Compile the student code */
        for (String path : Arrays.asList("./" + prefix + "/")) {
            try {
                compilationUnits = fileManager.getJavaFileObjectsFromStrings(
                        Arrays.asList(path + "MalformedGrammarException.java", path + "Rule.java",
                            path + "EarleyRule.java", path + "EarleyParse.java", path + "CFG.java", path + "ASTNode.java",
                            path + "FSMState.java", path + "FSMTransition.java", path + "NFA.java", path + "Grep.java"));
            } catch (Exception e) {
                // oops...
                continue;
            }

            task = compiler.getTask(null, null, null, Arrays.asList("-d", "."), null, compilationUnits);

            compilationSuccess = task.call();

            if (compilationSuccess) {
                break;
            }
        } 

        if (compilationSuccess == false) {
            System.err.println("Your Grep.java file failed to compile!");
            System.exit(1);
        }

        int n = 0;

        runTest(n++, 0, "aa");
        runTest(n++, 0, "aa*");
        runTest(n++, 0, "ab");
        runTest(n++, 0, "bb|-");
        runTest(n++, 0, "aaa-");
        runTest(n++, 0, "(a|b)|c|(b*c)(aa*)*");
        runTest(n++, 0, "a(a|(b|c|(b*)(aa*))*)");
        runTest(n++, 0, "aa|ba");
        runTest(n++, 0, "b(aa*)*");
        runTest(n++, 1, "aa");
        runTest(n++, 1, "aa*");
        runTest(n++, 1, "ab");
        runTest(n++, 1, "bb");
        runTest(n++, 1, "aaa");
        runTest(n++, 1, "aa|ba");
        runTest(n++, 0, "c(aa*)*");
        runTest(n++, 2, "rr(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)*");
        runTest(n++, 2, "(a|e|i|o|u)(a|e|i|o|u)(a|e|i|o|u)");
        runTest(n++, 2, "ad(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)m");
        runTest(n++, 2, "i(a|e|i|o|u)*(i(a*|e*|i*|o*|u*)*)*i");
        System.out.println("You passed all the tests!");
    }

    public static boolean runTest(int testNumber, int file, String regex) throws IOException {
        NUM_TESTS = testNumber + 1;
        System.out.printf("Test %02d...", testNumber);

        /* Redirect output; so, we can capture the answer. */
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        /* Run the student code if we can; otherwise, exit. */
        try {
            trapExit();
            String fileName = "test" + file;
            if (prefix.equals("src")) {
                fileName = Paths.get("tests", fileName).toString();
            }
            else {
                fileName = Paths.get("..", "tests", fileName).toString();
            }
            Object returncode = Class.forName("Grep").getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { new String[] {regex, fileName} });
            allowExit();
            System.setOut(SYS_OUT);
        } catch (Exception e) {
            allowExit();
            System.setOut(SYS_OUT);
            System.out.println("Failed :(");

            if (e instanceof InvocationTargetException) {
                e = (Exception)e.getCause();
            }

            if (e instanceof ExitTrappedException) {
                System.err.println("Did you change the command line arguments for Grep?  Take a look at Task 4.");
                System.exit(1);
            }
            else {
                System.err.println("Something weird went wrong running Grep.  :(");
                e.printStackTrace();
                System.exit(1);
            }
        }

        System.setOut(System.out);

        String grepOut = baos.toString().replaceAll("\r", "");

        String fileName = "";
        if (prefix.equals("src")) {
            fileName = Paths.get("tests", fileName).toString();
        }
        else {
            fileName = Paths.get("..", "tests", fileName).toString();
        }
        Scanner correctFile = new Scanner(new File(Paths.get(fileName, "out." + testNumber).toString())).useDelimiter("\\Z");
        String correct = correctFile.hasNext() ? correctFile.next() + System.lineSeparator() : "";
        correct = correct.replaceAll("\r", "");


        boolean answer = grepOut.equals(correct);
        if (answer) {
            System.out.println("Passed :)");
        }
        else {
            System.out.println("Failed :(");
            String fileString = new Scanner(new File(Paths.get(fileName, "test" + file).toString())).useDelimiter("\\Z").next();
            diff(fileString, grepOut, correct);
            System.exit(1);
        }
        return answer;
    }

    private static class ExitTrappedException extends SecurityException { }

    private static void trapExit() {
        final SecurityManager securityManager = new SecurityManager() {
            public void checkPermission(Permission permission) {
                if ("exitVM.1".equals(permission.getName())) {
                    throw new ExitTrappedException();
                }
            }
        };
        System.setSecurityManager(securityManager);
    }

    private static void allowExit() {
        System.setSecurityManager(null) ;
    }

    public static void diff(String file, String student, String correct) {
        String[] whole = file.replace("\r", "").split("\n");
        String[] stu = student.replace("\r", "").split("\n");
        String[] corr = correct.replace("\r", "").split("\n");

        int n = 0;
        int i = 0;
        int j = 0;
        while (i < stu.length && j < corr.length && n < whole.length) {
            if (whole[n].equals(corr[j]) && corr[j].equals(stu[i])) {
                n++;
                i++;
                j++;
            }
            else if (whole[n].equals(corr[j])) {
                System.out.println("-" + whole[n]);
                n++;
                j++;
            }
            else if (whole[n].equals(stu[i])) {
                System.out.println("+" + whole[n]);
                n++;
                i++;
            }
            else {
                n++;
            }
        }
        while (j < corr.length) {
            System.out.println("-" + corr[j++]);
        }
        while (i < stu.length) {
            System.out.println("+" + stu[i++]);
        }
    }
}
