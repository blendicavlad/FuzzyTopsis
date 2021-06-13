package algo;

import java.util.ArrayList;
import java.util.List;

public class Alternative {

	private String name;

	private List<IFSInitialValue> values;

	private double eucleidianPositiveDistance;
	private double eucleidianNegativeDistance;

	private double relativeDistanceFromIdealSolution;

	public Alternative(String name) {
		this.name = name;
		values = new ArrayList<>();
	}

	public void computeEucleiianDistances() {

	}

	public void computeRelativeDistanceFromIdealSolution() {

	}

	public void addInitialValue(IFSInitialValue initialValue) {
		values.add(initialValue);
	}

	public String getName() {
		return name;
	}

	public List<IFSInitialValue> getValues() {
		return values;
	}
}
