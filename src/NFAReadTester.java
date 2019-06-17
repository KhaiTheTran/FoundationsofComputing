import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

// These tests written by Esteban Posada :)

public class NFAReadTester {
    // keeps tracks over all the single tests
    static boolean passedAllTests = true;

    public static void main(String[] args) {
        System.out.println("Testing NFA.read()");
        System.out.println();

        test1();
        test2();
        test3();
        test4();
        test5();

        System.out.println();
        if (passedAllTests) {
            System.out.println("All tests are passing!");
        } 
    }

    // All binary strings of even length
    public static void test1() {
        String[] tests = {
            "101100101101", 
            "0", 
            "", 
            "101101110111011", 
            "10111011", 
            "00000000000000000010"
        };
        boolean[] answers = {true, false, true, false, true, true};

        testNfa("1) All binary strings of even length.", 
                tests, answers, machine1());
    }

    // Accepts binary strings with a '1' three positions from end
    public static void test2() { 
        NFA machine = machine2();

        String[] tests = {
            "101100101101", 
            "0", 
            "", 
            "101101110111011", 
            "10111011", 
            "000000000000000000110"
        };
        boolean[] answers = {true, false, false, false, false, true};

        testNfa("2) All binary strings with 1 three positions from the end.", 
                tests, answers, machine2());
    }

    // All binary strings with an even number of ones
    public static void test3() {
        String[] tests = {
            "101100101101", 
            "0", 
            "", 
            "101101110111011", 
            "10111011", 
            "000000000000000000110"};
        boolean[] answers = {false, true, true, false, true, true};

        testNfa("3) All binary strings with an even number of ones.", 
                tests, answers, machine3());

    }

    // Accepts binary strings containing '11'
    public static void test4() {
        String[] tests = {
            "101100101101", 
            "0", 
            "", 
            "101101110111011", 
            "1011101", 
            "000000000000000000110"
        };
        boolean[] answers = {true, false, false, true, true, true};

        testNfa("4) All binary strings containing the substring 11.",
                tests, answers, machine4());

    }

    // All binary strings with equal number of substrings 01 and 10
    public static void test5() {
        String[] tests = {
            "101100101101", 
            "0", 
            "", 
            "10110111011101110", 
            "10111010", 
            "000000000000000000110"
        };
        boolean[] answers = {true, true, true, false, false, true};

        testNfa("5) All binary strings with equal number of substrings 01 and 10.",
                tests, answers, machine5());

    }

    public static void testNfa(String prompt, String[] tests, boolean[] answers, NFA machine) {
        System.out.println(prompt);
        System.out.println();

        boolean allTestsPass = true;
        for (int i = 0; i < tests.length; i++) {
            String input = tests[i];
            boolean expectedAnswer = answers[i];

            try {
                boolean studentAnswer = machine.read(input);
                if (studentAnswer != expectedAnswer) {
                    System.out.println(String.format(
                        "    nfa.read(\"%s\") failed.", input));
                    System.out.println(String.format(
                        "         Returned '%b', expected '%b'.",
                        studentAnswer, expectedAnswer));
                    System.out.println();
                    allTestsPass = false;
                    passedAllTests = false;
                }
            } catch (Exception ex) {
                System.out.println(String.format(
                        "    nfa.read(\"%s\") threw an exception!", input));
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                System.out.println(indent("        ", sw.toString()));
                System.out.println();

                allTestsPass = false;
                passedAllTests = false;
            }
        }

        if (allTestsPass) {
            System.out.println("    Test passed!");
            System.out.println();
        }
    }

    public static NFA machine1() {
        FSMState oddLength = new FSMState();
        FSMState start = new FSMState();

        Set<FSMState> states = new HashSet<FSMState>();
        states.add(oddLength);
        states.add(start);

        Set<FSMState> endStates = new HashSet<FSMState>();
        endStates.add(start);

        Set<FSMTransition> transitions = new HashSet<FSMTransition>();
        transitions.add(new FSMTransition('0', start, oddLength));
        transitions.add(new FSMTransition('1', start, oddLength));
        transitions.add(new FSMTransition('0', oddLength, start));
        transitions.add(new FSMTransition('1', oddLength, start));

        return new NFA(states, start, endStates, transitions);
    }

