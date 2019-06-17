import java.util.Set;
import java.util.HashSet;

public class NFA {
	protected Set<FSMState> states;
	private FSMState startState;
	private Set<FSMState> finalStates;
	private Set<FSMTransition> transitions;
	private static final char EPSILON = '-';
	private static final char TAB = 9;

	/* Main Constructor */
	public NFA(Set<FSMState> states, FSMState startState, Set<FSMState> finalStates, Set<FSMTransition> transitions) {
		if (!states.contains(startState)) {
			throw new IllegalArgumentException("The start state must be in the NFA.");
		}

		if (!states.containsAll(finalStates)) {
			throw new IllegalArgumentException("The final states must be in the NFA.");
		}

		for (FSMTransition t : transitions) {
			if (!states.contains(t.getSource()) || !states.contains(t.getDestination())) {
				throw new IllegalArgumentException("The transitions may only use states in the NFA.");
			}
		}

		this.states = states;
		this.startState = startState;
		this.finalStates = finalStates;
		this.transitions = transitions;
	}

	/* Copy Constructor */
	public NFA(NFA n) {
		this(new HashSet<FSMState>(n.states), n.startState, new HashSet<FSMState>(n.finalStates),
				new HashSet<FSMTransition>(n.transitions));
	}

	/* @returns a new NFA that accepts any single character. */
	public static NFA dot() {
		HashSet<FSMState> states = new HashSet<FSMState>();
		HashSet<FSMState> finalStates = new HashSet<FSMState>();
		FSMState startState = new FSMState();
		FSMState acceptState = new FSMState();

		states.add(startState);
		states.add(acceptState);

		finalStates.add(acceptState);

		HashSet<FSMTransition> transitions = new HashSet<FSMTransition>();

		for (char c = ' '; c <= '~'; c++) {
			transitions.add(new FSMTransition(c, startState, acceptState));
		}
		transitions.add(new FSMTransition(TAB, startState, acceptState));

		return new NFA(states, startState, finalStates, transitions);
	}

	/* @returns true if and only if the NFA accepts s. */
	public boolean read(String s) {
		/* TODO: You need to implement this! */
		HashSet<FSMState> states = new HashSet<FSMState>();
		states.add(startState);
		for (int i = 0; i < s.length(); i++) {
			HashSet<FSMState> temp_tates = new HashSet<FSMState>();
			for (FSMTransition j : transitions) {
				for (FSMState k : states) {
					if (s.charAt(i) == j.getCharacter() && j.getSource().equals(k)) {
						temp_tates.add(j.getDestination());
					}
				}
			}
			states = temp_tates;
		}
		for (FSMState in : states) {
			for (FSMState f : finalStates) {
				if (in.equals(f)) {
					return true;
				}
			}

		}

		return false;
	}

	/* @returns an NFA which accepts the union of a and b */
	public static NFA union(NFA a, NFA b) {
		/* TODO: You need to implement this! */
		HashSet<FSMState> statesU = new HashSet<FSMState>();

		HashSet<FSMState> finalStatesU = new HashSet<FSMState>();
		HashSet<FSMTransition> transitionsU = new HashSet<FSMTransition>();

		statesU.addAll(a.states);
		finalStatesU.addAll(a.finalStates);
		transitionsU.addAll(a.transitions);
		statesU.addAll(b.states);
		finalStatesU.addAll(b.finalStates);
		transitionsU.addAll(b.transitions);

		FSMState m = new FSMState();

		statesU.add(m);
		FSMTransition transition1 = new FSMTransition(EPSILON, m, a.startState); // m -> a on EPSILON
		FSMTransition transition2 = new FSMTransition(EPSILON, m, b.startState); // m -> b on EPSILON
		

		NFA f = new NFA(statesU, m, finalStatesU, transitionsU);

		HashSet<FSMTransition> epsilonTransitionsU = new HashSet<FSMTransition>();
		epsilonTransitionsU.add(transition2);
		epsilonTransitionsU.add(transition1);
		return epsilonClosure(f, epsilonTransitionsU);
	}

	/* @returns an NFA which accepts the concat of a and b */
	public static NFA concat(NFA a, NFA b) {
		/* TODO: You need to implement this! */
		HashSet<FSMState> statesU = new HashSet<FSMState>();

		
		HashSet<FSMTransition> transitionsU = new HashSet<FSMTransition>();

		statesU.addAll(a.states);
		
		transitionsU.addAll(a.transitions);
		statesU.addAll(b.states);
		
		transitionsU.addAll(b.transitions);

		NFA f = new NFA(statesU, a.startState, b.finalStates, transitionsU);

		HashSet<FSMTransition> epsilonTransitionsU = new HashSet<FSMTransition>();
		for (FSMState fna : a.finalStates) {
			FSMTransition transition1 = new FSMTransition(EPSILON, fna, b.startState);
			epsilonTransitionsU.add(transition1);
		}

		return epsilonClosure(f, epsilonTransitionsU);
	}

	/* @returns an NFA which accepts the Kleene star of a */
	public static NFA star(NFA n) {
		/* TODO: You need to implement this! */
		HashSet<FSMState> statesStar = new HashSet<FSMState>();
		HashSet<FSMState> finalStatesStar = new HashSet<FSMState>();
		
		statesStar.addAll(n.states);
		finalStatesStar.addAll(n.finalStates);
		

		FSMState m = new FSMState();
		statesStar.add(m);
		finalStatesStar.add(m);

		FSMTransition transition1 = new FSMTransition(EPSILON, m, n.startState);

		NFA f = new NFA(statesStar, m, finalStatesStar, n.transitions);
		HashSet<FSMTransition> epsilonTransitionsU = new HashSet<FSMTransition>();
		for (FSMState fna : n.finalStates) {
			FSMTransition transition2 = new FSMTransition(EPSILON, fna, n.startState);
			epsilonTransitionsU.add(transition2);
		}
		epsilonTransitionsU.add(transition1);
		return epsilonClosure(f, epsilonTransitionsU);

	}

	/*
	 * @returns an NFA which is equivalent to n (including all transitions in
	 * epsilonTransition) that does not contain any epsilon transitions
	 */
	public static NFA epsilonClosure(NFA n, Set<FSMTransition> epsilonTransitions) {
		/* TODO: You need to implement this! */

		Set<FSMTransition> transitionsOld = new HashSet<FSMTransition>();
		Set<FSMTransition> curr = epsilonTransitions;
		while (!transitionsOld.equals(curr)) {

			transitionsOld.addAll(curr);
			curr.clear();
			
			curr.addAll(transitionsOld);

			for (FSMTransition ab : transitionsOld) {

				for (FSMTransition bc : transitionsOld) {
					
					if (bc.getSource().equals(ab.getDestination())) {
						
						FSMTransition transitionsTemp = new FSMTransition(EPSILON, ab.getSource(), bc.getDestination());

						curr.add(transitionsTemp);
					}
				}
			}

		}
		
		//part two
		Set<FSMTransition> newtransitions = new HashSet<FSMTransition>();
		for (FSMTransition ab : curr) {

			
			for (FSMTransition bc : n.transitions) {
				if (bc.getSource().equals(ab.getDestination())) {
					
					newtransitions.add(new FSMTransition(bc.getCharacter(), ab.getSource(), bc.getDestination()));
				}
			}
			if (n.finalStates.contains(ab.getDestination())) {
				n.finalStates.add(ab.getSource());
			}

			n.transitions.addAll(newtransitions);
		}

		
		return n;
	}
}
