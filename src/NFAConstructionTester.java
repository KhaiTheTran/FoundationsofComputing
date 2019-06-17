import java.lang.reflect.Field;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class NFAConstructionTester {
    public static final char[] ALPHABET = 
        "abcdefghijklmnopqrstuvwxyz0123456789'-".toCharArray();

    public static void main(String[] args) {
        CFG cfg = findCfg(); 
        EarleyParse.setGrammar(cfg);

        System.out.println("BASIC CASES:");
        compareNfa("a", makeSingleTestCase());
        compareNfa("ab", makeConcatTestCase());
        compareNfa("a|b", makeUnionTestCase());
        compareNfa("a*", makeStarTestCase());
        System.out.println();

        System.out.println("COMPLEX CASES:");
        compareNfa("a|(b*c)", makeComplexCase1());
        compareNfa("(a|bc|c)*", makeComplexCase2());
        compareNfa("a*b*c*", makeComplexCase3());
        compareNfa("(ab|cd*e)fg", makeComplexCase4());
        System.out.println();
    }

    public static CFG findCfg() {
        CFG cfg = null;
        try {
            try {
                cfg = new CFG("regex-grammar.cfg");
            } catch (FileNotFoundException e1) {
                try {
                    cfg = new CFG("../src/regex-grammar.cfg");
                } catch (FileNotFoundException e2) {
                    try {
                        cfg = new CFG("src/regex-grammar.cfg");
                    } catch (FileNotFoundException e3) {
                        System.err.println("ERROR: Make sure your regex-grammar.cfg file is either in the task_3_tests directory or the src directory.");
                        System.exit(1);
                    }
                }
            }        
        } catch (MalformedGrammarException e3) {
            System.err.println("ERROR: The CFG you wrote does not parse correctly.  Make sure you read the instructions in the spec carefully.");
            System.exit(1);
        }
        return cfg;
    }

    public static void compareNfa(String input, NFAMirror solution) {
        try {
            NFAMirror student = constructStudentNFA(input);
            if (student == null) {
                System.out.println(String.format(
                            "FAIL: Grep.makeNFAFromRegex(\"%s\") returned null", input));
                System.out.println("    Expected output:");
                System.out.println(indent("        ", solution.toString()));
                System.out.println();
            }
            if (areGraphsIsomorphic(student, solution)) {
                System.out.println(String.format(
                            "PASS: Grep.makeNFAFromRegex(\"%s\") matched!", input));
            } else {
                System.out.println(String.format(
                            "FAIL: Grep.makeNFAFromRegex(\"%s\") didn't match", input));

                System.out.println("    Your NFA:");
                System.out.println(indent("        ", student.toString()));
                System.out.println();
                System.out.println("    Solution NFA (omitting unreachable states and transitions):");
                System.out.println(indent("        ", solution.toString()));
                System.out.println();
            }
        } catch (Exception ex) {
                System.out.println(String.format(
                            "FAIL: Grep.makeNFAFromRegex(\"%s\") threw exception!", input));
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                System.out.println(indent("    ", sw.toString()));
                System.out.println();
        }
    }

    public static NFAMirror constructStudentNFA(String input) {
        ASTNode parsed = null;
        try {
            parsed = EarleyParse.parse(input);
        } catch (NullPointerException e) {
            throw new NFATestException(
                    "Your CFG failed to parse the input \"" + input + "\"", e);
        }

        NFA originalNfa = Grep.makeNFAFromRegex(parsed);
        return (originalNfa == null) ? null : new NFAMirror(originalNfa);
    }

    public static String NFAToString(NFA nfa) {
        return new NFAMirror(nfa).toString();
    }

    public static NFAMirror makeSingleTestCase() {
        return NFABuilder.start()
            .addStartState("start")
            .addTransition('a', "start", "end")
            .addAcceptState("end")
            .toNFA();
    }

    /* Test case for "a|b" */
    public static NFAMirror makeUnionTestCase() {
        return NFABuilder.start()
            .addStartState("start")
            .addAcceptStates("end1", "end2")
            .addTransition('a', "start", "end1")
            .addTransition('b', "start", "end2")
            .toNFA();
    }

    /* Test case for "ab" */
    public static NFAMirror makeConcatTestCase() {
        return NFABuilder.start()
            .addStartState("start")
            .addTransition('a', "start", "middle")
            .addTransition('b', "middle", "end")
            .addAcceptState("end")
            .toNFA();
    }

    /* Test case for "a*" */
    public static NFAMirror makeStarTestCase() {
        return NFABuilder.start()
            .addStartState("start")
            .addAcceptStates("start", "accept")
            .addTransition('a', "start", "accept")
            .addTransition('a', "accept", "accept")
            .toNFA();
    }

    /* Test case for "a|(b*c)" */
    public static NFAMirror makeComplexCase1() {
        return NFABuilder.start()
            .addStartState("start")
            .addAcceptStates("end1", "end2")
            // Branch 1
            .addTransition('a', "start", "end1")

            // Branch 2
            .addTransition('b', "start", "mid")
            .addTransition('b', "mid", "mid")
            .addTransition('c', "mid", "end2")
            .addTransition('c', "start", "end2")
            .toNFA();
    }

    /* Test case for "(a|bc|c)*" */
    public static NFAMirror makeComplexCase2() {
        return NFABuilder.start()
            .addStartState("start")
            .addAcceptStates("start", "accept1", "accept2", "accept3")

            // Branch 1
            .addTransition('a', "start", "accept1")
            .addTransition('a', "accept1", "accept1")
            .addTransition('b', "accept1", "temp")
            .addTransition('c', "accept1", "accept3")

            // Branch 2
            .addTransition('b', "start", "temp")
            .addTransition('c', "temp", "accept2")
            .addTransition('a', "accept2", "accept1")
            .addTransition('b', "accept2", "temp")
            .addTransition('c', "accept2", "accept3")

            // Branch 3
            .addTransition('c', "start", "accept3")
            .addTransition('a', "accept3", "accept1")
            .addTransition('b', "accept3", "temp")
            .addTransition('c', "accept3", "accept3")
            .toNFA();
    }

    /* Test case for "a*b*c*" */
    public static NFAMirror makeComplexCase3() {
        return NFABuilder.start()
            .addStartState("start")
            .addAcceptStates("start", "acceptA", "acceptB", "acceptC")
            
            // Handle a*
            .addTransition('a', "start", "acceptA")
            .addTransition('a', "acceptA", "acceptA")
            
            // Handle b*
            .addTransition('b', "start", "acceptB")
            .addTransition('b', "acceptA", "acceptB")
            .addTransition('b', "acceptB", "acceptB")

            // Handle c*
            .addTransition('c', "start", "acceptC")
            .addTransition('c', "acceptA", "acceptC")
            .addTransition('c', "acceptB", "acceptC")
            .addTransition('c', "acceptC", "acceptC")
            .toNFA();
    }

    /* Test case for "(ab|cd*e)fg" */
    public static NFAMirror makeComplexCase4() {
        return NFABuilder.start()
            .addStartState("start")

            // Branch 1
            .addTransition('a', "start", "stateA")
            .addTransition('b', "stateA", "stateB")

            // Branch 2
            .addTransition('c', "start", "stateC")
            .addTransition('d', "stateC", "stateD")
            .addTransition('d', "stateD", "stateD")
            .addTransition('e', "stateC", "stateE")
            .addTransition('e', "stateD", "stateE")

            // Connect two branches back and finish
            .addTransition('f', "stateB", "stateF")
            .addTransition('f', "stateE", "stateF")
            .addTransition('g', "stateF", "stateG")

            .addAcceptState("stateG")
            .toNFA();
    }


    /**
     * Tests if two graphs have the same structure.
     *
     * Ignores any unreachable states. Currently cannot handle NFAs containing
     * states that have multiple transitions that match the same character.
     */
    public static boolean areGraphsIsomorphic(NFAMirror student, NFAMirror solution) {
        Set<FSMState> encountered = new HashSet<>();
        return areGraphsIsomorphic(
                student.startState, student,
                solution.startState, solution,
                encountered);
    }

    private static boolean areGraphsIsomorphic(
            FSMState currA, NFAMirror A, FSMState currB, NFAMirror B,
            Set<FSMState> encountered) {
        boolean aRepeated = encountered.contains(currA);
        boolean bRepeated = encountered.contains(currB);

        if (aRepeated != bRepeated) {
            // Fail if one path loops and the other doesn't.
            return false;
        } else if (aRepeated && bRepeated) {
            // Succeed if both paths loop to a previously verified state
            return true;
        } else if (currA == null && currB == null) {
            // Succeed if both paths naturally end
            return true;
        } else if (currA == null || currB == null) {
            // Fail if one path ends but the other doesn't
            return false;
        } else {
            boolean aFinal = A.finalStates.contains(currA);
            boolean bFinal = B.finalStates.contains(currB);

            if (aFinal != bFinal) {
                // Fail if one state is an accept state and the other isn't
                return false;
            }

            encountered.add(currA);
            encountered.add(currB);

            Map<Character, FSMState> aChildren = makeCharMap(A.graph.get(currA));
            Map<Character, FSMState> bChildren = makeCharMap(B.graph.get(currB));

            if (aChildren.size() != bChildren.size()) {
                // Fail if number of transitions for current state is different
                return false;
            }

            if (bChildren.size() != B.graph.get(currB).size()) {
                // Don't attempt to handle multipled out-transitions that match
                // the same character.
                throw new NFATestException(
                        "Unexpected error: tester cannot compare states with " + 
                        "multiple matching outgoing transitions.");
            }

            for (char c : ALPHABET) {
                if (!areGraphsIsomorphic(aChildren.get(c), A, 
                            bChildren.get(c), B, encountered)) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Converts a set of nodes and edges into adjancency list form to
     * make traversal more convenient.
     */
    public static Map<FSMState, Set<FSMTransition>> makeAdjacencyList(
            Set<FSMState> nodes, Set<FSMTransition> edges) {
        Map<FSMState, Set<FSMTransition>> graph = new HashMap<>();

        for (FSMState node : nodes) {
            graph.put(node, new HashSet<>());
        }

        for (FSMTransition edge : edges) {
            graph.get(edge.getSource()).add(edge);
        }
        return graph;
    }

    /**
     * Computes a map of characters to their corresponding destinations.
     *
     * Ignores duplicate characters.
     */
    public static Map<Character, FSMState> makeCharMap(
            Set<FSMTransition> transitions) {
        Map<Character, FSMState> output = new HashMap<>();
        for (FSMTransition arrow : transitions) {
            output.put(arrow.getCharacter(), arrow.getDestination());

        }
        return output;
    }

    /**
     * Indents arbitrary strings by the given prefix.
     */
    public static String indent(String prefix, String text) {
        return String.join(
                System.getProperty("line.separator"), 
                Arrays.stream(text.split("\\r?\\n|\\r"))
                    .map(s -> prefix + s)
                    .collect(Collectors.toList()));
    }

    public static class NFATestException extends RuntimeException {
        public NFATestException(String message) {
            super(message);
        }

        public NFATestException(String message, Throwable ex) {
            super(message, ex);
        }
    }

    /* Represents a mirror or copy of the normally-hidden, internal
     * state of an arbitrary NFA object.
     *
     * This object will accept an NFA object and copy its private
     * state using reflection to make it easier to compare two
     * arbitrary NFA objects on a deep level.
     */
    private static class NFAMirror {
        public Set<FSMState> states;
        public FSMState startState;
        public Set<FSMState> finalStates;
        public Set<FSMTransition> transitions;
        public Map<FSMState, Set<FSMTransition>> graph;

        public NFAMirror(Set<FSMState> states, FSMState startState, 
                Set<FSMState> finalStates, Set<FSMTransition> transitions) {
            this.states = states;
            this.startState = startState;
            this.finalStates = finalStates;
            this.transitions = transitions;
            this.graph = NFAConstructionTester.makeAdjacencyList(this.states, this.transitions);
        }

        @SuppressWarnings("unchecked")
        public NFAMirror(NFA nfa) {
            // If you're using Eclipse, and the following lines are failing to compile,
            // it's probably because you configured Eclipse to treat unchecked generic
            // type operations as errors (most likely because CSE 331 told you to do so
            // earlier this quarter).
            //
            // To fix this, go to Windows >> Preferences and select
            // Java >> Compiler >> Errors/Warnings. Under 'Annotations', enable the
            // "Suppress optional errors with '@SuppressWarnings'" option.
            //
            // This will let you use Eclipse for this assignment while still keeping
            // it in the state you need for CSE 331.
            this.states = (Set<FSMState>) this.getField(nfa, "states");
            this.startState = (FSMState) this.getField(nfa, "startState");
            this.finalStates = (Set<FSMState>) this.getField(nfa, "finalStates");
            this.transitions = (Set<FSMTransition>) this.getField(nfa, "transitions");
            this.graph = NFAConstructionTester.makeAdjacencyList(this.states, this.transitions);
        }

        private Object getField(NFA nfa, String name) {
            try {
                Field field = nfa.getClass().getDeclaredField(name);
                field.setAccessible(true);
                return field.get(nfa);
            } catch (NoSuchFieldException ex) {
                throw new NFATestException(String.format(
                            "NFA class is missing the \"%s\" field.", name), ex);
            } catch (IllegalAccessException ex) {
                throw new NFATestException(String.format(
                            "Unexpected access error with \"%s\" field.", name), ex);
            }
        }

        /**
         * Prints out the state of the mirrored NFA object in human-readable form.
         *
         * More specifically, this method will:
         *
         * 1. Assign each state within this graph a human-readable name, for convenience
         * 2. Print out the contents of nfa.stateState
         * 3. Print out the contents of nfa.finalStates
         * 4. Print out the contents of nfa.graph
         *
         * The contents for nfa.transitions are ignored.
         */
        public String toString() {
            StringBuffer out = new StringBuffer();

            // Give each state a human-readable name
            Map<FSMState, String> niceNames = new HashMap<>();
            List<String> finalStateNames = new ArrayList<>();

            int stateCounter = 0;
            for (FSMState state : this.states) {
                String niceName = "S-" + stateCounter;
                stateCounter += 1;

                niceNames.put(state, niceName);
                if (this.finalStates.contains(state)) {
                    finalStateNames.add(niceName);
                }
            }

            // Print out general info about states
            out.append("NFA contents (states renamed for readability):\n");
            out.append("    nfa.startState:  " + niceNames.get(this.startState) + "\n");
            out.append("    nfa.finalStates: " + finalStateNames.toString() + "\n");
            out.append("    nfa.graph:\n");
            for (FSMState state : this.graph.keySet()) {
                // Print out key
                out.append(String.format("        %s:\n", niceNames.get(state)));

                // Print out values (transitions)
                for (FSMTransition arrow : this.graph.get(state)) {
                    out.append(String.format(
                                "            (%s, %s) -> %s\n", 
                                niceNames.get(arrow.getSource()),
                                arrow.getCharacter(),
                                niceNames.get(arrow.getDestination())));
                }
                if (this.graph.get(state).isEmpty()) {
                    out.append("            [No transitions]\n");
                }
                out.append("\n");
            }
            return out.toString();
        }
    }

    /**
     * A helper class to make building NFAs easier, because
     * life is too short to construct them by hand.
     *
     * To help make building the graph easier, this object will
     * maintain a map of names to FSMStates. This lets us
     * refer to states by name when constructing the graph.
     */
    private static class NFABuilder {
        private Map<String, FSMState> stateNameMap;
        private Set<FSMState> states;
        private FSMState startState;
        private Set<FSMState> finalStates;
        private Set<FSMTransition> transitions;

        // Start building a new graph. (Graph is initially empty).
        public static NFABuilder start() {
            return new NFABuilder();
        }

        public NFABuilder() {
            this.stateNameMap = new HashMap<>();
            this.states = new HashSet<>();
            this.startState = null;
            this.finalStates = new HashSet<>();
            this.transitions = new HashSet<>();
        }

        // Gets the state corresponding to the name (or a new state,
        // if there doesn't exist one).
        private FSMState getByName(String name) {
            if (!this.stateNameMap.containsKey(name)) {
                this.stateNameMap.put(name, new FSMState());
            }
            return this.stateNameMap.get(name);
        }

        public NFABuilder addState(String name) {
            this.states.add(getByName(name));
            return this;
        }

        public NFABuilder addStartState(String name) {
            if (this.startState != null) {
                throw new IllegalStateException("Start state already set");
            }
            this.startState = getByName(name);
            this.states.add(this.startState);
            return this;
        }

        public NFABuilder addAcceptState(String name) {
            FSMState state = getByName(name);
            this.states.add(state);
            this.finalStates.add(state);
            return this;
        }

        public NFABuilder addAcceptStates(String... names) {
            for (String name : names) {
                this.addAcceptState(name);
            }
            return this;
        }

        public NFABuilder addNfaFragment(NFABuilder nfa) {
            this.states.addAll(nfa.states);
            this.finalStates.addAll(nfa.finalStates);
            this.transitions.addAll(nfa.transitions);
            return this;
        }

        public NFABuilder addTransition(FSMTransition arrow) {
            this.states.add(arrow.getSource());
            this.states.add(arrow.getDestination());
            this.transitions.add(arrow);
            return this;
        }

        public NFABuilder addTransition(char c, String start, String end) {
            FSMState startState = getByName(start);
            FSMState endState = getByName(end);

            this.states.add(startState);
            this.states.add(endState);
            this.transitions.add(new FSMTransition(c, startState, endState));
            return this;
        }

        // Takes the entire graph, and converts it into an actual NFA object.
        public NFAMirror toNFA() {
            return new NFAMirror(this.states, this.startState, 
                    this.finalStates, this.transitions);
        }
    }
}
