/**     This class encapsulates Context Free Grammars. 
 *      Zeph Grunschlag created this class for use in JavaCFG.
 *      Yoav Hirsch added the grammar conversion functionality.
 *
 *      a grammar file is specified as follows:
 *         o  Each line consists of white-space separated strings which
 *         o  The first string of any line must have length 1 and is a variable
 *         o  Subsequent strings on a given line are the right hand sides
 *                of productions from the variable for that line
 *         o  The first variable in the file is the start variable
 *         o  A single double-quote symbol " represents the empty
 *                string epsilon 
 *                (we think of " as two single quotes with nothing in between)
 *         o  Any character which isn't a variable, is a terminal
 *
 *     
 *      Conversion Instructions:  There are three possible flags which may not be
 *      combined at the moment:  -removeEpsilons, -removeUnits, -makeCNF.
 *      If no flags are specified, the program tries to create a CFG and gives some
 *      information.
 * 
 *      1)  -removeEpsilons  -- given an arbitrary grammar, modify the grammar to an
 *                             equivalent grammar with only one allowable epsilon
 *                             transtion:  start ---> epsilon
 *      2)  -removeUnits     -- given a grammar with no epsilon transitions, except possibly
 *                             from the start variable, modify the grammar to an equivalent
 *                             grammar with no unit transitions
 *      3)  -makeCNF         -- given a grammar with no epsilon transitions and no unit
 *                             transitions, convert into an equivalent grammar in Chomsky
 *                             Normal Form
 *
 *      removeEpsilons() is tested by hasNoEpsilonsExceptForStart(),
 *      removeUnits() is tested by hasUnitTransitions(), and
 *      makeCNF() is tested by isChomskyNormalFormGrammar()
 *
 * @author Zeph Grunschlag
 * @author Yoav Hirsch
 * @author Adam Blank
 * @author Michael Lee
 * @version 0
 ****/


import java.util.*;
import java.io.*;

/**
 * A class that keeps track of general
 * Context Free Grammars
 */
public class CFG {
    char start;                 // start variable
    SortedSet<Rule> ruleSet;    // keep the rules sorted
    Map<Character, List<String>> ruleMap; 
    Set<Character> variableSet; 

    /** Returns true if char represents a variable */
    public boolean hasVariable(char c) {
	    return variableSet.contains(c);
    }

    public CFG(String file) throws FileNotFoundException, MalformedGrammarException {
	    ruleSet = new TreeSet<>();
	    parseFile(file);
	    makeRuleMap();
    }

    /**
     * The first method that needs to be called after a constructor
     */
    public void parseFile(String fname) 
    	        throws FileNotFoundException, MalformedGrammarException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(fname);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringTokenizer tokens;

            int line_num = 0;
            char currVar;
            variableSet = new TreeSet<>();
            
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) continue; //when line ends immediately in '\n'
                tokens = new StringTokenizer(line, " \t");
                if (!tokens.hasMoreTokens()) { // empty line
                    continue;
                }

                // get the variable
                String variable = tokens.nextToken();
                if (variable.length() != 1)
                    throw new MalformedGrammarException();
                currVar = variable.charAt(0);
                variableSet.add(currVar);

                // start variable is at the beginning of the file
                if (line_num == 0)
                    start = currVar;

                // get the productions
                while (tokens.hasMoreTokens()) {
                    String prod = tokens.nextToken();
                    if (prod.equals("\"")) // epsilon production 
                        ruleSet.add(new Rule(currVar, "")); 
                    else if (prod.indexOf('\"') != -1) {
                        throw new MalformedGrammarException();
                    }
                    else
                        ruleSet.add(new Rule(currVar, prod));
                }
                line_num++;
            }
            fis.close();
        } catch(IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new FileNotFoundException();
	        }
	        throw new MalformedGrammarException();
	    } catch (NullPointerException e) {
		    e.printStackTrace();
		    throw new MalformedGrammarException();
	    }
    }

    /**
     * This method resets the ruleMap and variableSet
     * in accordance with the ruleSet
     */
    private void makeRuleMap() {
        // initialize the ruleMap
        ruleMap = new HashMap<>();
        for (char curr : variableSet) {
            ruleMap.put(curr, new ArrayList<>());
        }

        // fill the ruleMap	    
        for (Rule r : ruleSet) {
            ruleMap.get(r.LHS).add(r.RHS);
        }
    }	
}
