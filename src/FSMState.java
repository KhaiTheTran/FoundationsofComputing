public class FSMState {
    /* Surprisingly, we don't actually need to put anything in this class.
     * The only reason we need an FSMState class is to refer uniquely to
     * states, which Java does for us automatically by Object reference. */

    public String toString() {
        return "S-" + Integer.toString(this.hashCode());
    }
}
