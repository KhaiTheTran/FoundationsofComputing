/**
 * Thrown when a given Grammar is not correctly defined.
 */
class MalformedGrammarException extends Exception {
    public MalformedGrammarException() {
	    super();
    }

    public MalformedGrammarException(String s) {
	    super(s);
    }
}
