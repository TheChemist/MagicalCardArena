package de.mca.io;

/**
 *
 * @author Maximilian Werling
 *
 */
@SuppressWarnings("serial")
public class ResourceReadingException extends RuntimeException {
	public ResourceReadingException(Throwable cause) {
		this(cause, "");
	}

	public ResourceReadingException(Throwable cause, String fileName) {
		super("Resource konnte nicht gelesen werden: " + fileName);
	}
}
