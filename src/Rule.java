/** 
 * A class that keeps track of rules
 * for a CFG
 */
class Rule implements Comparable{
    char LHS;  //left hand side (upper case)
    String RHS;
    
    public Rule(char left, String right){
	LHS = left;
	RHS = right;
    }
    
    
    /** for implementing Comparable **/
    public int compareTo(Object x){
	Rule y = (Rule)x;
	if( LHS < y.LHS ) return -1;
	if( LHS > y.LHS ) return 1;
	return(RHS.compareTo(y.RHS));
    }
	
    public boolean equals(Object x){
	Rule y = (Rule)x;
	return LHS==y.LHS && RHS.equals(y.RHS);
    } 

    public boolean isUnitRule(){
	return RHS.length()==1;
    }

    public boolean isEpsilonRule(){
	return RHS.length()==0;
    }

    public Rule copyAndReplaceAll(char oldSymbol, String newSymbol) {
        int occurrences = 0;

        int length = RHS.length();
        StringBuffer buff = new StringBuffer();
        char c;
        for(int i=0;i<length;i++) {
            c = RHS.charAt(i);
            if (c == oldSymbol) {
                buff.append(newSymbol);
                occurrences ++;
            }
            else buff.append(c);
        }

        if(occurrences > 0) return new Rule(LHS,buff.toString());
        return null;
    } 
    
    public String getRHS() {
	    return RHS;
    }
   
    public void setRHS(String RHS) {
	    this.RHS = RHS;
    }
    
    public char getLHS() {
	    return LHS;
    }

   
    /* Test to see a rule is just symbol deriving itself */
    public boolean isReflexiveRule() {
	    return (isUnitRule() && LHS == RHS.charAt(0));
    }
    
    public String toString() {
	    return LHS + " -> " + (isEpsilonRule()?"<epsilon>":RHS);
    }
}
