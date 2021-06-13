package algo;

public abstract class IFSValue {

	private Criteria criteria;

	private double μA;

	private double vA;

	public IFSValue(Criteria criteria, double μA, double vA) {
		this.criteria = criteria;
		this.μA = μA;
		this.vA = vA;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public double getμA() {
		return μA;
	}

	public void setμA(double μA) {
		this.μA = μA;
	}

	public double getvA() {
		return vA;
	}

	public void setvA(double vA) {
		this.vA = vA;
	}
}
