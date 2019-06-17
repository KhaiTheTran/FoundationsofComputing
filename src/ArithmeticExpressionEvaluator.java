import java.util.*;
import java.io.*;

public class ArithmeticExpressionEvaluator {
    static int INDENT_LEVEL = 0;

    public static void main(String[] args)
	throws FileNotFoundException, 
	       MalformedGrammarException,
	       ExceptionInInitializerError,
           Exception {
        if (args.length != 1) {
            System.out.println("Usage: java ArithmeticExpressionParser <arithmetic expression>");
            System.exit(1);
        }

        String input = args[0];

        /* Build the CFG */
        CFG cfg = new CFG("arithmetic-grammar.cfg");
        EarleyParse.setGrammar(cfg);

        /* Parse The Input String */
        ASTNode parsed = null;
        try {
            parsed = EarleyParse.parse(input);
        } catch (NullPointerException e) {
            System.out.println("Your CFG failed to parse the input " + input + "!!");
            System.exit(1);
        }
            
        System.out.println("Yay!  We parsed the input correctly!  The parse tree looks like this:");

        INDENT_LEVEL = 0;
        printParseTree(parsed);
        INDENT_LEVEL = 0;

        System.out.println("eval(" + input + ") = " + evaluateExpression(parsed));
    }

    public static void printIndentation() {
        for (int i=0; i < INDENT_LEVEL; i++) {
            System.out.print(" ");
        }
    }

    public static void printParseTree(ASTNode n) {
        if (n.isTerminal()) {
            printIndentation();
            System.out.println(n.getValue());
        }
        else {
            printIndentation();
            System.out.println(n.getRuleName() + ": ");
            INDENT_LEVEL += 4;
            for (ASTNode child : n.getChildren()) {
                printParseTree(child);
            }
            INDENT_LEVEL -= 4;
        }
    }
    public static int evaluateExpression(ASTNode n) throws Exception {
        System.out.println("Overall: " + n);
        if (n.isTerminal()) {
            System.out.println("Terminal: " + n);
            String value = n.getValue();
            return Integer.parseInt(value);
        }
        else {
            String name = n.getRuleName();
            if (name.equals("N")) {
                /* Handle Concatenation of Digits */
                int value = Integer.parseInt(n.getLeftChild().getValue());
                int rest = evaluateExpression(n.getRightChild());
                return Integer.parseInt(("" + value) + ("" + rest));
            }
            else if (name.equals("P")) {
                String s = n.toString();
                /* Handle Parentheses */
                if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
                    System.out.println("Parentheses: " + n);
                    return evaluateExpression(n.getChildren()[1]);
                }

                /* Handle Number */
                else if (n.hasOneChild()) {
                    System.out.println("One Child: " + n);
                    return evaluateExpression(n.getChild());
                }
            }
            else if (name.equals("A") || name.equals("M")) {
                String s = n.toString();
                /* Handle Individual Arithmetic Operations */
                if (n.isBinaryOperation()) {
                    String op = n.getChildren()[1].getValue();
                    int left = evaluateExpression(n.getLeftChild());
                    int right = evaluateExpression(n.getRightChild());
                    System.out.println("Binary Operation:" + ("" + left) + op + ("" + right));

                    if (op.equals("+")) {
                        return left + right;
                    }
                    else if (op.equals("*")) {
                        return left * right;
                    }
                    else if (op.equals("-")) {
                        return left - right;
                    }
                    else if (op.equals("/")) {
                        return left / right;
                    }
                }
                else if (n.hasOneChild()) {
                    System.out.println("One Child: " + n);
                    return evaluateExpression(n.getChild());
                }
                else {
                    throw new Exception("Failed to specify what to do for " + n);
                }
            }
            else {
                throw new Exception("Failed to specify what to do for " + name);
            }
        }
        return -1;
    }
}
