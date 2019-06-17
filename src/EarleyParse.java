import java.util.*;

public class EarleyParse {
    static String INPUT;  //The input String to parse
    static CFG GRAMMAR;   //The grammar to parse with
    static List<SortedSet<EarleyRule>> RECORD; // Array of TreeSets for EarleyRule's
    
    public static ASTNode parse(String x) {
        return parseTree(earleyParse(x));
    }

    public static void setGrammar(CFG g) {
        GRAMMAR = g;
    }

    private static EarleyRule earleyParse(String x) {
        INPUT = x;
        RECORD = new ArrayList<>();
        for (int i = 0; i < INPUT.length() + 1; i++) {
            RECORD.add(null);
        }
        EarleyRule[] temp = getAllLoops(GRAMMAR.start, 0);
        RECORD.set(0, new TreeSet<>(Arrays.asList(temp)));

        for (int j = 0; j < INPUT.length();) {
            closeEarley(j);
            advanceEarley(j++);
            completeEarley(j);
        }

        for (EarleyRule er : RECORD.get(INPUT.length())) {
            if (er.isDone() && er.I == 0 && er.LHS == GRAMMAR.start) {
                return er;
            }
        }
        return null;
    }

    public static EarleyRule[] getAllLoops(char left, int j) {
        List<String> al = GRAMMAR.ruleMap.get(left);
        int numRules = al.size();
        EarleyRule[] temp = new EarleyRule[numRules];
        for (int i = 0; i < numRules; i++) {
            String s = al.get(i);
            temp[i] = new EarleyRule(left, s, j);
        }
        return temp;	
    }

    public static void closeEarley(int j) {
        Deque<EarleyRule> q = new LinkedList<>(RECORD.get(j));
        while (q.size() != 0) {
            EarleyRule er = q.removeFirst();  //dequeue
            EarleyRule[] c = er.close();
            if (c == null) continue;
            for (int k = 0; k < c.length; k++) {
                if(RECORD.get(j).add(c[k])) { //true if rule was newly created
                    q.add(c[k]);          //then enqueue
                    c[k].leftCreator = er;
                }
            }
        }	
    }
    
    public static void advanceEarley(int j) {
        RECORD.set(j + 1, new TreeSet<>());
        EarleyRule[] prev = EarleyRule.O2ER(RECORD.get(j).toArray());
        for (int k = 0; k < prev.length; k++) {
            EarleyRule next = prev[k].advance();
            if (next==null) continue;
            RECORD.get(j + 1).add(next);
            next.leftCreator = prev[k];
        }
    }
     
    public static void completeEarley(int j) {
        Deque<EarleyRule> q = new LinkedList<>(RECORD.get(j));

        while (q.size() != 0) {
            EarleyRule suffix = q.removeFirst();  //dequeue
            if (!suffix.isDone()) continue;
            for (EarleyRule prefix : RECORD.get(suffix.I)) {
                EarleyRule combination = suffix.complete(prefix);
                if (combination == null) continue;
                if (RECORD.get(j).add(combination)) {
                    q.add(combination);
                    combination.leftCreator = prefix;
                    combination.rightCreator = suffix;
                }
            }
        }
    }

    private static ASTNode parseTree(EarleyRule node) {
        String childstr = node.RHS[0];
        EarleyRule itr = node;

        ASTNode[] children = new ASTNode[childstr.length()];
        for (int i = childstr.length() - 1; i >= 0; i--) {
            char c = childstr.charAt(i);
            // first case, variables.  So right creator exists...
            if (GRAMMAR.hasVariable(c)) {
                children[i] = parseTree(itr.rightCreator);
            }
            else {//must be a terminal
                children[i] = new ASTNode("" + c);
            }
            itr = itr.leftCreator;
        }
        return new ASTNode("" + node.LHS, children);
    }
}
