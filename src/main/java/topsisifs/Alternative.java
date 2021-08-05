package topsisifs;

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

	@Override public String toString() {
		return "Alternative{" + "name='" + name + '\'' + '}';
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Alternative))
			return false;

		Alternative that = (Alternative) o;

		if (Double.compare(that.eucleidianPositiveDistance, eucleidianPositiveDistance) != 0)
			return false;
		if (Double.compare(that.eucleidianNegativeDistance, eucleidianNegativeDistance) != 0)
			return false;
		if (Double.compare(that.relativeDistanceFromIdealSolution, relativeDistanceFromIdealSolution) != 0)
			return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
			return false;
		return getValues() != null ? getValues().equals(that.getValues()) : that.getValues() == null;
	}

	@Override public int hashCode() {
		int result;
		long temp;
		result = getName() != null ? getName().hashCode() : 0;
		result = 31 * result + (getValues() != null ? getValues().hashCode() : 0);
		temp = Double.doubleToLongBits(eucleidianPositiveDistance);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(eucleidianNegativeDistance);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(relativeDistanceFromIdealSolution);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
