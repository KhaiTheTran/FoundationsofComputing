import java.util.Objects;

/* Note that FSMTransition is IMMUTABLE like Strings in Java.
 * When you want to "edit" the transition, you should create a new
 * one instead.  (Think back to binary trees and ch inventory from
 * 143.)
 */
public class FSMTransition {
    private char ch;
    private FSMState source;
    private FSMState destination;

    public FSMTransition(char c, FSMState source, FSMState destination) {
        this.ch = c;
        this.source = source;
        this.destination = destination;
    }

    public char getCharacter() {
        return this.ch;
    }

    public FSMState getSource() {
        return this.source;
    }

    public FSMState getDestination() {
        return this.destination;
    }

    public boolean equals(Object other) {
        if (!(other instanceof FSMTransition)) { 
            return false;
        }
        FSMTransition oth = (FSMTransition)other;
        return this.ch == oth.ch &&
               this.source.equals(oth.source) &&
               this.destination.equals(oth.destination);
    }

    public int hashCode() {
        return Objects.hash(this.ch, this.source, this.destination);
    }


    public String toString() {
        return "(" + this.source + ", " + this.ch + ") -> " + this.destination;
    }
}
