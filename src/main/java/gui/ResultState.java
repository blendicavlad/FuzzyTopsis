package gui;

import topsisifs.TopsisIFS;

public class ResultState {

	private static TopsisIFS.ComputedAlternative[] state = null;

	public static TopsisIFS.ComputedAlternative[] getState() {
		return state;
	}

	public static void setState(TopsisIFS.ComputedAlternative[] state) {
		ResultState.state = state;
	}
}
