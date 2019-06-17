import java.io.FileNotFoundException;

public class RegexCFGTester {
    public static void main(String[] args) {
        String[] testCases = {"aa*", "a|b|c**(ab)a*", 
                              "(a|b|c*)**aabb",
                              "ab0|'", "a((((1))))c|'", "a||b"};
        String[] answers   = {"(a(a*))", "((a|b)|((((c*)*)((ab)))(a*)))", 
                              "(((((((((a|b)|(c*)))*)*)a)a)b)b)",
                              "(((ab)0)|')", "(((a((((1)))))c)|')", null};

        for (int i=0; i < testCases.length; i++) {
            String student = testParser(testCases[i]);
            if ((answers[i] != null || student != null) && ((answers[i] == null && student != null) || (answers[i] != null && student == null) || !student.equals(answers[i]))) {
                System.out.println("Uh Oh!  You don't do the right thing on " + testCases[i] + ".\nThe answer was \"" + answers[i] + "\", but you gave \"" + student + "\".");
                System.exit(1);
            }
        }
        System.out.println("Your regular expression CFG looks good!  Nice job!");
    }

    public static String parenthesizeRegex(ASTNode n) {
        if (n.isTerminal()) {
            if (n.getValue().equals("(") ||
                n.getValue().equals(")")) {
                return "";
            }
            return n.getValue();
        }
        else if (n.hasOneChild()) {
            return parenthesizeRegex(n.getLeftChild());
        }
        else {
            String output = "";
            for (ASTNode child : n.getChildren()) {
                output += parenthesizeRegex(child);
            }
            return "(" + output + ")";
        }
    }

    public static String testParser(String regex) {
        /* Build the CFG */ 
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
                        System.err.println("ERROR: Make sure your regex-grammar.cfg file is either in the task_1_tests directory or the src directory.");
                        System.exit(1);
                    }
                }
            }        
        } catch (MalformedGrammarException e3) {
            System.err.println("ERROR: The CFG you wrote does not parse correctly.  Make sure you read the instructions in the spec carefully.");
            System.exit(1);
        }
        EarleyParse.setGrammar(cfg);

        /* Parse The Input String */
        ASTNode parsed = null;
        try {
            ASTNode parsedRegex = EarleyParse.parse(regex);
            return parenthesizeRegex(parsedRegex);
        } catch (NullPointerException e) {
            /* We should fail precisely when the correct string is null. */
            return null;
        }
    }
}
