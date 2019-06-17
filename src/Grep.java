import java.util.*;

//import com.sun.corba.se.spi.orbutil.fsm.State;
//import com.sun.tools.jdeprscan.scan.Scan;

import java.io.*;

public class Grep {
    public static void main(String[] args)
	throws FileNotFoundException, 
	       MalformedGrammarException,
	       ExceptionInInitializerError,
           Exception {
       if (args.length != 2) {
           // System.out.println("Usage: java Grep <regex>");
            System.exit(1);
        }

        String input = args[0];
        

        /* Build the CFG */ 
        CFG cfg = new CFG("regex-grammar.cfg");
        EarleyParse.setGrammar(cfg);

        /* Parse The Input String */
        ASTNode parsed = null;
        try {
            parsed = EarleyParse.parse(input);
        } catch (NullPointerException e) {
            System.out.println("Your CFG failed to parse the input " + input + "!!");
            System.exit(1);
        }
            
       // System.out.println("Yay!  We parsed the input correctly!");

        NFA N = makeNFAFromRegex(parsed);
        
       N= NFA.concat(NFA.star(NFA.dot()),N);
       N = NFA.concat(N, NFA.star(NFA.dot()));
       

        
       Scanner console = new Scanner(new File(args[1]));
       while(console.hasNextLine()) {
    	   String str = console.nextLine();
    	  if( N.read(str)){
    		  System.out.println(str);
    	  }
       } 
       console.close();
        
    }

    public static NFA makeNFAFromRegex(ASTNode n) {
    	
    	HashSet<FSMState> finalStatesE = new HashSet<FSMState>();
    	 String name = n.getRuleName();
    	 
    	//System.out.println("Overall: " + n);
        if (n.isTerminal()) {
            
           if(n.getValue().equals("'")) {
        	   FSMState startState= new FSMState();
        	  Set<FSMState> setStates = new HashSet<FSMState>();
        	  setStates.add(startState);
        	  NFA F = new NFA(setStates,startState,finalStatesE,new HashSet<FSMTransition>());
              return F;
           }else if(n.getValue().equals("-")) {
        	  
              return NFA.dot();
           }else {
        	FSMState   startState1 = new FSMState();
        	FSMState   FinalState1 = new FSMState();
        	Set<FSMState> States = new HashSet<FSMState>();
        	States.add(FinalState1);
        	States.add(startState1);
        	Set<FSMState> finalStates = new HashSet<FSMState>();
        	finalStates.add(FinalState1);
           	FSMTransition transitions = new FSMTransition(n.getValue().charAt(0),startState1,FinalState1);
           	HashSet<FSMTransition> settransitions = new HashSet<FSMTransition>();
           	settransitions.add(transitions);
           	
           
           	NFA F = new NFA(States,startState1,finalStates,settransitions);
            return F;
           }
       
        }
         
           
            if (name.equals("A")) {
                /* Handle union */
            	if(n.hasOneChild()) {
            		return makeNFAFromRegex(n.getChild());
            	}else {
                return NFA.union(makeNFAFromRegex(n.getLeftChild()), makeNFAFromRegex(n.getRightChild()));
            	}
            }
            else
             if (name.equals("M")) {
                /* Handle concat */
            	if (n.hasOneChild()) {
                    return makeNFAFromRegex(n.getChild());
            	}else
            	{
                    return NFA.concat(makeNFAFromRegex(n.getLeftChild()),makeNFAFromRegex(n.getRightChild()));
                }

                /* Handle star */
            }else if (name.equals("F")){
            	if(n.hasOneChild()) {
            		 return makeNFAFromRegex(n.getChild()); 
            	}else {
            		//System.out.println(n);
            		return NFA.star(makeNFAFromRegex(n.getChild())) ;
            	}
            
                /* Handle recur */
            }else if(name.equals("P")) {
            	if(n.hasOneChild()) {
           		 return makeNFAFromRegex(n.getChild()); 
           	}else {
           		return makeNFAFromRegex(n.getChildren()[1]) ;
           	}
            
                 
            }else {
            	 return null;
            } 
   
    
    }
}
