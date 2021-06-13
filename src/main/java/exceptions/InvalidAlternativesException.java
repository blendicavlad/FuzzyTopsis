package exceptions;

import java.util.Objects;

public class InvalidAlternativesException extends RuntimeException {

	private Causes cause;

	public InvalidAlternativesException(Causes cause) {
		super();
		Objects.requireNonNull(cause);
		this.cause = cause;
	}

	public enum Causes {
		EMPTY("Lista de alternative este goala");

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
