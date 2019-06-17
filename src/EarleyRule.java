import java.io.*;
import java.util.*;
import java.awt.*;

/** 
 * A class that keeps track of each step in Earley's
 * algorithm for parsing CFG's.
 * Implementation of parsing and Earley's algorithm by Zeph.
 * Other details by Adam.
 * 
 * @author Zeph Grunschlag
 * @author Adam Blank
 **/
public class EarleyRule implements Comparable {
    // THE DEFINING FIELDS
    char LHS;  //left hand side (upper case)
    String[] RHS = new String[2];  // both parts of the production
    int I; // the start index 
    int J; // up to where the parse has succeeded
    EarleyRule leftCreator, rightCreator;
    
    /** return true if parse has succeed to get the RHS
     *  completely to the left of the break point
     */ 
    boolean isDone() {
	    return RHS[1].length()==0;
    }

    /**
     * Rule always starts looking like:
     * A --> (i)(i).XYZ 
     * unless j is specified
     */
    public EarleyRule(char left, String right, int i) {
        LHS = left;
        RHS[1] = right;
        RHS[0] = "";
        J = I = i;
    }

    /**
     * Rule always starts looking like:
     * A --> (i)(i).XYZ 
     * unless j is specified
     */
    public EarleyRule(char left, String right, int i, int j) {
        LHS = left;
        RHS[0] = right.substring(0,j-i);
        RHS[1] = right.substring(j-i);
        I = i;
        J = j;
    }
    
    /** Constructor for copying */
    public EarleyRule(EarleyRule x) {
        LHS = x.LHS;
        RHS[0] = x.RHS[0];
        RHS[1] = x.RHS[1];
        I = x.I;
        J = x.J;
    }
	
    /** Cast from Object array to EarleyRule array **/
    public static EarleyRule[] O2ER(Object[] x) {
        if (x==null) {return null;}
        EarleyRule er[] = new EarleyRule[x.length];
        for(int i=0; i<x.length; i++) {
            er[i] = (EarleyRule)x[i];
        }
        return er;
    }

    public boolean canAdvance(){
	return !isDone() 
	    && J < EarleyParse.INPUT.length()
	    && RHS[1].charAt(0)==EarleyParse.INPUT.charAt(J);
	    }
    
    /** advance forward regardless of the input string */
    public EarleyRule advanceRegardless(){
	EarleyRule temp = new EarleyRule(this);
	if (temp.isDone())
	    return temp;
	temp.J++;
	temp.RHS[0] += temp.RHS[1].charAt(0);
	temp.RHS[1] = temp.RHS[1].substring(1);
	return temp;
    }

    /**for the advance phase of the algorithm */
    public EarleyRule advance(){
	if (!canAdvance())
	    return null;
	return advanceRegardless();
    }

    /** true if the first character of second halp of production is a variable */
    public boolean canClose(){
	return !isDone() && EarleyParse.GRAMMAR.hasVariable(RHS[1].charAt(0)) ;
    }
	
    /**for the closure phase of the algorithm */
    public EarleyRule[] close(){
	if (!canClose() ) // the next letter isn't a variable
	    return null;
	char generator = RHS[1].charAt(0);
	return EarleyParse.getAllLoops(generator, J);
    }

    public boolean canComplete(EarleyRule prefix){
	return isDone() && J>I && I == prefix.J 
	    && !prefix.isDone() && prefix.RHS[1].charAt(0)==LHS;
    }


    /**for the completion phase of the algorithm */
    public EarleyRule complete(EarleyRule prefix){
	if (!canComplete(prefix))
	    return null;
	EarleyRule temp = prefix.advanceRegardless();
	temp.J = J;
	return temp;
    }


/*

    public boolean canAdvance() {
	    return !isDone() 
	           && J < EarleyParse.INPUT.length()
	           && RHS[1].charAt(0)==EarleyParse.INPUT.charAt(J);
	}

    public EarleyRule advance() {
        if (!canAdvance())
            return null;
        return advanceRegardless();
    }
 
    public boolean canComplete(EarleyRule prefix) {
        return isDone() && J>I && I == prefix.J 
            && !prefix.isDone() && prefix.RHS[1].charAt(0)==LHS;
    }

    public EarleyRule complete(EarleyRule prefix) {
        if (!canComplete(prefix))
            return null;
        EarleyRule temp = prefix.advanceRegardless();
        temp.J = prefix.J;
        return temp;
    }

    public boolean canClose() {
	    return !isDone() && EarleyParse.GRAMMAR.hasVariable(RHS[1].charAt(0)) ;
    }

    public EarleyRule[] close() {
        if (!canClose()) // the next letter isn't a variable
            return null;
        char generator = RHS[1].charAt(0);
        return EarleyParse.getAllLoops(generator, J);
    }


    public EarleyRule advanceRegardless() {
        EarleyRule temp = new EarleyRule(this);
        if (temp.isDone())
            return temp;
        temp.J++;
        temp.RHS[0] += temp.RHS[1].charAt(0);
        temp.RHS[1] = temp.RHS[1].substring(1);
        return temp;
    }

*/



    public int compareTo(Object x) {
        EarleyRule y = (EarleyRule)x;

        //Rules in record[J-1] come before rules in record[J]
        if( J != y.J )    
            return J-y.J;

        //Completed rules come first in record[J]
        //Thus they are all contiguous and an extral log(n)
        //Factor can be avoided (if really want to)
        if( (isDone() && !y.isDone()) || (!isDone() && y.isDone()) )
            return RHS[1].length() - y.RHS[1].length(); 
        
        
        //MAY WANT TO REFINE THIS IN THE FUTURE...
        //Two completed rules are compared by rather arbitrary toString()
        if ( isDone() && y.isDone() )
            return toString().compareTo(y.toString());

        //Two uncomplete rules are compared by their first RHS char
        //and then arbitrarily (makes it easy to do advance and closure
        if( RHS[1].charAt(0) != y.RHS[1].charAt(0) )
            return RHS[1].charAt(0) - y.RHS[1].charAt(0);

        //Otherwise, compare arbitarily
        return toString().compareTo(y.toString());
    }

    public boolean equals(Object x) {
	    return compareTo(x) == 0;
    } 

    public String toString() {
	    return ""+LHS+" ---> ("+I+") "+
	    RHS[0] +".("+J+") "+RHS[1];
    }    
}
