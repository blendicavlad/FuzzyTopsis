package topsisifs;

public class Criteria {

	private String name;

	private double weight;

	public Criteria(String name, double weight) {
		this.name = name;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	@Override public String toString() {
		return "Criteria{" + "name='" + name + '\'' + ", weight=" + weight + '}';
	}
}