    public static NFA machine2() {
        FSMState threeFromEnd = new FSMState();
        FSMState twoFromEnd = new FSMState();
        FSMState oneFromEnd = new FSMState();
        FSMState end = new FSMState();

        Set<FSMState> states = new HashSet<FSMState>();
        states.add(threeFromEnd);
        states.add(twoFromEnd);
        states.add(oneFromEnd);
        states.add(end);

        Set<FSMState> endStates = new HashSet<FSMState>();
        endStates.add(end);

        Set<FSMTransition> transitions = new HashSet<FSMTransition>();
        transitions.add(new FSMTransition('0', threeFromEnd, threeFromEnd));
        transitions.add(new FSMTransition('1', threeFromEnd, threeFromEnd));
        transitions.add(new FSMTransition('1', threeFromEnd, twoFromEnd));
        transitions.add(new FSMTransition('0', twoFromEnd, oneFromEnd));
        transitions.add(new FSMTransition('1', twoFromEnd, oneFromEnd));
        transitions.add(new FSMTransition('0', oneFromEnd, end));
        transitions.add(new FSMTransition('1', oneFromEnd, end));

        return new NFA(states, threeFromEnd, endStates, transitions);
    }

    public static NFA machine3() {
        FSMState a0 = new FSMState();
        FSMState a1 = new FSMState();

        Set<FSMState> states = new HashSet<FSMState>();
        states.add(a0);
        states.add(a1);

        Set<FSMState> endStates = new HashSet<FSMState>();
        endStates.add(a0);

        Set<FSMTransition> transitions = new HashSet<FSMTransition>();
        transitions.add(new FSMTransition('0', a0, a0));
        transitions.add(new FSMTransition('1', a0, a1));
        transitions.add(new FSMTransition('0', a1, a1));
        transitions.add(new FSMTransition('1', a1, a0));

        return new NFA(states, a0, endStates, transitions);
    }

    public static NFA machine4() {
        FSMState b0 = new FSMState();
        FSMState b1 = new FSMState();
        FSMState b2 = new FSMState();

        Set<FSMState> states = new HashSet<FSMState>();
        states.add(b0);
        states.add(b1);
        states.add(b2);

        Set<FSMState> endStates = new HashSet<FSMState>();
        endStates.add(b2);

        Set<FSMTransition> transitions = new HashSet<FSMTransition>();
        transitions.add(new FSMTransition('0', b0, b0));
        transitions.add(new FSMTransition('1', b0, b0));
        transitions.add(new FSMTransition('1', b0, b1));
        transitions.add(new FSMTransition('1', b1, b2));
        transitions.add(new FSMTransition('0', b2, b2));
        transitions.add(new FSMTransition('1', b2, b2));

        return new NFA(states, b0, endStates, transitions);
    }

    public static NFA machine5() {
        FSMState q0 = new FSMState();
        FSMState q1 = new FSMState();
        FSMState q2 = new FSMState();
        FSMState q3 = new FSMState();
        FSMState q4 = new FSMState();

        Set<FSMState> states = new HashSet<FSMState>();
        states.add(q0);
        states.add(q1);
        states.add(q2);
        states.add(q3);
        states.add(q4);

        Set<FSMState> endStates = new HashSet<FSMState>();
        endStates.add(q0);
        endStates.add(q1);
        endStates.add(q2);

        Set<FSMTransition> transitions = new HashSet<FSMTransition>();
        transitions.add(new FSMTransition('0', q0, q1));
        transitions.add(new FSMTransition('1', q0, q2));
        transitions.add(new FSMTransition('0', q1, q1));
        transitions.add(new FSMTransition('1', q2, q2));
        transitions.add(new FSMTransition('1', q1, q3));
        transitions.add(new FSMTransition('0', q3, q1));
        transitions.add(new FSMTransition('1', q3, q3));
        transitions.add(new FSMTransition('0', q2, q4));
        transitions.add(new FSMTransition('1', q4, q2));
        transitions.add(new FSMTransition('0', q4, q4));

        return new NFA(states, q0, endStates, transitions);
    }

    public static String indent(String prefix, String text) {
        return String.join(
                System.getProperty("line.separator"), 
                Arrays.stream(text.split("\\r?\\n|\\r"))
                    .map(s -> prefix + s)
                    .collect(Collectors.toList()));
    }
}
