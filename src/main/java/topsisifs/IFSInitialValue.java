package topsisifs;

import exceptions.IFSValueException;

public class IFSInitialValue extends IFSValue {

	public IFSInitialValue(Criteria criteria, double μA, double vA) {
		super(criteria, μA, vA);
		validate();
	}

	private void validate() {
//		if (super.getμA() + super.getvA() >= 1) {
//			throw new IFSValueException(IFSValueException.Causes.NOT_LESS_THAN_ONE);
//		}
	}
}
