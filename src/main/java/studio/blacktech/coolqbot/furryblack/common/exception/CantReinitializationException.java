package studio.blacktech.coolqbot.furryblack.common.exception;


public class CantReinitializationException extends InitializationException {

	private static final long serialVersionUID = 1L;


	public CantReinitializationException() {

		super();

	}

	public CantReinitializationException(String message) {

		super(message);

	}

	public CantReinitializationException(String message, Throwable cause) {

		super(message, cause);

	}

	public CantReinitializationException(Throwable cause) {

		super(cause);

	}

}
