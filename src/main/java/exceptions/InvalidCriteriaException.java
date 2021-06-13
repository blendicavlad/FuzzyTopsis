package exceptions;

import java.util.Objects;

public class InvalidCriteriaException extends RuntimeException {

	private Causes cause;

	public InvalidCriteriaException(Causes cause) {
		super();
		Objects.requireNonNull(cause);
		this.cause = cause;
	}

	public enum Causes {
		EMPTY("Lista de criterii este goala"),
		COUNT_INCONSISTENCY("Numarul de criterii trebuie sa fie la fel pentru toate alternativele");

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
