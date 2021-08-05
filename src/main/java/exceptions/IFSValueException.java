package exceptions;

import java.util.Objects;

public class IFSValueException extends RuntimeException {
	private IFSValueException.Causes cause;

	public IFSValueException(IFSValueException.Causes cause) {
		super();
		Objects.requireNonNull(cause);
		this.cause = cause;
	}

	public enum Causes {
		NOT_LESS_THAN_ONE("Suma dintre μA si vA trebuie sa fie <= 1");

		private String cause;

		Causes(String cause) {
			this.cause = cause;
		}

		@Override public String toString() {
			return this.cause;
		}
	}

	@Override public String getMessage() {
		return this.cause.toString();
	}
}
